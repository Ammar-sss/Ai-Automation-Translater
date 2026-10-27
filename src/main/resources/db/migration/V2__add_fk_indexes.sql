CREATE INDEX idx_term_definitions_term_id    ON term_definitions (term_id);
CREATE INDEX idx_examples_term_id            ON examples (term_id);
CREATE INDEX idx_example_definitions_term_id ON example_definitions (term_id);
CREATE INDEX idx_user_roles_role_id          ON user_roles (role_id);