insert into wisiszme.currency
values
    ('USD'),
    ('EUR'),
    ('GBP'),
    ('JPY'),
    ('INR'),
    ('PLN'),
    ('CHF');

insert into wisiszme.category
    (category_name  )
values
    ('Groceries'    ),
    ('Restaurant'   ),
    ('Rent'         ),
    ('Utilities'    ),
    ('Entertainment');

insert into wisiszme.person
    (fname,   lname,    email_addr,          password_hash)
values
    ('John',  'Doe',    'john@example.com',  'hash1'      ),
    ('Mary',  'Smith',  'mary@example.com',  'hash2'      ),
    ('Harry', 'Potter', 'harry@example.com', 'hash3'      ),
    ('Emily', 'Brown',  'emily@example.com', 'hash4'      );

insert into wisiszme.team
    (team_name)
values
    ('Trip to Krakow');

-- Assuming team_id = 1
insert into wisiszme.team_member
    (team_id, person_id, default_share)
values
    (1,       1,         1.0          ), -- John
    (1,       2,         1.0          ), -- Mary
    (1,       3,         2.0          ), -- Harry
    (1,       4,         2.0          );

-- Emily

-- Expense by John
insert into wisiszme.operation
    (team_id, description, total_amount, category_id, currency_code, operation_type)
values
    (1, 'Groceries at Biedronka', 60.00, 1, 'PLN', 'expense');

-- John: +60 PLN, -10 PLN
insert into wisiszme.operation_entry
    (operation_id, member_id, amount)
values
    (1,            1,         60.00 ),
    (1,            1,         -10.00),

-- Mary: -10 PLN
    (1,            2,         -10.00),

-- Harry: -20 PLN
    (1,            3,         -20.00),

-- Emily: -20 PLN
    (1,            4,         -20.00);

-- Repayment from Mary to John
insert into wisiszme.operation
    (team_id, description, total_amount, category_id, currency_code, operation_type)
values
    (1, 'Mary repays John', 10.00, 1, 'PLN', 'transfer');

-- Mary: +10 PLN
insert into wisiszme.operation_entry
    (operation_id, member_id, amount)
values
    (2,            2,         10.00 ),

-- John: -10 PLN
    (2,            1,         -10.00);
