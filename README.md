# Cldbc
## Cldbc – a tiny command line db client

   Structure 
 * `/cldbc.connection` – Contains JDBC driver class descriptions.

 * `/config.xml` – Contains cldbc.connection configurations.
 
    Supports: Postgresql Mysql MongoDb. 
    app uses standard sql;
    ``
    clbdc> con mysql1
    DB Connected, alias: mysql1
    mysql1> select * from sakila.city limit 3;
    +----------+---------------------+-------------+----------------------+
    | city_id  | city                | country_id  | last_update          |
    +----------+---------------------+-------------+----------------------+
    | 1        | A Corua (La Corua)  | 87          | 2006-02-15 04:45:25  |
    | 2        | Abha                | 82          | 2006-02-15 04:45:25  |
    | 3        | Abu Dhabi           | 101         | 2006-02-15 04:45:25  |
    +----------+---------------------+-------------+----------------------+
    DB rows: 3
    DB columns: 4
    mysql1> show 1-2
    +----------+---------------------+-------------+----------------------+
    | city_id  | city                | country_id  | last_update          |
    +----------+---------------------+-------------+----------------------+
    | 1        | A Corua (La Corua)  | 87          | 2006-02-15 04:45:25  |
    +----------+---------------------+-------------+----------------------+
    DB rows: 1
    DB columns: 4
    mysql1> 
   ``
