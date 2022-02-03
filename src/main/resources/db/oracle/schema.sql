-- //@delimiter /
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE students';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE attachment';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;

/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE description';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;

/
-- //@delimiter ;

CREATE TABLE students
( id number(10) NOT NULL,
  name varchar2(40) NOT NULL,
  class varchar2(10) NOT NULL,
  birth_date date NOT NULL,
  created_timestamp timestamp NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE attachment
( file_id number(10) NOT NULL,
  content blob NOT NULL,
  PRIMARY KEY(file_id)
);

CREATE TABLE description
( id number(10) NOT NULL,
  content clob NOT NULL,
  PRIMARY KEY(id)
);