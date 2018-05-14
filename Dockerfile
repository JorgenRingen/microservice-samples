FROM postgres
ENV POSTGRES_DB micoservice-samples
ENV POSTGRES_USER user
ENV POSTGRES_PASSWORD secret
#COPY init.sql /docker-entrypoint-initdb.d/