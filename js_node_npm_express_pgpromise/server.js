'use strict';
const express = require('express'),
    bodyParser = require('body-parser');

const port = process.env.PORT || 8080;

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const routes = require('./api/routes/employeeRoutes');
routes(app);

app.listen(port, function () {
    console.log('Application listening on port ' + port);
});
