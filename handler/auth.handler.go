package handler

import (
	"github.com/dgrijalva/jwt-go"
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
	"rest-api-go/database"
	"rest-api-go/middleware"
	"rest-api-go/model/entity"
	"rest-api-go/model/request"
	"rest-api-go/utils"
)

// CREATE A USER/REGISTER
func UserHandlerCreate(ctx *fiber.Ctx) error {
	user := new(request.UserCreateRequest)
	if err := ctx.BodyParser(user); err != nil {
		return err
	}

	validate := validator.New()
	errValidate := validate.Struct(user)

	if errValidate != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "your data is not valid",
			"error":   errValidate,
		})
	}

	var existingUser entity.User
	result := database.DB.Where("email = ?", user.Email).First(&existingUser)
	if result.RowsAffected > 0 {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "Email already exists",
		})
	}

	newUser := entity.User{
		Name:         user.Name,
		Email:        user.Email,
		Height:       user.Height,
		Weight:       user.Weight,
		PasswordHash: hashPassword(user.PasswordHash),
	}

	errCreateUser := database.DB.Create(&newUser).Error
	if errCreateUser != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "failed to store data",
			"error":   errCreateUser,
		})
	}

	return ctx.JSON(fiber.Map{
		"message": "user successfully created",
		"name":    user.Name,
		"email":   user.Email,
		"height":  user.Height,
		"weight":  user.Weight,
	})
}

// LOGIN
func UserHandlerLogin(ctx *fiber.Ctx) error {
	loginRequest := new(request.UserLoginRequest)
	if err := ctx.BodyParser(loginRequest); err != nil {
		return err
	}
	//VALIDATING REQUEST
	validate := validator.New()
	errValidate := validate.Struct(loginRequest)
	if errValidate != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "please input valid data",
		})
	}
	//VALIDATING USER'S EMAIL AND PASSWORD
	var user entity.User
	err := database.DB.First(&user, "email = ?", loginRequest.Email).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "wrong email/password",
		})
	}

	//VALIDATING USER'S PASSWORD
	storedHash := []byte(user.PasswordHash)
	errPass := bcrypt.CompareHashAndPassword(storedHash, []byte(loginRequest.PasswordHash))
	if errPass != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "wrong email/password",
		})
	}

	//GENERATE JWT
	uniqueID := uuid.New().String()

	claims := jwt.MapClaims{}
	claims["id"] = user.ID
	claims["name"] = user.Name
	claims["email"] = user.Email
	claims["unique_id"] = uniqueID

	token, errGenerateToken := utils.GenerateToken(&claims)
	if errGenerateToken != nil {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "unauthorized",
		})
	}

	return ctx.JSON(fiber.Map{
		"token": token,
		"id":    user.ID,
		"email": user.Email,
		"name":  user.Name,
	})
}

func LogoutHandler(ctx *fiber.Ctx) error {
	// Extract the token from the request headers
	token := ctx.Get("x-token") // Assuming the token is in the Authorization header

	// Add the token to the blacklist
	middleware.AddToBlacklist(token)

	return ctx.JSON(fiber.Map{
		"message": "Logout successful",
	})
}
