
-- Установка расширения uuid-ossp что бы работала автогенерация DEFAULT uuid_generate_v4()
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


create table if not exists users (
                              id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                              user_name varchar (20),
                              login varchar (20) unique,
                              password_user varchar (100),
                              role_user varchar (30),
                              comment varchar,
                              data_create_user date
);


create table if not exists products (
                                      id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      product_name varchar (20),
                                      producct_category varchar (20),
                                      product_articul varchar (20) unique,
                                      product_reference varchar (100),
                                      product_description varchar (300),
                                      product_coast double precision,
                                      product_count bigint,
                                      product_reserv bigint,
                                      data_create_product date);

create table if not exists comments (
                                        id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                        text_comment varchar (300),
                                        product_id uuid references products (id),
                                        user_id uuid references users (id),
                                        data_create_comment date
                            );

create table if not exists userId_productId (
                                        id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                        user_id uuid references users (id),
                                        product_id uuid references products (id)
                                            );

create table if not exists userId_commentId (
                                                id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                                user_id uuid references users (id),
                                                comment_id uuid references comments (id),
                                                product_id uuid references products (id));











