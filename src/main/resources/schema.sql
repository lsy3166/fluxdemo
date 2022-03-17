DROP TABLE IF exists customer;
CREATE TABLE customer (id SERIAL PRIMARY KEY, first_name VARCHAR(255), last_name VARCHAR(255));

-- DROP TABLE IF exists notification;
CREATE TABLE IF NOT EXISTS notification (id SERIAL PRIMARY KEY, title VARCHAR(200), content VARCHAR(5000), userid varchar(255), notiid bigint);