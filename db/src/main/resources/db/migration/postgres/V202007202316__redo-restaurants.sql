SET search_path TO ${schema};

ALTER TABLE restaurants
    DROP container_type,
    ADD COLUMN type VARCHAR(256) NOT NULL DEFAULT 'generic',
    ADD COLUMN address VARCHAR(256);
