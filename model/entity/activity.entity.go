package entity

import (
	"gorm.io/gorm"
	"time"
)

type Activity struct {
	ID          uint           `json:"id" gorm:"primaryKey"`
	UserId      uint           `json:"user_id"`
	Type        string         `json:"type"`
	Total       int32          `json:"total"`
	Correct     int32          `json:"correct"`
	Incorrect   int32          `json:"incorrect"`
	Duration    int32          `json:"duration"`
	Description string         `json:"description"`
	CreatedAt   time.Time      `json:"created_at"`
	DeletedAt   gorm.DeletedAt `json:"deleted_at"  gorm:"index"`
}
