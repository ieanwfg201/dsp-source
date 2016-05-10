CREATE TABLE risk_models
(
    model_id int NOT NULL PRIMARY KEY,
    data LONGTEXT,
    weight TINYINT(3) NOT NULL DEFAULT 0,
    modified_on TIMESTAMP
);
