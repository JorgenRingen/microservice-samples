FROM payara/server-full

# add postgresql driver to payara
COPY lib/postgresql-42.2.5.jar ${PAYARA_PATH}/glassfish/domains/domain1/lib

# add postgresql connection-pool datasource to payara server by using asadmin
RUN ${PAYARA_PATH}/bin/asadmin start-domain domain1 && \
    ${PAYARA_PATH}/bin/asadmin --user=admin --passwordfile=/opt/pwdfile create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource \
    --property port=5432:databaseName="companies":serverName="postgresql":user="user":password="password" PostgreSQLPool && \
    ${PAYARA_PATH}/bin/asadmin --user=admin --passwordfile=/opt/pwdfile create-jdbc-resource --connectionpoolid PostgreSQLPool jdbc/postgresql && \
    ${PAYARA_PATH}/bin/asadmin stop-domain domain1

# add application to autodeploy directory
COPY target/demoapp.war $AUTODEPLOY_DIR