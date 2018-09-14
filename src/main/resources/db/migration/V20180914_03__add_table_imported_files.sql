CREATE TABLE IF NOT EXISTS mutationfile(id bigserial
, absolute_file_path varchar(512)   NOT NULL
, file_bytes         bytea         NOT NULL
, checksum           integer       NOT NULL
, created_at         TIMESTAMP     NOT NULL DEFAULT now()
);