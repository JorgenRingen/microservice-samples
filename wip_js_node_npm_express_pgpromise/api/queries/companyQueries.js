const db = require('./db');
const companyModel = require('../models/companyModel');
const employeeModel = require('../models/employeeModel');

exports.findAll = async function (resultHandler, errorHandler) {
    const companyIdRows = await db.any('select c.id from company as c')
        .catch(error => {
            if (error.received === 0) {
                resultHandler([]);
            } else {
                errorHandler(error);
            }
        });

    const companies = [];

    await Promise.all(companyIdRows.map(async (companyIdRow) => {
            await this.findById(companyIdRow.id, async function (company) {
                await companies.push(company);
            }, errorHandler);
        }
    ));

    await resultHandler(companies);
};

exports.findById = async function (companyId, resultHandler, errorHandler) {
    const companyRow = await db.one('select id, name from company where id = $1', companyId)
        .catch(error => {
            if (error.received === 0) {
                resultHandler(null);
            } else {
                errorHandler(error);
            }
        });

    if (companyRow) {
        return db.any('select e.id, e.firstname, e.lastname, e.date_of_birth from employee e inner join company_employees ce on e.id = ce.employee_id where ce.company_id = $1', companyRow.id)
            .then(employeeRowsForCompany => {
                const employeesForCompany = employeeRowsForCompany.map(employeeRow => employeeModel.fromEmployeeRow(employeeRow));
                resultHandler(new companyModel(companyRow.id, companyRow.name, employeesForCompany));
            })
            .catch(error => {
                if (error.received === 0) {
                    return new companyModel(companyRow.id, companyRow.name, []);
                } else {
                    errorHandler(error);
                }
            });
    }

};

exports.create = function (company, resultHandler, errorHandler) {
    db.one('insert into company(name) values($1) returning id', [ company.name ])
        .then(data => {
            resultHandler(data.id);
        })
        .catch(error => {
            errorHandler(error);
        });
};

exports.delete = function (companyId, resultHandler, errorHandler) {
    db.none('delete from company where id = $1', companyId)
        .then(resultHandler())
        .catch(error => {
            errorHandler(error);
        });
};

exports.addEmployeeToCompany = function (companyId, employeeId, resultHandler, errorHandler) {
    db.none('insert into company_employees(company_id, employee_id) values ($1, $2)', [ companyId, employeeId ])
        .then(resultHandler())
        .catch(error => {
            errorHandler(error);
        });
};

exports.removeEmployeeFromCompany = function (companyId, employeeId, resultHandler, errorHandler) {
    db.none('delete from company_employees where company_id = $1 and employee_id = $2', [ companyId, employeeId ])
        .then(resultHandler())
        .catch(error => {
            errorHandler(error);
        });
};
