package route

import (
	"github.com/gofiber/fiber/v2"
	"rest-api-go/handler"
	"rest-api-go/middleware"
)

func RouteInit(r *fiber.App) {
	//r.Get("/users", handler.UserHandlerGetAll)
	r.Get("/", handler.UserHandlerWelcome)
	r.Post("/register", handler.UserHandlerCreate)
	r.Post("/login", handler.UserHandlerLogin)
	r.Post("/logout", middleware.Auth, handler.LogoutHandler)

	r.Get("/user/:id", middleware.Auth, handler.UserHandlerGetSpecific)
	r.Put("/user/:id", middleware.Auth, handler.UserHandlerUpdate)
	r.Delete("/user/:id", middleware.Auth, handler.UserHandlerDelete)

	r.Get("/activity/:id", middleware.Auth, handler.ActivityHandlerGet)
	r.Post("/activity", middleware.Auth, handler.ActivityHandlerCreate)
	r.Delete("/activity/:id", middleware.Auth, handler.ActivityHandlerDelete)
	r.Put("/activity/:id", middleware.Auth, handler.ActivityUpdateHandler)
}
