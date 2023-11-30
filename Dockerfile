FROM golang:1.21-alpine3.18
WORKDIR /app
COPY go.mod .
COPY go.sum .
RUN go mod download
COPY . .
RUN go build -o ./out/dist .
CMD ./out/dist