package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"log"
	"rest-api-go/database"
	"rest-api-go/model/entity"
	"rest-api-go/model/request"
	"strings"
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

func ActivityUpdateHandler(ctx *fiber.Ctx) error {
	activityUpdate := new(request.ActivityUpdateRequest)
	if err := ctx.BodyParser(activityUpdate); err != nil {
		return ctx.Status(400).JSON(fiber.Map{
			"message": "Bad Request",
		})
	}

	activityId := ctx.Params("id")
	var activities entity.Activity

	//CHECK ACTIVITY EXISTENCE
	err := database.DB.First(&activities, "id = ?", activityId).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "Activity not found",
		})
	}

	if activityUpdate.Description != "" {
		activities.Description = activityUpdate.Description
	}

	errUpdate := database.DB.Save(&activities).Error
	if errUpdate != nil {
		return ctx.Status(500).JSON(fiber.Map{
			"message": "internal server error",
		})
	}

	return ctx.JSON(fiber.Map{
		"message": "Activity successfully updated",
	})

}

func ActivityHandlerDelete(ctx *fiber.Ctx) error {
	activityId := ctx.Params("id")
	var activities entity.Activity

	//CHECK ACTIVITY EXISTENCE
	err := database.DB.First(&activities, "id = ?", activityId).Error
	if err != nil {
		return ctx.Status(404).JSON(fiber.Map{
			"message": "Activity not found",
		})
	}

	errDelete := database.DB.Debug().Delete(&activities).Error
	if errDelete != nil {
		return ctx.Status(500).JSON(fiber.Map{
			"message": "internal server error",
		})
	}

	return ctx.Status(200).JSON(fiber.Map{
		"message": "Activity successfully deleted",
	})
}

func ActivityHandlerGet(ctx *fiber.Ctx) error {
	id := ctx.Params("id")

	var activities []entity.Activity
	result := database.DB.Where("user_id = ?", id).Find(&activities)

	if result.Error != nil {
		// Check if the error message contains "record not found"
		if strings.Contains(result.Error.Error(), "record not found") {
			return ctx.Status(404).JSON(fiber.Map{
				"message": "There's no activity",
			})
		}

		log.Println(result.Error)
		return ctx.Status(500).JSON(fiber.Map{
			"message": "An error occurred",
		})
	}

	if len(activities) == 0 {
		// Return a message indicating no activities found
		return ctx.Status(fiber.StatusNotFound).JSON(fiber.Map{
			"message": "No activities found for the given ID",
		})
	}

	return ctx.Status(200).JSON(fiber.Map{
		"message": "Data successfully retrieved",
		"data":    activities,
	})
}
