
-- Установка расширения uuid-ossp что бы работала автогенерация DEFAULT uuid_generate_v4()
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


create table if not exists testData (
                              id uuid PRIMARY KEY,
                              test_name varchar (20),
                              test_login varchar (20) unique,
                              test_password_user varchar (30),
                              test_role_user varchar (30),
                              test_comment varchar,
                              test_data_create_user date
);












