
-- Установка расширения uuid-ossp что бы работала автогенерация DEFAULT uuid_generate_v4()
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


create table if not exists groups_Product (
                              id uuid PRIMARY KEY,
                              name_group VARCHAR(80) not null,
                              id_parrent uuid,
                              id_slave uuid,
                              level_group bigint

);
-- колонка в товарах для связи с группой
alter table products ADD COLUMN id_groups uuid not null;












