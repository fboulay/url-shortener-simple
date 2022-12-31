CREATE TABLE url (
  id BIGSERIAL,
  version INTEGER NOT NULL DEFAULT 0,
  hash TEXT NOT NULL,
  long_url TEXT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true
);

CREATE UNIQUE INDEX hash_idx ON url (hash);
CREATE UNIQUE INDEX long_url_idx ON url (long_url);
