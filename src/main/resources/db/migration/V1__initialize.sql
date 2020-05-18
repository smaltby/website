CREATE FUNCTION set_updated_at() RETURNS trigger LANGUAGE plpgsql as $$
BEGIN
    NEW.updated_at = current_date;
    RETURN NEW;
END;
$$;

CREATE TABLE post
(
	id SERIAL PRIMARY KEY,
	title TEXT NOT NULL,
	content TEXT NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER set_updated_at
	BEFORE UPDATE
	ON post
	FOR EACH ROW
	execute procedure set_updated_at();

CREATE TABLE tag
(
    id SERIAL PRIMARY KEY,
	post_id INTEGER NOT NULL REFERENCES post ON UPDATE CASCADE ON DELETE CASCADE,
	name TEXT NOT NULL
);

create index tag_name_index
	on tag (name);