CREATE TABLE organization_request_file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    uuid BINARY(16) NOT NULL UNIQUE,

    original_file_name VARCHAR(255) NOT NULL,
    saved_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_url VARCHAR(512),

    request_id BIGINT NOT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_organization_request_file_request
       FOREIGN KEY (request_id) REFERENCES organization_request(request_pk_id)
);
