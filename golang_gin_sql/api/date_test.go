package api_test

import (
	"encoding/json"
	"time"

	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"

	. "github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

var _ = Describe("Date", func() {

	It("should marshal date as json", func() {
		t, err := time.Parse(DateLayout, "2018-12-24")

		Expect(err).ToNot(HaveOccurred())

		d := &Date{Time: t}

		res, err := json.Marshal(d)

		Expect(err).ToNot(HaveOccurred())
		Expect(res).To(MatchJSON("[2018,12,24]"))
	})

	It("should unmarshal json as date", func() {
		t, err := time.Parse(DateLayout, "2018-12-24")

		Expect(err).ToNot(HaveOccurred())

		var d *Date

		err = json.Unmarshal([]byte("[2018,12,24]"), &d)

		Expect(err).ToNot(HaveOccurred())
		Expect(d.Time).To(BeTemporally("==", t))
	})
})
