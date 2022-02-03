# oracle-to-pg-data-copy
Demo code for copy data from Oracle to Postgres

## What does it do

* Connects to an Oracle and Postgres database

* Creates tables and imports data

* Copy data from Oracle to Postgres

Here are some data types convensions, see ``DataCopyService.java``

* VARCHAR2 -> varchar
* DATE -> date
* TIMESTAMP -> timestamp without time zone
* TIMESTAMP WITH TIME ZONE -> timestamp with time zone
* NUMBER -> numeric
* CLOB -> text
* BLOB -> bytea

## Prerequisite

You need to have Oracle and Postgres database.

If you don't have, you cound refer below to run Oracle and PG locally in vagrant box.

https://github.com/bin9wei/vagrant-oracle-database-xe-18c

https://github.com/bin9wei/vagrant-postgres-14

## How to build

```
mvn clean install
```

## How to run

Run ``App.java``

## Database schema

Oracle ``src/main/resources/db/oracle/schema.sql``

Postgres ``src/main/resources/db/pg/schema.sql``

## DB configurations

COnfigurations in ``Constraints.java``. Change if needed.