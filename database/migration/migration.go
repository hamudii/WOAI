package migration

import (
	"fmt"
	"log"
	"rest-api-go/database"
	"rest-api-go/model/entity"
)

func RunMigration() {
	err := database.DB.AutoMigrate(&entity.Activity{})
	if err != nil {
		log.Println(err)
	}
	fmt.Println("Database Migrated")
}
