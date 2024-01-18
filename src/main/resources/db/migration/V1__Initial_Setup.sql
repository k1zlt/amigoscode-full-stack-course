CREATE SEQUENCE customer_id_sequence increment by 50;

CREATE TABLE customer (
    id BIGINT default nextval('customer_id_sequence') PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INT NOT NULL
);