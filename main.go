package main

import (
	"github.com/gofiber/fiber/v2"
	"rest-api-go/database"
	"rest-api-go/route"
)

func main() {
	//INITIALZE DATABASE
	database.DatabaseInit()
	//RUNNING MIGRATION
	//migration.RunMigration()

	app := fiber.New()
	route.RouteInit(app)
	app.Listen(":8000")
}
