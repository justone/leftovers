package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/go-martini/martini"
	"github.com/justone/go-minibus"
	"github.com/martini-contrib/cors"
	// "github.com/martini-contrib/staticbin"
)

type Payment struct {
	Location string  `json:"location"`
	Amount   float32 `json:"amount"`
}

func (this Payment) Filter() interface{} {
	return this
}

type Data struct {
	StartAmount float32   `json:"start-amount"`
	Payments    []Payment `json:"previous-payments"`
}

func (this Data) Filter() interface{} {
	return this
}

func main() {

	m := martini.New()
	route := martini.NewRouter()

	m.Use(cors.Allow(&cors.Options{
		AllowOrigins:     []string{"http://localhost:3449"},
		AllowMethods:     []string{"GET", "PATCH"},
		AllowHeaders:     []string{"Origin"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
	}))

	// generated with "go-bindata -o ../../static.go public/..."
	// m.Use(staticbin.Static("public", Asset))

	route.Get("/test", func(res http.ResponseWriter) {
		result := &Data{
			513.22,
			[]Payment{
				{"Rite-aid", 5.28},
				{"Trader Joe's", 104.22},
			},
		}

		json, _ := json.Marshal(result)

		fmt.Fprintln(res, string(json))
	})
	mb := minibus.Init()

	route.Get("/conn/:cust/:conn", func(res http.ResponseWriter, params martini.Params) {
		cust := params["cust"]
		conn := params["conn"]

		message, err := mb.Receive(cust, conn)
		if err != nil {
			http.Error(res, "Timeout", http.StatusRequestTimeout)
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

		mb.Send(cust, string(body))
	})

	m.Action(route.Handle)

	log.Println("Waiting for connections...")

	m.Run()
}
