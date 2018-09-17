package api

import (
	"database/sql/driver"
	"encoding/json"
	"time"
)

const (
	DateLayout = "2006-01-02"
)

type Date struct {
	time.Time
}

func (d *Date) MarshalJSON() ([]byte, error) {
	if d == nil {
		return nil, nil
	}

	if d.Time.IsZero() {
		return []byte("null"), nil
	}

	return json.Marshal(d.Format(DateLayout))
}

func (d *Date) UnmarshalJSON(b []byte) (err error) {
	if d == nil {
		return
	}

	var s string

	err = json.Unmarshal(b, &s)

	if err != nil {
		return
	}

	t, err := time.Parse(DateLayout, s)

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

func NewDate(year int, month time.Month, day int) Date {
	return Date{time.Date(year, month, day, 0, 0, 0, 0, time.UTC)}
}
