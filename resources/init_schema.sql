CREATE TABLE url (
  id BIGSERIAL,
  version INTEGER DEFAULT 0,
  hash TEXT NOT NULL,
  long_url TEXT NOT NULL,
  active BOOLEAN DEFAULT true
);
