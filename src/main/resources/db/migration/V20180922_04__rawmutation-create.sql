CREATE TABLE IF NOT EXISTS simplemutation(id         bigserial
, accountnumber   varchar(16)   NOT NULL REFERENCES accountnumber(name)
, transactiondate date          NOT NULL
, ordernumber     smallint      DEFAULT -1
, amount          numeric(10,2) NOT NULL
, description     varchar(512)  NOT NULL
, created_at      TIMESTAMP     NOT NULL DEFAULT now()
, remark          varchar(512)
);
