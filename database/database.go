package database

import (
	"fmt"
	"github.com/joho/godotenv"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"os"
)

var DB *gorm.DB

func DatabaseInit() {
	errLoad := godotenv.Load()
	if errLoad != nil {
		panic("Failed to load ENV")
	}

	var err error
	name := os.Getenv("DATABASE_NAME")
	username := os.Getenv("DATABASE_USERNAME")
	password := os.Getenv("DATABASE_PASSWORD")
	url := os.Getenv("DATABASE_URL")
	var MYSQL = username + ":" + password + "@tcp(" + url + ":3306)/" + name + "?charset=utf8mb4&parseTime=True&loc=Local"
	dsn := MYSQL
	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})

	if err != nil {
		panic("CANT CONNECT TO DATABASE")
	}

	fmt.Println("YOU'RE CONNECTED TO DATABASE")

}
