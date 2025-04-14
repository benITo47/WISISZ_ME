package me.wisisz.controller;

import me.wisisz.dto.OperationDTO;
import me.wisisz.service.AuthenticationService;
import me.wisisz.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me/teams/{teamId}/operations")
public class MeOperationsController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("")
    public ResponseEntity<List<OperationDTO>> getTeamOperations(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<List<OperationDTO>> operations = teamService.getTeamOperationsView(teamId);
        if (operations.isPresent()) {
            return new ResponseEntity<>(operations.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * /api/me/teams/{teamId}/operations, POST - Add an operation; automatically adds according operationEntries.
     * 
     * @param authorizationHeader - token from the authorization header.
     * @param teamId - teamId from path.
     * @param operationData - Map<String, String> containing following operation data: 
     *      "totalAmount": amount to be paid in an operation
     *      "cathegoryId": ID of operation cathegory
     *      "currencyCode": code of operation currency
     *      "description": operation description
     *      "operationType": "expense", "income" or "transfer"
     *      ["recipientID": if operationType is "transfer", ID of the recipient]
     * @return ResponseEntity containing either OK status or an error message.
     */
    @PostMapping("")
    public ResponseEntity<Map<String, String>> addOperation(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> operationData) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try{
            String message = teamService.saveTeamOperation(meId, teamId, operationData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("")
    public ResponseEntity<Map<String, String>> updateOperation(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> operationData) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        throw new UnsupportedOperationException("TODO");
    }

    @DeleteMapping("/{operationId}")
    public ResponseEntity<Map<String, String>> deleteOperation(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @PathVariable Integer operationId) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        throw new UnsupportedOperationException("TODO");
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getTeamBalance(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        throw new UnsupportedOperationException("TODO");
    }
}
