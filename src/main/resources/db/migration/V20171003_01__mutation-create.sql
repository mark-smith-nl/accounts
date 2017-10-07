create table account.mutation(id         bigserial
, accountnumber    character varying(9)  NOT NULL
, currency        character varying(3)   NOT NULL
, interestdate    date                   NOT NULL
, balancebefore   numeric(10,2)          NOT NULL
, balanceafter    numeric(10,2)          NOT NULL
, transactiondate date                   NOT NULL
, amount          numeric(10,2)          NOT NULL
, description     character varying(512) NOT NULL
, ordernumber     smallint               default -1
);

 
