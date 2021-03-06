module.exports = function (app) {
    const companyController = require('../controllers/companyController');

    app.route('/companies')
        .get(companyController.findAll)
        .post(companyController.create);

    app.route('/companies/:companyId')
        .get(companyController.findById)
        .delete(companyController.delete);

    app.route('/companies/:companyId/employees')
        .post(companyController.addEmployee);

    app.route('/companies/:companyId/employees/:employeeId')
        .delete(companyController.removeEmployee);
};