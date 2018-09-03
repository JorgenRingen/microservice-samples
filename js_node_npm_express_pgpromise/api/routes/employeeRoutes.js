'use strict';
module.exports = function (app) {
    const employeeController = require('../controllers/employeeController');

    app.route('/employees')
        .get(employeeController.findAll)
        .post(employeeController.create);

    app.route('/employees/:employeeId')
        .get(employeeController.findById)
        .put(employeeController.update)
        .delete(employeeController.delete);
};