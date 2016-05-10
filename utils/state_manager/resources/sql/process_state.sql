CREATE TABLE process_state
(
    state_identifier VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(300) NOT NULL,
    status BOOLEAN NOT NULL,
    last_modified TIMESTAMP NOT NULL
);
