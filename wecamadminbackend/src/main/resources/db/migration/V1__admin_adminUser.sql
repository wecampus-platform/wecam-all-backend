CREATE TABLE admin_user (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(255) NOT NULL,
                            role VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
