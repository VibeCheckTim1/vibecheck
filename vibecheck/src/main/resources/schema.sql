CREATE TABLE users (
    id_user BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(15) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    avatar_url VARCHAR(255) NOT NULL,
    avatar_public_id VARCHAR(255),
    bio VARCHAR(500),
    profile_visibility VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    tstamp TIMESTAMP NOT NULL
);