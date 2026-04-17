--liquibase formatted sql

--changeset lsaric:insert-users
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Luka', 'Saric', 'lsaric', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', true, 'lsaric@vibecheck.hr', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Marko', 'Gradiscaj', 'mgradiscaj', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', true, 'mgradiscaj@vibecheck.hr', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);
INSERT INTO users (first_name, last_name, username, avatar_url, avatar_public_id, bio, is_private, email, password, tstamp)
VALUES ('Karlo', 'Stjepanović', 'kstjepanovic', 'https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg', NULL, 'bio', true, 'kstjepanovic@vibecheck.hr', '$2a$12$AZyrR6f9u3f2z3vJXOV6yeLYAFdHPMmiLZw/OrSt2U.g.Fo/okfo2', CURRENT_TIMESTAMP);