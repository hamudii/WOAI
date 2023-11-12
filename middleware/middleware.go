package middleware

import (
	"fmt"
	"github.com/gofiber/fiber/v2"
	"rest-api-go/utils"
)

func Auth(ctx *fiber.Ctx) error {
	token := ctx.Get("x-token")
	fmt.Println(token)
	//CHECK IF THERE'S NO TOKEN
	if token == "" {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "unauthorized",
		})
	}

	_, err := utils.VerifyToken(token)
	if err != nil {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "unauthorized",
		})
	}
	return ctx.Next()
}
