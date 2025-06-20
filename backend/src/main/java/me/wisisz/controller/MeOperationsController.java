package me.wisisz.controller;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.OperationSummaryDTO;
import me.wisisz.dto.OperationDetailDTO;
import me.wisisz.dto.TransactionDTO;
import me.wisisz.dto.TeamOperationRequestDTO;
import me.wisisz.dto.TeamOperationsOverviewDTO;
import me.wisisz.model.TeamMemberBalances;
import me.wisisz.service.TeamMemberBalancesService;
import me.wisisz.service.TeamService;

import me.wisisz.exception.AppException.UserNotInTeamException;
import me.wisisz.exception.AppException.BadRequestException;
import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.exception.AppException.UnexpectedException;

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
     * <p>
     * Retrieves all operations for a given team where the authenticated user is a
     * member.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * <p>
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
     * <p>
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
     * <p>
     * Retrieves basic summary for all operations for a given team where the authenticated user is a
     * member.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * <p>
     * Response (200 OK):
     * [
     * {
     * "operationId": 101,
     * "title": "Lunch",
     * "totalAmount": "250.0",
     * "currencyCode": "USD",
     * "categoryName": "Restaurant",
     * },
     * ...]
     * <p>
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (404 NOT FOUND): If team or operations not found.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<OperationSummaryDTO>> getTeamOperationsSummary(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws UserNotInTeamException, NotFoundException {

        Optional<List<OperationSummaryDTO>> operations = teamService.getTeamOperationsSummaryView(teamId);
        if (operations.isEmpty()) {
            throw new NotFoundException("Operation not found in database");
        }

        return new ResponseEntity<>(operations.get(), HttpStatus.OK);
    }

    
     /**
     * GET /api/me/teams/{teamId}/operations/overview
     *
     * Retrieves total amount paid by the group and its distribution among categories
     *
     * Headers:
     * - Authorization: Bearer <JWT>
     *
     * Path Variables:
     * - teamId (Integer): ID of the team
     *
     * Response (200 OK):
     * {
     * "totalAmount": "250.0",
     * "amountByCategory": {
     * "food": "150.0",
     * "entertainment": "100.0"
     * ...
     * }
     * }
     *
     * Response (404 NOT FOUND): If team not found.
     * Response (403 FORBIDDEN): If user is not a member of the team.
     */
    @GetMapping("/overview")
    public ResponseEntity<TeamOperationsOverviewDTO> getTeamOperationsOverview(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws NotFoundException {
        TeamOperationsOverviewDTO overview = teamService.getTeamOperationsOverview(teamId);
        return new ResponseEntity<>(overview, HttpStatus.OK);
    }

    /**
     * POST /api/me/teams/{teamId}/operations
     * <p>
     * Adds a new operation to the team.
     * Automatically generates operation entries based on the input.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * <p>
     * Request Body (JSON):
     * {
     * "title": "Lunch",
     * "totalAmount": "250.00", // paid by the person with token
     * "categoryId": "3",
     * "currencyCode": "USD",
     * "description": "Post-project lunch gathering",
     * "operationType": "expense", // or "income", "transfer"
     * "participants": [
     * {
     * "personId": "1",
     * "owedAmount": "50.00",
     * },
     * ...]
     * }
     * <p>
     * Response (200 OK):
     * { "message": "Operation successfully saved" }
     * <p>
     * Response (400 BAD REQUEST): If incorrect data passed
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (500 INTERNAL SERVER ERROR): On unexpected server error.
     */
    @PostMapping("")
    public ResponseEntity<Map<String, String>> addOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @RequestBody TeamOperationRequestDTO operationData) throws UserNotInTeamException, BadRequestException, UnexpectedException {

        Integer meId = (Integer) request.getAttribute("personId");
        String message = teamService.saveTeamOperation(meId, teamId, operationData);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    /**
     * PUT /api/me/teams/{teamId}/operations/{operationId}
     * <p>
     * Updates an existing operation.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * - operationId (Integer): ID of the operation
     * <p>
     * Request Body (JSON):
     * {
     * "title": "Lunch",
     * "totalAmount": "250.00", // paid by the person with token
     * "categoryId": "3",
     * "currencyCode": "USD",
     * "description": "Post-project lunch gathering",
     * "operationType": "expense", // or "income", "transfer"
     * "participants": [
     * {
     * "personId": "1",
     * "owedAmount": "50.00",
     * },
     * ...]
     * }
     * <p>
     * Response (200 OK):
     * { "message": "Operation successfully updated" }
     * <p>
     * Response (400 BAD REQUEST): If incorrect data passed
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (500 INTERNAL SERVER ERROR): On unexpected server error.
     */
    @PutMapping("/{operationId}")
    public ResponseEntity<Map<String, String>> updateOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId,
            @RequestBody TeamOperationRequestDTO operationData) throws Exception {

        Integer meId = (Integer) request.getAttribute("personId");
        String message = teamService.updateTeamOperation(meId, teamId, operationId, operationData);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    /**
     * GET /api/me/teams/{teamId}/operations/{operationId}
     * <p>
     * Retrieves specific operation for a given team where the authenticated user is a
     * member.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * - operationId (Integer): ID of the operation
     * <p>
     * Response (200 OK):
     * {
     * "title": "Lunch",
     * "description": "Post-project lunch gathering",
     * "totalAmount": "250.00",
     * "operationDate": "2025-04-08T10:53:17.578441Z",
     * "categoryName": "Restaurant",
     * "participants": [
     * {
     * "personId": ...,
     * "fname": ...,
     * "lname": ...,
     * "emailAddr": ...,
     * "paidAmount": ...,
     * "currencyCode": ...,
     * },
     * ...]
     * }
     * <p>
     * Response (403 FORBIDDEN): If user is not a member of the team.
     * Response (404 NOT FOUND): If team or operation not found.
     */
    @GetMapping("/{operationId}")
    public ResponseEntity<OperationDetailDTO> getOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId) throws NotFoundException, UserNotInTeamException {

        Optional<OperationDetailDTO> operation = teamService.getSingleTeamOperationView(teamId, operationId);
        if (operation.isEmpty()) {
            throw new NotFoundException("Operation not found in database");
        }
        return new ResponseEntity<>(operation.get(), HttpStatus.OK);
    }

    /**
     * DELETE /api/me/teams/{teamId}/operations/{operationId}
     * <p>
     * Deletes specific operation.
     * <p>
     * Headers:
     * - Authorization: Bearer <JWT>
     * <p>
     * Path Variables:
     * - teamId (Integer): ID of the team
     * - operationId (Integer): ID of the operation
     * <p>
     * Response (200 OK):
     * { "message": "Operation removed" }
     * <p>
     * Response (403 FORBIDDEN): If user is not a team member.
     * Response (404 NOT FOUND): If operation cannot be found.
     */
    @DeleteMapping("/{operationId}")
    public ResponseEntity<Map<String, String>> deleteOperation(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId) throws NotFoundException {

        Integer meId = (Integer) request.getAttribute("personId");
        String message = teamService.removeTeamOperation(meId, teamId, operationId);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    /**
     * GET /api/me/teams/{teamId}/operations/balance
     * <p>
     * Retrieves the current balance of all team members.
     * <p>
     * Response (200 OK):
     * [
     * { "firstName": "Alice", "lastName": "Smith", "balance": 123.45 },
     * { "firstName": "Bob", "lastName": "Jones", "balance": -123.45 }
     * ]
     * <p>
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
     * <p>
     * Retrieves suggested transactions between users to settle balances.
     * <p>
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
     * <p>
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
