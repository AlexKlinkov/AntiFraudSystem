create table if not exists users
(
    id       bigint primary key,
    name     varchar (100),
    username varchar unique (100),
    password varchar (50),
    role     varchar,
    locked   boolean
);

create table if not exists suspicious_ip_address
(
    id bigint primary key,
    ip varchar unique (20)
);

create table if not exists stolen_card
(
    id     bigint primary key,
    number varchar unique (16)
);

create table if not exists transaction_info
(
    id          bigint primary key,
    amount      bigint,
    ip          varchar (20),
    number      varchar (16),
    region      varchar (10),
    createdDate date,
    result      varchar (20)
);

create table if not exists transaction_feetback
(
    transactionId bigint REFERENCES transaction_info (id),
    feedback varchar (50),
    PRIMARY KEY (transactionId, feedback)
)

create table if not exists max_value_transaction
(
    number varchar REFERENCES transaction_info (number),
    maxALLOWED bigint,
    maxMANUAL bigint,
    PRIMARY KEY (number, maxALLOWED, maxMANUAL) unique
)
