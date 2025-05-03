-- View: team_member_balances
-- Description:
-- This view calculates and stores balances for each team member across all teams,
-- ensuring that balances are scoped specifically to their respective teams.
--
-- For each team, the view provides:
-- - The team ID (`team_id`).
-- - The member ID (`member_id`) and person ID (`person_id`).
-- - The first name (`fname`) and last name (`lname`) of the team member.
-- - The total balance (`balance`), which represents:
--   * Positive balance: Indicates the team member has covered expenses for the team.
--   * Negative balance: Indicates the team member owes money to the team.
--
-- Balances are calculated based on entries in the `operation_entry` table,
-- grouped by team and member, with expenses scoped to the team's operations only.
--
-- Notes:
-- - Changes to the underlying tables (`operation_entry`, `team_member`, `operation`, etc.)
--   are automatically reflected in the view, ensuring real-time balance updates.

CREATE OR REPLACE VIEW wisiszme.team_member_balances AS
SELECT
    row_number() OVER () AS id,
    tm.team_id,
    tm.member_id,
    p.person_id AS person_id,
    p.fname,
    p.lname,
    SUM(oe.amount) AS balance
FROM
    wisiszme.operation_entry oe
INNER JOIN
    wisiszme.team_member tm ON oe.member_id = tm.member_id
INNER JOIN
    wisiszme.person p ON tm.person_id = p.person_id
INNER JOIN
    wisiszme.operation o ON oe.operation_id = o.operation_id
WHERE
    o.team_id = tm.team_id
GROUP BY
    tm.team_id,
    tm.member_id,
    p.person_id,
    p.fname,
    p.lname;


-- Function: calculate_team_balances
-- Description:
-- This function queries the `team_member_balances` view to retrieve balances for all members
-- within a specified team (`team_id`).
--
-- Parameters:
-- - p_team_id (INT): The ID of the team for which balances are retrieved.
--
-- Output:
-- - A table with the following columns:
--   * person_id (INT): The ID of the person (team member).
--   * fname (TEXT): First name of the person.
--   * lname (TEXT): Last name of the person.
--   * balance (DECIMAL): Total balance of the team member (positive or negative).
--
-- Notes:
-- - This function filters balances for the given team ID based on the `team_member_balances` view.
-- - Use this function for efficient querying of team-specific balances.
--
-- Example Usage:
-- SELECT * FROM wisiszme.calculate_team_balances(1);


CREATE OR REPLACE FUNCTION wisiszme.calculate_team_balances(p_team_id INT)
RETURNS TABLE (
    person_id INT,
    fname TEXT,
    lname TEXT,
    balance DECIMAL(10, 2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        tmb.person_id,
        tmb.fname,
        tmb.lname,
        tmb.balance
    FROM
        wisiszme.team_member_balances AS tmb
    WHERE
        team_id = p_team_id;
END;
$$ LANGUAGE plpgsql
