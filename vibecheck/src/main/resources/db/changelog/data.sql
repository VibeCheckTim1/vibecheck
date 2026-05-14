--liquibase formatted sql

--changeset lsaric:insert-users context:integration
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Luka', 'Saric', 'lsaric', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', true, 'lsaric@vibecheck.hr', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Marko', 'Gradiscaj', 'mgradiscaj', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', true, 'mgradiscaj@gmail.com', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Karlo', 'Stjepanović', 'kstjepanovic', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', false, 'kstjepanovic@vibecheck.hr', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);

--changeset lsaric:insert-roles context:integration
INSERT INTO roles (name, description)
VALUES ('USER', 'Default application user role');

INSERT INTO roles (name, description)
VALUES ('ADMIN', 'Application administrator role');


--PRIMJER za DODAVANJE USER ROLE I ENDPOINT
--INSERT INTO user_role (user_id, role_id)
--SELECT u.id_user, r.id_role
--FROM users u
--JOIN roles r ON r.name = 'USER';

--INSERT INTO endpoints (http_method, path, description)
--VALUES ('GET', '/security/current-user', 'Returns the currently authenticated user');

--INSERT INTO role_endpoint (role_id, endpoint_id)
--SELECT r.id_role, e.id_endpoint
--FROM roles r
--JOIN endpoints e ON e.http_method = 'GET' AND e.path = '/security/current-user'
--WHERE r.name = 'USER';