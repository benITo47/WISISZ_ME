package me.wisisz.controller;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.OperationSummaryDTO;
import me.wisisz.dto.OperationDetailDTO;
import me.wisisz.dto.TransactionDTO;
import me.wisisz.dto.TeamOperationRequestDTO;
import me.wisisz.model.TeamMemberBalances;
import me.wisisz.service.TeamMemberBalancesService;
import me.wisisz.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me/teams/{teamId}/operations")
public class MeOperationsController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberBalancesService teamMemberBalancesService;

    /**
     * GET /api/me/teams/{teamId}/operations
     *
     * Retrieves all operations for a given team where the authenticated user is a
     * member.
     *
     * Headers:
     * - Authorization: Bearer <JWT>
     *
     * Path Variables:
     * - teamId (Integer): ID of the team
     *
     * Response (200 OK):
     * [
     * {
     * "operationId": 101,
     * "teamName": "Trip Fund",
     * "categoryName": "Travel",
     * "operations": [
     * {
     * "fname": ...,
     * "lname": ...,
     * "emailAddr": ...,
     * "amount": ...,
     * "currencyCode": ...
     * },
     * ...]
     * }
     * ]
     *
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (404 NOT FOUND): If team or operations not found.
     */
    @GetMapping("")
    public ResponseEntity<List<OperationDTO>> getTeamOperations(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws Exception {

        Optional<List<OperationDTO>> operations = teamService.getTeamOperationsView(teamId);
        if (operations.isPresent()) {
            return new ResponseEntity<>(operations.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * GET /api/me/teams/{teamId}/operations/summary
     *
     * Retrieves basic summary for all operations for a given team where the authenticated user is a
     * member.
     *
     * Headers:
     * - Authorization: Bearer <JWT>
     *
     * Path Variables:
     * - teamId (Integer): ID of the team
     *
     * Response (200 OK):
     * [
     * {
     * "operationId": 101,
     * "title": "Lunch",
     * "totalAmount": "250.0",
     * "categoryName": "Restaurant",
     * },
     * ...]
     *
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (404 NOT FOUND): If team or operations not found.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<OperationSummaryDTO>> getTeamOperationsSummary(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws Exception {

        Optional<List<OperationSummaryDTO>> operations = teamService.getTeamOperationsSummaryView(teamId);
        if (operations.isPresent()) {
            return new ResponseEntity<>(operations.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * POST /api/me/teams/{teamId}/operations
     *
     * Adds a new operation to the team.
     * Automatically generates operation entries based on the input.
     *
     * Headers:
     * - Authorization: Bearer <JWT>
     *
     * Path Variables:
     * - teamId (Integer): ID of the team
     *
     * Request Body (JSON):
     * {
     * "title": "Lunch",
     * "totalAmount": "250.00",
     * "categoryId": "3",
     * "currencyCode": "USD",
     * "description": "Post-project lunch gathering",
     * "operationType": "expense", // or "income", "transfer"
     * "participants": [
     * {
     *  "personId": "1",
     *  "share": "2",
     * },
     * ...]
     * }
     *
     * Response (200 OK):
     * { "message": "Operation successfully saved" }
     *
     * Response (400 BAD REQUEST): If incorrect data passed
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (500 INTERNAL SERVER ERROR): On unexpected server error.
     */
    @PostMapping("")
    public ResponseEntity<Map<String, String>> addOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @RequestBody TeamOperationRequestDTO operationData) throws Exception {

        Integer meId = (Integer) request.getAttribute("personId");
        try {
            String message = teamService.saveTeamOperation(meId, teamId, operationData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            HttpStatus status = msg != null && msg.contains("not in the team") ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", msg));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/me/teams/{teamId}/operations
     *
     * TODO: Updates an existing operation.
     * Not yet implemented.
     *
     * Response (403 FORBIDDEN): If user is not a team member.
     */
    @PutMapping("")
    public ResponseEntity<Map<String, String>> updateOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> operationData) throws Exception {

        throw new UnsupportedOperationException("TODO");
    }

    /**
     * GET /api/me/teams/{teamId}/operations/{operationId}
     *
     * Retrieves specific operation for a given team where the authenticated user is a
     * member.
     *
     * Headers:
     * - Authorization: Bearer <JWT>
     *
     * Path Variables:
     * - teamId (Integer): ID of the team
     * - operationId (Integer): ID of the operation
     *
     * Response (200 OK):
     * {
     * "title": "Lunch",
     * "description": "Post-project lunch gathering",
     * "totalAmount": "250.00",
     * "operationDate": "2025-04-08T10:53:17.578441Z"
     * "participants": [
     * {
     * "personId": ...,
     * "fname": ...,
     * "lname": ...,
     * "emailAddr": ...,
     * "share": ...,
     * "paidAmount": ...,
     * "currencyCode": ...,
     * },
     * ...]
     * }
     *
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (404 NOT FOUND): If team or operation not found.
     */
    @GetMapping("/{operationId}")
    public ResponseEntity<OperationDetailDTO> getOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId) throws Exception {

        Optional<OperationDetailDTO> operation = teamService.getSingleTeamOperationView(teamId, operationId);
        if (operation.isPresent()) {
            return new ResponseEntity<>(operation.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE /api/me/teams/{teamId}/operations/{operationId}
     *
     * TODO: Deletes a specific operation.
     * Not yet implemented.
     *
     * Response (403 FORBIDDEN): If user is not a team member.
     */
    @DeleteMapping("/{operationId}")
    public ResponseEntity<Map<String, String>> deleteOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId) throws Exception {

        throw new UnsupportedOperationException("TODO");
    }

    /**
     * GET /api/me/teams/{teamId}/operations/balance
     *
     * Retrieves the current balance of all team members.
     *
     * Response (200 OK):
     * [
     * { "firstName": "Alice", "lastName": "Smith", "balance": 123.45 },
     * { "firstName": "Bob", "lastName": "Jones", "balance": -123.45 }
     * ]
     *
     * Response (404 NOT FOUND): If no balances found.
     * Response (403 FORBIDDEN): If user is not a member.
     */
    @GetMapping("/balance")
    public ResponseEntity<List<TeamMemberBalances>> getTeamBalance(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws Exception {

        List<TeamMemberBalances> balances = teamMemberBalancesService.getBalancesByTeam(Long.valueOf(teamId));

        if (balances.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balances);
    }

    /**
     * GET /api/me/teams/{teamId}/operations/transactions
     *
     * Retrieves suggested transactions between users to settle balances.
     *
     * Response (200 OK):
     * [
     * {
     * "fromFirstName": "Alice",
     * "fromLastName": "Smith",
     * "fromEmailAddr": "alice@example.com",
     * "toFirstName": "Bob",
     * "toLastName": "Jones",
     * "toEmailAddr": "bob@example.com",
     * "amount": 75.50
     * }
     * ]
     *
     * Response (404 NOT FOUND): If no transactions found.
     * Response (403 FORBIDDEN): If user is not a member.
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTeamTransactions(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws Exception {

        List<TransactionDTO> balances = teamMemberBalancesService.getTeamTransactions(Long.valueOf(teamId));

        if (balances.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balances);
    }
}
