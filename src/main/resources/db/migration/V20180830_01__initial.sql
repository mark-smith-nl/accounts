DROP TABLE    IF EXISTS mutation;
DROP TABLE    IF EXISTS currency;
DROP TABLE    IF EXISTS mutationtype;
DROP TABLE    IF EXISTS accountnumber;
DROP FUNCTION IF EXISTS create_enumtable_type(varchar, varchar);
DROP FUNCTION IF EXISTS deactivate_enumvalues(varchar);
DROP FUNCTION IF EXISTS insert_enumvalue(varchar, varchar, varchar, boolean, boolean);
DROP FUNCTION IF EXISTS dropAll();

CREATE OR REPLACE FUNCTION create_enumtable_type(table_name varchar(30), table_description varchar(100))
RETURNS VOID AS
$$
BEGIN
EXECUTE format('
    CREATE TABLE IF NOT EXISTS %1$s (
        name            varchar(64)  PRIMARY KEY
        , description   varchar(126) NOT NULL
        , default_value boolean   NOT NULL DEFAULT false
        , active_value  boolean    NOT NULL DEFAULT true
		, created       TIMESTAMP        NOT NULL DEFAULT now()
        , modified      TIMESTAMP        NOT NULL DEFAULT now());
     COMMENT ON TABLE %1$s IS %2$L;
',  table_name, table_description);
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION deactivate_enumvalues(table_name varchar(30))
RETURNS VOID AS
$$
BEGIN
EXECUTE format('UPDATE %s SET default_value = false, active_value = false, modified = now()', table_name);
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_enumvalue(table_name varchar(30), name varchar(64), description varchar(126), default_value boolean, active_value  boolean)
RETURNS VOID AS
$$
BEGIN
EXECUTE format('INSERT INTO %s(name, description, default_value, active_value, modified = now()) VALUES(%L, %L, %L, %L)', table_name, name, description, default_value, active_value);
END
$$ LANGUAGE plpgsql;
