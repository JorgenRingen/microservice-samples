const companyModel = require('../models/companyModel');
const companyQueries = require('../queries/companyQueries');
const employeeQueries = require('../queries/employeeQueries');

exports.findAll = function (req, res, next) {
    companyQueries.findAll(function (companies) {
        res.json(companies);
    }, next);
};

exports.findById = function (req, res, next) {
    const companyId = req.params.companyId;
    companyQueries.findById(companyId, function (company) {
        if (company == null) {
            res.status(404).end();
        } else {
            res.json(company);
        }
    }, next);
};

exports.create = function (req, res, next) {
    const name = req.body.name;
    const company = new companyModel(null, name);

    companyQueries.create(company, function (id) {
        res.location('/companies/' + id);
        res.status(201).end();
    }, next);
};

exports.delete = function (req, res, next) {
    const companyId = req.params.companyId;
    companyQueries.findById(companyId, function (company) {
        if (company == null) {
            res.status(404).end();
        } else {
            companyQueries.delete(companyId, function () {
                res.status(204).end();
            }, next);
        }
    }, next);
};

exports.addEmployee = async function (req, res, next) {
    const companyId = req.params.companyId;
    const employeeId = req.params.employeeId;

    await companyQueries.findById(companyId, async function (company) {
        if (company == null) {
            res.status(404).end();
            return;
        }

        await employeeQueries.findById(employeeId, async function (employee) {
            if (employee == null) {
                res.status(404).end();
                return;
            }

            await companyQueries.addEmployeeToCompany(companyId, employeeId, function () {
                res.status(204).end();
            }, next);

        }, next);
    }, next);
};

exports.removeEmployee = async function (req, res, next) {
    const companyId = req.params.companyId;
    const employeeId = req.params.employeeId;

    await companyQueries.findById(companyId, async function (company) {
        if (company == null) {
            res.status(404).end();
            return;
        }

        await employeeQueries.findById(employeeId, async function (employee) {
            if (employee == null) {
                res.status(404).end();
                return;
            }

            await companyQueries.removeEmployeeFromCompany(companyId, employeeId, function () {
                res.status(204).end();
            }, next);

        }, next);
    }, next);
};
