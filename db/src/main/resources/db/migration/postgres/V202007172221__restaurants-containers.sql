SET search_path TO ${schema};

CREATE TABLE restaurant_containers
(
    id                   BIGSERIAL PRIMARY KEY,
    restaurant_id        BIGINT NOT NULL REFERENCES restaurants ON DELETE CASCADE,
    takeout_container_ID BIGINT NOT NULL REFERENCES takeout_containers ON DELETE CASCADE,
    create_time          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
GRANT USAGE ON restaurant_containers_id_seq TO ${username};
CREATE INDEX restaurant_containers_restaurant_id_idx ON restaurant_containers (restaurant_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON restaurant_containers TO ${username};
