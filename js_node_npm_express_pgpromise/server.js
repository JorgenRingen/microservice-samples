const express = require('express'),
    bodyParser = require('body-parser');

const port = process.env.PORT || 8080;

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const employeeRoutes = require('./api/routes/employeeRoutes');
employeeRoutes(app);

const companyRoutes = require('./api/routes/companyRoutes');
companyRoutes(app);

app.listen(port, function () {
    console.log('Application listening on port ' + port);
});
