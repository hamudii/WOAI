package request

import "gorm.io/gorm"

type UserCreateRequest struct {
	Name         string `json:"name" validate:"required"`
	Email        string `json:"email" validate:"required"`
	Height       int32  `json:"height" validate:"required"`
	Weight       int32  `json:"weight" validate:"required"`
	PasswordHash string `json:"password_hash" validate:"required"`
}
type UserUpdateRequest struct {
	Name   string `json:"name"`
	Email  string `json:"email"`
	Height int32  `json:"height"`
	Weight int32  `json:"weight"`
}

type UserDeleteRequest struct {
	DeletedAt gorm.DeletedAt `json:"deleted_at"`
}

type UserLoginRequest struct {
	Email        string `json:"email" validate:"required,email"`
	PasswordHash string `json:"password_hash" validate:"required"`
}
