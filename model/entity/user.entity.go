package entity

import (
	"gorm.io/gorm"
	"time"
)

type User struct {
	ID           uint           `json:"id" gorm:"primaryKey"`
	Name         string         `json:"name"`
	Email        string         `json:"email"`
	Height       int32          `json:"height"`
	Weight       int32          `json:"weight"`
	PasswordHash string         `json:"password_hash"`
	Session      string         `json:"session"`
	CreatedAt    time.Time      `json:"created_at"`
	UpdatedAt    time.Time      `json:"updated_at"`
	DeletedAt    gorm.DeletedAt `json:"deleted_at"  gorm:"index"`
}
