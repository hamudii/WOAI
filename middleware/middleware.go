package middleware

import (
	"github.com/gofiber/fiber/v2"
	"rest-api-go/utils"
)

var TokenBlacklist = make(map[string]struct{})

func Auth(ctx *fiber.Ctx) error {
	token := ctx.Get("x-token")

	//CHECK IF THERE'S NO TOKEN
	if token == "" {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "unauthorized",
		})
	}

	if _, exists := TokenBlacklist[token]; exists {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "Token is blacklisted. Please log in with valid credentials.",
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

func AddToBlacklist(token string) {
	TokenBlacklist[token] = struct{}{}
}
