package entity

import (
	"gorm.io/gorm"
	"time"
)

type Activity struct {
	ID        uint           `json:"id" gorm:"primaryKey"`
	UserId    uint           `json:"user_id"`
	Type      string         `json:"type"`
	Correct   string         `json:"correct"`
	Incorrect string         `json:"incorrect"`
	CreatedAt time.Time      `json:"created_at"`
	DeletedAt gorm.DeletedAt `json:"deleted_at"  gorm:"index"`
}
