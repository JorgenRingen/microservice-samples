FROM postgres:alpine
ENV POSTGRES_DB companies
ENV POSTGRES_USER user
ENV POSTGRES_PASSWORD password
COPY init.sql /docker-entrypoint-initdb.d/
