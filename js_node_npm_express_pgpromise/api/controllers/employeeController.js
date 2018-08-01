const url = require('url');
const moment = require('moment');
const employeeModel = require('../models/employeeModel');
const employeeQueries = require('../queries/employeeQueries');

exports.findAll = function (req, res, next) {
    employeeQueries.findAll(function (employees) {
        res.json(employees);
    }, next);
};

exports.create = function (req, res, next) {
    const firstname = req.body.firstname;
    const lastname = req.body.lastname;
    const dateOfBirth = getDateOfBirthFromRequest(req);
    const employee = new employeeModel(null, firstname, lastname, dateOfBirth);

    employeeQueries.create(employee, function (id) {
        const fullUrl = req.protocol + '://' + req.get('host') + req.originalUrl + "/";
        res.location(fullUrl + id);
        res.status(201).end();
    }, next);
};

exports.findById = function (req, res, next) {
    const employeeId = req.params.employeeId;
    employeeQueries.findById(employeeId, function (employee) {
        if (employee == null) {
            res.status(404).end();
        } else {
            res.json(employee);
        }
    }, next);
};

exports.update = async function (req, res, next) {
    const employeeId = req.params.employeeId;

    await employeeQueries.findById(employeeId, async function (employee) {
        if (employee == null) {
            res.status(404).end();
        } else {
            const firstname = req.body.firstname;
            const lastname = req.body.lastname;
            const dateOfBirth = getDateOfBirthFromRequest(req);
            const employee = new employeeModel(employeeId, firstname, lastname, dateOfBirth);

            await employeeQueries.update(employee, async function () {
                await res.status(204).end();
            }, next);
        }
    }, next);
};

exports.delete = async function (req, res, next) {
    const employeeId = req.params.employeeId;
    await employeeQueries.findById(employeeId, async function (employee) {
        if (employee == null) {
            res.status(404).end();
        } else {
            await employeeQueries.delete(employeeId, async function () {
                await res.status(204).end();
            }, next);
        }
    }, next);
};

function getDateOfBirthFromRequest(req) {
    return (req.body.dateOfBirth != null) ? moment(req.body.dateOfBirth).format('YYYY-MM-DD') : null;
}
