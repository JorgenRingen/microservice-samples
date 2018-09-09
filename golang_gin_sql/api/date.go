package api

import (
	"database/sql/driver"
	"encoding/json"
	"fmt"
	"time"
)

const (
	DateLayout = "2006-1-2"
)

type Date struct {
	time.Time
}

func (d *Date) MarshalJSON() ([]byte, error) {
	if d == nil {
		return nil, nil
	}

	if d.Time.IsZero() {
		return []byte("[]"), nil
	}

	return json.Marshal([3]int{d.Year(), int(d.Month()), d.Day()})
}

func (d *Date) UnmarshalJSON(b []byte) (err error) {
	if d == nil {
		return
	}

	var a [3]int

	err = json.Unmarshal(b, &a)

	if err != nil {
		return
	}

	t, err := time.Parse(DateLayout, fmt.Sprintf("%d-%d-%d", a[0], a[1], a[2]))

	if err != nil {
		return
	}

	d.Time = t

	return
}

func (d *Date) Scan(src interface{}) error {
	if t, ok := src.(time.Time); ok {
		d.Time = t
	}

	return nil
}

func (d *Date) Value() (driver.Value, error) {
	if d == nil {
		return nil, nil
	}

	return d.Time, nil
}
