drop table if exists students;
CREATE TABLE students
( id NUMERIC(10) NOT NULL,
  name VARCHAR(40) NOT NULL,
  class VARCHAR(10),
  birth_date date NOT NULL,
  created_timestamp timestamp NOT NULL,
  PRIMARY KEY(id)
);

drop table if exists attachment;
CREATE TABLE attachment
( file_id NUMERIC(10) NOT NULL,
  content bytea NOT NULL,
  PRIMARY KEY(file_id)
);

drop table if exists description;
CREATE TABLE description
( id NUMERIC(10) NOT NULL,
  content text NOT NULL,
  PRIMARY KEY(id)
);