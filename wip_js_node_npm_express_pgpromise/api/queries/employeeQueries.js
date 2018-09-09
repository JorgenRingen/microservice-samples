const db = require('./db');
const employeeModel = require('../models/employeeModel');

exports.findAll = function (resultHandler, errorHandler) {
    return db.any('select id, firstname, lastname, date_of_birth from employee')
        .then(employeeRows => {
            const employees = employeeRows.map(employeeRow => employeeModel.fromEmployeeRow(employeeRow));
            resultHandler(employees);
        })
        .catch(error => {
            errorHandler(error);
        });
};

exports.findById = function (employeeId, resultHandler, errorHandler) {
    return db.one('select id, firstname, lastname, date_of_birth from employee where id = $1', employeeId)
        .then(employeeRow => {
            resultHandler(employeeModel.fromEmployeeRow(employeeRow));
        })
        .catch(error => {
            if (error.received === 0) {
                resultHandler(null);
            } else {
                errorHandler(error);
            }
        });
};

exports.create = function (employee, resultHandler, errorHandler) {
    db.one('insert into employee(firstname, lastname, date_of_birth) values($1, $2, $3) returning id',
        [ employee.firstname, employee.lastname, employee.dateOfBirth ])
        .then(data => {
            resultHandler(data.id);
        })
        .catch(error => {
            errorHandler(error);
        });
};

exports.update = function (employee, resultHandler, errorHandler) {
    db.none('update employee set (firstname, lastname, date_of_birth) = ($1, $2, $3) where id = $4',
        [ employee.firstname, employee.lastname, employee.dateOfBirth, employee.id ])
        .then(resultHandler())
        .catch(error => {
            errorHandler(error);
        });
};

exports.delete = function (employeeId, resultHandler, errorHandler) {
    db.none('delete from employee where id = $1', employeeId)
        .then(resultHandler())
        .catch(error => {
            errorHandler(error);
        });
};
