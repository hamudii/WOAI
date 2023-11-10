package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"golang.org/x/crypto/bcrypt"
	"log"
	"rest-api-go/database"
	"rest-api-go/model/entity"
	"rest-api-go/model/request"
	"strings"
	"time"
)

// HASHING PASSWORD
func hashPassword(password string) string {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		log.Println(err)
		return ""
	}
	return string(hashedPassword)
}

// GETTING ALL USERS - DONE
func UserHandlerGetAll(ctx *fiber.Ctx) error {
	var users []entity.User
	result := database.DB.Where("deleted_at IS NULL").Find(&users)

	if result.Error != nil {
		log.Println(result.Error)
	}

	var userData []fiber.Map

	// Iterate through the users and extract name and email
	for _, user := range users {
		userData = append(userData, fiber.Map{
			"id":         user.ID,
			"name":       user.Name,
			"email":      user.Email,
			"height":     user.Height,
			"weight":     user.Weight,
			"created_at": user.CreatedAt,
			"updated_at": user.UpdatedAt,
		})
	}

	return ctx.JSON(fiber.Map{
		"message": "Data successfully retrieved",
		"data":    userData,
	})
}

// CREATE A USER/REGISTER
func UserHandlerCreate(ctx *fiber.Ctx) error {
	user := new(request.UserCreateRequest)
	if err := ctx.BodyParser(user); err != nil {
		return err
	}

	// Check if the email already exists in the database
	var existingUser entity.User
	result := database.DB.Where("email = ?", user.Email).First(&existingUser)
	if result.RowsAffected > 0 {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "Email already exists",
		})
	}

	validate := validator.New()
	errValidate := validate.Struct(user)

	if errValidate != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "failed to store data",
			"error":   errValidate,
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
		"message": "User successfully created",
		"name":    user.Name,
		"email":   user.Email,
		"height":  user.Height,
		"weight":  user.Weight,
	})
}

//func UserHandlerLogin(ctx *fiber.Ctx) error {
//	user := new(request.UserLoginRequest)
//	if err := ctx.BodyParser(user); err != nil {
//		return err
//	}
//
//	validate := validator.New()
//	errValidate := validate.Struct(user)
//
//	if errValidate != nil {
//		return ctx.Status(400).JSON(fiber.Map{
//			"message": "failed to store data",
//			"error":   errValidate,
//		})
//	}
//
//	return nil
//}

// GETTING A SPECIFIC USER
func UserHandlerGetSpecific(ctx *fiber.Ctx) error {
	// Get the name parameter from the URL
	id := ctx.Params("id")

	var users []entity.User
	result := database.DB.Where("id = ?", id).First(&users)

	if result.Error != nil {
		// Check if the error message contains "record not found"
		if strings.Contains(result.Error.Error(), "record not found") {
			return ctx.Status(404).JSON(fiber.Map{
				"message": "User not found",
			})
		}

		log.Println(result.Error)
		return ctx.Status(500).JSON(fiber.Map{
			"message": "An error occurred",
		})
	}

	var userData []fiber.Map
	for _, user := range users {
		heightInMeters := float32(user.Height) / 100.0
		bmi := float32(user.Weight) / (heightInMeters * heightInMeters)
		userData = append(userData, fiber.Map{
			"id":         user.ID,
			"name":       user.Name,
			"email":      user.Email,
			"height":     user.Height,
			"weight":     user.Weight,
			"created_at": user.CreatedAt,
			"updated_at": user.UpdatedAt,
			"bmi":        bmi,
		})
	}

	return ctx.JSON(fiber.Map{
		"message": "Data successfully retrieved",
		"data":    userData,
	})
}

// EDIT DATA SPECIFIC USER
func UserHandlerUpdate(ctx *fiber.Ctx) error {
	userRequest := new(request.UserUpdateRequest)
	if err := ctx.BodyParser(userRequest); err != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "Bad Request",
		})
	}
	userId := ctx.Params("id")
	var users entity.User

	//CHECK USER EXISTENCE
	err := database.DB.First(&users, "id = ?", userId).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "User not found",
		})
	}

	//UPDATE DATA
	if userRequest.Name != "" {
		users.Name = userRequest.Name
	}

	if userRequest.Email != "" {
		users.Email = userRequest.Email
	}

	//SPECIAL CONDITIONS FOR INT VALUE

	if userRequest.Height != 0 && userRequest.Height > 0 {
		users.Height = userRequest.Height
	}

	if userRequest.Weight != 0 && userRequest.Weight > 0 {
		users.Weight = userRequest.Weight
	}

	users.UpdatedAt = time.Now()

	errUpdate := database.DB.Save(&users).Error
	if errUpdate != nil {
		return ctx.Status(500).JSON(fiber.Map{
			"message": "internal server error",
		})
	}

	return ctx.JSON(fiber.Map{
		"message": "Data successfully updated",
	})
}

// DELETE DATA SPECIFIC USER
func UserHandlerDelete(ctx *fiber.Ctx) error {
	userId := ctx.Params("id")
	var user entity.User

	//CHECKING USER ACCOUNT EXISTENCE
	err := database.DB.Debug().First(&user, "id = ?", userId).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "User not found",
		})
	}
	errDelete := database.DB.Debug().Delete(&user).Error
	if errDelete != nil {
		return ctx.Status(500).JSON(fiber.Map{
			"message": "internal server error",
		})
	}

	return ctx.Status(200).JSON(fiber.Map{
		"message": "User successfully deleted",
	})
}
