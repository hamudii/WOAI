package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"rest-api-go/database"
	"rest-api-go/model/entity"
	"rest-api-go/model/request"
)

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
			"message": "failed",
			"error":   errValidate.Error(),
		})
	}
	//var passwordHashed = hashPassword(loginRequest.PasswordHash)
	var user entity.User
	err := database.DB.First(&user, "email = ?", loginRequest.Email).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "user not found",
		})
	}

	return ctx.JSON(fiber.Map{
		"token": "thisissecret",
	})
}
