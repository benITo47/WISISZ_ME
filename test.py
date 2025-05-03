balance = [
    ["A", 10],
    ["B", 10],
    ["C", 25],
    ["D", -15],
    ["E", -20],
    ["F", 25],
    ["G", -30],
    ["H", -5]
]


def print_balance():
    print("--- Balance ---")
    for p, v in balance:
        print(p, v)


balance.sort(key=lambda x: x[1])

begin = 0
end = len(balance) - 1

while begin < end:
    print_balance()

    if balance[begin][1] > 0 or balance[end][1] < 0:
        raise ValueError("Unexpected balance")

    if -balance[begin][1] > balance[end][1]:
        owed = balance[end][1]
    else:
        owed = -balance[begin][1]

    balance[begin][1] += owed
    balance[end][1] -= owed

    print(f"{balance[begin][0]} owes {balance[end][0]} {owed}$")

    if balance[begin][1] == 0:
        begin += 1

    if balance[end][1] == 0:
        end -= 1
