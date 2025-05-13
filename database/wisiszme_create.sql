-- tables

-- Table: category
create table wisiszme.category
(
    category_id   int  not null generated always as identity,
    category_name text not null,
    constraint category_name_unique unique (category_name) not deferrable initially immediate,
    constraint category_pk primary key (category_id)
);

-- Table: currency
create table wisiszme.currency
(
    currency_code text not null,
    constraint currency_code_unique unique (currency_code) not deferrable initially immediate,
    constraint currency_pk primary key (currency_code)
);

-- Table: operation
create table wisiszme.operation
(
    operation_id   int            not null generated always as identity,
    team_id        int            not null,
    operation_date timestamptz    not null default now(),
    description    text           null,
    total_amount   decimal(10, 2) not null,
    category_id    int            not null,
    currency_code  text           not null,
    operation_type text           not null check (operation_type in ('expense', 'transfer', 'income')),
    constraint operation_pk primary key (operation_id)
);

create index operation_idx_1 on wisiszme.operation (team_id asc);

create index operation_idx_2 on wisiszme.operation (currency_code asc);

create index operation_idx_3 on wisiszme.operation (category_id asc);

create index operation_idx_4 on wisiszme.operation (operation_date asc);

-- Table: operation_entry
create table wisiszme.operation_entry
(
    entry_id     int            not null generated always as identity,
    operation_id int            not null,
    member_id    int            not null,
    amount       decimal(10, 2) not null,
    constraint operation_entry_pk primary key (entry_id)
);

comment on table wisiszme.operation_entry is 'double-entry bookkeeping';

create index operation_entry_idx_1 on wisiszme.operation_entry (operation_id asc);

create index operation_entry_idx_2 on wisiszme.operation_entry (member_id asc);

-- Table: person
create table wisiszme.person
(
    person_id     int  not null generated always as identity,
    fname         text not null,
    lname         text null,
    email_addr    text not null,
    password_hash text not null,
    constraint email_addr_unique unique (email_addr) not deferrable initially immediate,
    constraint person_pk primary key (person_id)
);

comment on column wisiszme.person.email_addr is 'email will be a domain, so it can''''t be used as the name of the column';

-- Table: refresh_token
create table wisiszme.refresh_token
(
    token_id   int         not null generated always as identity,
    person_id  int         not null,
    token      text        not null,
    issued_at  timestamptz not null default now(),
    expires_at timestamptz not null,
    is_revoked boolean     not null default false,
    constraint refresh_token_unique unique (token) not deferrable initially immediate,
    constraint refresh_token_pk primary key (token_id)
);

create index refresh_token_idx_1 on wisiszme.refresh_token (person_id asc);

-- Table: team
create table wisiszme.team
(
    team_id     int  not null generated always as identity,
    team_name   text not null,
    invite_code text generated always as (upper(substring(encode(sha256(team_id::text::bytea), 'hex') from 1 for
                                                          8))) stored unique,
    constraint team_pk primary key (team_id)
);

comment on table wisiszme.team is 'group is a reserved keyword';

-- Table: team_member
create table wisiszme.team_member
(
    member_id     int           not null generated always as identity,
    team_id       int           not null,
    person_id     int           not null,
    default_share decimal(2, 1) not null default 1.0 check (default_share > 0),
    constraint team_member_unique unique (member_id, team_id) not deferrable initially immediate,
    constraint team_member_pk primary key (member_id)
);

comment on table wisiszme.team_member is 'decides the split, like a weight. 1.0 for all members means an equal split';

create index team_member_idx_1 on wisiszme.team_member (team_id asc);

create index team_member_idx_2 on wisiszme.team_member (person_id asc);

-- foreign keys
-- Reference: expense_category (table: operation)
alter table wisiszme.operation
    add constraint expense_category foreign key (category_id) references wisiszme.category (category_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: expense_contribution_expense (table: operation_entry)
alter table wisiszme.operation_entry
    add constraint expense_contribution_expense foreign key (operation_id) references wisiszme.operation (operation_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: expense_contribution_team_member (table: operation_entry)
alter table wisiszme.operation_entry
    add constraint expense_contribution_team_member foreign key (member_id) references wisiszme.team_member (member_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: expense_currency (table: operation)
alter table wisiszme.operation
    add constraint expense_currency foreign key (currency_code) references wisiszme.currency (currency_code) on delete restrict on update restrict not deferrable initially immediate;

-- Reference: expense_team (table: operation)
alter table wisiszme.operation
    add constraint expense_team foreign key (team_id) references wisiszme.team (team_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: refresh_token_person (table: refresh_token)
alter table wisiszme.refresh_token
    add constraint refresh_token_person foreign key (person_id) references wisiszme.person (person_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: team_member_person (table: team_member)
alter table wisiszme.team_member
    add constraint team_member_person foreign key (person_id) references wisiszme.person (person_id) on delete cascade on update cascade not deferrable initially immediate;

-- Reference: team_member_team (table: team_member)
alter table wisiszme.team_member
    add constraint team_member_team foreign key (team_id) references wisiszme.team (team_id) on delete cascade on update cascade not deferrable initially immediate;
