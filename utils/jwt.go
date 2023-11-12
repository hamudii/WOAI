package utils

import "github.com/dgrijalva/jwt-go"

var SecretKey = "BANGKIT_WOAI"

func GenerateToken(claims *jwt.MapClaims) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	webToken, err := token.SignedString([]byte(SecretKey))
	if err != nil {
		return "", err
	}

	return webToken, nil
}
