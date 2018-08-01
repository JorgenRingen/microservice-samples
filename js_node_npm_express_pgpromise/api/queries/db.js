const pgp = require('pg-promise')(/*options*/);
const db = pgp('postgres://user:password@localhost:5432/companies');
db.connect()
    .then(obj => {
        obj.done();
    })
    .catch(error => {
        console.log('ERROR connecting to database:', error.message || error);
    });

module.exports = db;