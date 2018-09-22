SELECT create_enumtable_type('currency'     , 'Table mappes to Java enum: nl.smith.account.enums.Currency');
SELECT create_enumtable_type('mutationtype' , 'Table mappes to Java enum: nl.smith.account.enums.Currency');
SELECT create_enumtable_type('accountnumber', 'Table mappes to Java enum: nl.smith.account.enums.AccountNumber');

CREATE TABLE IF NOT EXISTS mutation(id         bigserial
, accountnumber   varchar(16)   NOT NULL REFERENCES accountnumber(name)
, currency        varchar(16)   NOT NULL REFERENCES currency(name)
, interestdate    date          NOT NULL
, balancebefore   numeric(10,2) NOT NULL
, balanceafter    numeric(10,2) NOT NULL
, transactiondate date          NOT NULL
, amount          numeric(10,2) NOT NULL
, description     varchar(512)  NOT NULL
, ordernumber     smallint      DEFAULT -1
, created_at      TIMESTAMP     NOT NULL DEFAULT now()
);
