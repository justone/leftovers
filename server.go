package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"strconv"

	"github.com/go-martini/martini"
	"github.com/justone/go-minibus"
	"github.com/martini-contrib/cors"
	// "github.com/martini-contrib/staticbin"
)

type Payment struct {
	Location string  `json:"location"`
	Amount   float64 `json:"amount"`
}

func (this Payment) Filter() interface{} {
	return this
}

type Data struct {
	StartAmount float64   `json:"start-amount"`
	Payments    []Payment `json:"previous-payments"`
}

func (this Data) Filter() interface{} {
	return this
}

type IncomingMessage struct {
	Type string            `json:"type"`
	Args map[string]string `json:"args"`
}

func main() {

	m := martini.New()
	route := martini.NewRouter()

	m.Use(cors.Allow(&cors.Options{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{"GET", "PATCH"},
		AllowHeaders:     []string{"Origin"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
	}))

	// generated with "go-bindata -o ../../static.go public/..."
	// m.Use(staticbin.Static("public", Asset))

	mb := minibus.Init()

	payments := make([]Payment, 0)
	initialAmount := 513.22

	route.Get("/conn/:cust/:conn", func(res http.ResponseWriter, params martini.Params) {
		cust := params["cust"]
		conn := params["conn"]

		message, err := mb.Receive(cust, conn)
		if err != nil {
			fmt.Fprintln(res, "{}")
		} else {
			fmt.Fprintln(res, message.Contents)
		}
	})
	route.Post("/conn/:cust", func(res http.ResponseWriter, req *http.Request, params martini.Params) {
		cust := params["cust"]

		body, err := ioutil.ReadAll(req.Body)
		if err != nil {
			http.Error(res, "Unable to read request body: "+err.Error(), http.StatusInternalServerError)
			return
		}

		fmt.Println("Got: ", string(body))

		var incoming IncomingMessage
		err = json.Unmarshal(body, &incoming)
		if err != nil {
			http.Error(res, "Bad request: "+err.Error(), http.StatusBadRequest)
			return
		}

		if incoming.Type == "add-payment" {
			fmt.Println("Location : ", incoming.Args["location"])
			fmt.Println("Amount : ", incoming.Args["amount"])

			amount, _ := strconv.ParseFloat(incoming.Args["amount"], 64)

			payments = append(payments, Payment{incoming.Args["location"], amount})

			coredata := Data{
				initialAmount,
				payments,
			}

			json, _ := json.Marshal(coredata)

			fmt.Println(string(json))

			mb.Send(cust, string(json))
		} else if incoming.Type == "load-info" {
			coredata := Data{
				initialAmount,
				payments,
			}

			json, _ := json.Marshal(coredata)

			fmt.Println(string(json))

			mb.Send(cust, string(json))
		}
	})

	m.Action(route.Handle)

	log.Println("Waiting for connections...")

	m.Run()
}
