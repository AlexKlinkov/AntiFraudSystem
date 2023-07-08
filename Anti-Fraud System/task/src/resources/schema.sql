create table if not exists users
(
    id       bigint primary key,
    name     varchar,
    username varchar,
    password varchar,
    role     varchar,
    locked   boolean
);

create table if not exists suspicious_ip_address
(
    id bigint primary key,
    ip varchar
);

create table if not exists stolen_card
(
    id     bigint primary key,
    number varchar
);

create table if not exists transaction_info
(
    id          bigint primary key,
    amount      bigint,
    ip          varchar,
    number      varchar,
    region      varchar,
    createdDate date,
    result      varchar
);

create table if not exists transaction_feetback
(
    transactionId bigint REFERENCES transaction_info (id),
    feedback varchar,
    PRIMARY KEY (transactionId, feedback)
)

create table if not exists max_value_transaction
(
    number varchar REFERENCES transaction_info (number),
    maxALLOWED bigint,
    maxMANUAL bigint,
    PRIMARY KEY (number, maxALLOWED, maxMANUAL) unique
)