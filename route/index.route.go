package route

import (
	"github.com/gofiber/fiber/v2"
	"rest-api-go/handler"
)

//SESSION
//func middleware(ctx *fiber.Ctx) error {
//
//}

func RouteInit(r *fiber.App) {
	r.Get("/users", handler.UserHandlerGetAll)

	r.Post("/register", handler.UserHandlerCreate)
	r.Post("/login", handler.UserHandlerLogin)

	r.Get("/user/:id", handler.UserHandlerGetSpecific)
	r.Put("/user/:id", handler.UserHandlerUpdate)
	r.Delete("/user/:id", handler.UserHandlerDelete)
}
