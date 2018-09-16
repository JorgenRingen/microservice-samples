package api_test

import (
	"encoding/json"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

func TestDate(t *testing.T) {
	t.Run("should marshal date as json", func(t *testing.T) {
		d := api.NewDate(2018, time.December, 24)

		actual, err := json.Marshal(&d)

		assert.NoError(t, err)
		assert.JSONEq(t, `[2018,12,24]`, string(actual))
	})

	t.Run("should unmarshal json as date", func(t *testing.T) {
		var actual api.Date

		err := json.Unmarshal([]byte("[2018,12,24]"), &actual)

		assert.NoError(t, err)
		assert.Equal(t, api.NewDate(2018, time.December, 24), actual)
	})
}
