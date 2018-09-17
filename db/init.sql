create table company (
  id   bigserial not null primary key,
  name varchar(255)
);

create table employee (
  id            bigserial not null primary key,
  date_of_birth date,
  firstname     varchar(255),
  lastname      varchar(255)
);

create table company_employees (
  company_id  bigint not null references company,
  employee_id bigint not null references employee,
  constraint company_employees_pkey primary key (company_id, employee_id)
);
