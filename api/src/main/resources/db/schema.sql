use wirebarley;

create table if not exists account
(
    id               binary(16) unique not null
        primary key,
    created_datetime datetime(6)       null,
    updated_datetime datetime(6)       null,
    account_number   varchar(255)      not null,
    balance          decimal(38, 2)    not null
);

create table if not exists transactions
(
    id                      bigint auto_increment
        primary key,
    account_id              binary(16)                               not null,
    counterparty_account_id binary(16)                               null,
    amount                  decimal(38, 2)                           not null,
    fee                     decimal(38, 2)                           not null,
    type                    enum ('DEPOSIT', 'TRANSFER', 'WITHDRAW') not null,
    created_datetime        datetime(6)                              null,
    updated_datetime        datetime(6)                              null
);

