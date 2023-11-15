package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"rest-api-go/database"
	"rest-api-go/model/entity"
	"rest-api-go/model/request"
)

func ActivityHandlerCreate(ctx *fiber.Ctx) error {
	activity := new(request.ActivityCreateRequest)
	if err := ctx.BodyParser(activity); err != nil {
		return err
	}

	validate := validator.New()
	errValidate := validate.Struct(activity)

	if errValidate != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "your data is not valid",
			"error":   errValidate,
		})
	}

	newActivity := entity.Activity{
		UserId:      activity.UserId,
		Type:        activity.Type,
		Total:       activity.Total,
		Correct:     activity.Correct,
		Incorrect:   activity.Incorrect,
		Duration:    activity.Duration,
		Description: activity.Description,
	}

	errCreateActivity := database.DB.Create(&newActivity).Error
	if errCreateActivity != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "failed to store data",
			"error":   errCreateActivity,
		})
	}

	return ctx.JSON(fiber.Map{
		"message":     "activity successfully created",
		"user_id":     activity.UserId,
		"type":        activity.Type,
		"total":       activity.Total,
		"correct":     activity.Correct,
		"incorrect":   activity.Incorrect,
		"duration":    activity.Duration,
		"description": activity.Description,
	})
}
