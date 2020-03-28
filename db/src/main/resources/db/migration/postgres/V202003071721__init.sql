SET search_path TO ${schema};

DO
$$
    BEGIN
        CREATE USER ${username} PASSWORD '${password}';
    EXCEPTION
        WHEN DUPLICATE_OBJECT THEN
            RAISE NOTICE 'not creating existing user';
    END
$$;
GRANT USAGE ON SCHEMA ${schema} TO ${username};

-- data tables
CREATE TABLE restaurants
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(256)             NOT NULL,
    container_type VARCHAR(128)             NOT NULL,
    create_time    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
GRANT USAGE ON restaurants_id_seq TO ${username};
CREATE INDEX restaurants_name_idx ON restaurants (name);
GRANT SELECT, INSERT, UPDATE, DELETE ON restaurants TO ${username};

CREATE TABLE takeout_containers
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL
);
GRANT USAGE ON takeout_containers_id_seq TO ${username};
GRANT SELECT, INSERT, UPDATE, DELETE ON takeout_containers TO ${username};
