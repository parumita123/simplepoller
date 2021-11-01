CREATE TABLE kry_services
(
    name        VARCHAR(32),
    url         VARCHAR(200) NOT NULL,
    created     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active   VARCHAR(4),
    PRIMARY KEY (name)
);
