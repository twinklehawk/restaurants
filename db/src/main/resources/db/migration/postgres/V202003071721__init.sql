SET search_path TO ${schema};

DO $$
    BEGIN
        CREATE USER ${username} PASSWORD '${password}';
    EXCEPTION WHEN DUPLICATE_OBJECT THEN
        RAISE NOTICE 'not creating existing user';
    END
$$;
GRANT USAGE ON SCHEMA ${schema} TO ${username};

-- data tables
CREATE TABLE restaurant
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(256)             NOT NULL,
    container_type VARCHAR(128)             NOT NULL,
    create_time    TIMESTAMP WITH TIME ZONE NOT NULL
);
GRANT USAGE ON restaurant_id_seq TO ${username};
CREATE INDEX restaurant_name_idx ON restaurant (name);
GRANT SELECT, INSERT, UPDATE, DELETE ON restaurant TO ${username};

CREATE TABLE takeout_container
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL
);
GRANT USAGE ON takeout_container_id_seq TO ${username};
GRANT SELECT, INSERT, UPDATE, DELETE ON takeout_container TO ${username};
