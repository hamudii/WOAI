package request

type ActivityCreateRequest struct {
	UserId      uint   `json:"user_id" validate:"required"`
	Type        string `json:"type" validate:"required"`
	Total       int32  `json:"total" validate:"required"`
	Correct     int32  `json:"correct" validate:"required"`
	Incorrect   int32  `json:"incorrect" validate:"required"`
	Duration    int32  `json:"duration" validate:"required"`
	Description string `json:"description" validate:"required"`
}
