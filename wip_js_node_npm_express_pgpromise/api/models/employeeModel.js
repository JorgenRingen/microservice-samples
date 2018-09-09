const moment = require('moment');

module.exports = class Employee {

    constructor(id, firstname, lastname, dateOfBirth) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
    }

    static fromEmployeeRow(employeeRow) {
        const dateOfBirth = (employeeRow.date_of_birth) ? moment(employeeRow.date_of_birth).format('YYYY-MM-DD') : null;
        return new Employee(employeeRow.id, employeeRow.firstname, employeeRow.lastname, dateOfBirth);
    }
};