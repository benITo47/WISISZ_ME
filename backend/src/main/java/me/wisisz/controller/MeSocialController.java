package me.wisisz.controller;

import me.wisisz.dto.TeamWithMembersDTO;

import me.wisisz.model.Person;

import me.wisisz.service.PersonService;
import me.wisisz.service.TeamService;

import me.wisisz.exception.AppException.BadRequestException;
import me.wisisz.exception.AppException.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me")
public class MeSocialController {

    @Autowired
    private PersonService personService;

    @Autowired
    private TeamService teamService;

    /**
     * GET /api/me/profile
     * 
     * Returns the authenticated user's profile and issues a refreshed token.
     *
     * @param authorizationHeader Bearer token from Authorization header.
     * @return JSON with user's basic info and new access token or 403/404.
     *
     *         Example response:
     *         {
     *         "fname": "John",
     *         "lname": "Doe",
     *         "emailAddr": "john@example.com",
     *         "id": 123,
     *         "token": "new.jwt.token.here"
     *         }
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Integer meId = (Integer) request.getAttribute("personId");
        Optional<Person> person = personService.getPersonById(meId);

        if (person.isPresent()) {
            Person p = person.get();
            return ResponseEntity.ok(
                    Map.of(
                            "fname", p.getFname(),
                            "lname", p.getLname(),
                            "emailAddr", p.getEmailAddr(),
                            "id", p.getId()));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * GET /api/me/teams
     *
     * Returns all teams the authenticated user belongs to.
     *
     * @param authorizationHeader Bearer token
     * @return List of team objects or 404 if user is in no teams.
     *
     *         Example response:
     *         [
     *         {
     *         "id": 1,
     *         "teamName": "Flatmates",
     *         "inviteCode": "4B227777",
     *         "newestOperationDate": "2025-04-08T10:53:17.578441Z", // contains null if team has no operations
     *         "newestOperation": { // contains null if team has no operations
     *         "operationId": 4,
     *         "title": "Refund",
     *         "categoryName": "Entertainment",
     *         "totalAmount": 10.00,
     *         "members": [
     *         {
     *         "personId": 101,
     *         "fname": "Alice",
     *         "lname": "Smith",
     *         "emailAddr": "alice@example.com",
     *         "defaultShare": 1
     *         },
     *         {
     *         "personId": 102,
     *         "fname": "Bob",
     *         "lname": "Brown",
     *         "emailAddr": "bob@example.com",
     *         "defaultShare": 2
     *         }]
     *         },
     *         {
     *         "id": 2,
     *         "teamName": "Work Project",
     *         "inviteCode": "47777777",
     *         "newestOperationDate": null, 
     *         "newestOperation": null,
     *          "members": [
     *         {
     *         "personId": 101,
     *         "fname": "Alice",
     *         "lname": "Smith",
     *         "emailAddr": "alice@example.com",
     *         "defaultShare": 1
     *         }]
     *         },
     *         ...]
     *         Response (404 NOT FOUND): If person not found.
     */

    @GetMapping("/teams")
    public ResponseEntity<List<TeamWithMembersDTO>> getTeams(HttpServletRequest request)
            throws Exception {

        Integer meId = (Integer) request.getAttribute("personId");
        Optional<List<TeamWithMembersDTO>> teams = personService.getPersonTeams(meId);
        if (teams.isPresent()) {
            return new ResponseEntity<>(teams.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * POST /api/me/teams
     *
     * Creates a new team and adds the authenticated user as the first member with
     * share = 1.
     *
     * @param authorizationHeader Bearer token
     * @param teamData            JSON containing:
     *                            {
     *                            "teamName": "Trip to Iceland"
     *                            }
     * @return JSON with success message or error.
     *
     *         Example response:
     *         {
     *         "message": "Team added"
     *         }
     *
     *         Response (500 INTERNAL SERVER ERROR): On unexpected server error.
     */
    @PostMapping("/teams")
    public ResponseEntity<Map<String, String>> createTeam(
            HttpServletRequest request,
            @RequestBody Map<String, String> teamData) throws Exception {

        Integer meId = (Integer) request.getAttribute("personId");

        try {
            String message = teamService.saveTeam(meId, teamData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/me/teams/{teamId}
     *
     * Retrieves a team and its members if the user is in the team.
     *
     * @param authorizationHeader Bearer token
     * @param teamId              ID of the team
     * @return Team with list of members or 403/404.
     *
     *         Example response:
     *         {
     *         "teamId": 5,
     *         "teamName": "Road Trip",
     *         "newestOperationDate": "2025-04-08T10:53:17.578441Z", // contains null if team has no operations
     *         "newestOperation": { // contains null if team has no operations
     *         "operationId": 4,
     *         "title": "Refund",
     *         "categoryName": "Entertainment",
     *         "totalAmount": 10.00,
     *         "members": [
     *         {
     *         "personId": 101,
     *         "fname": "Alice",
     *         "lname": "Smith",
     *         "emailAddr": "alice@example.com",
     *         "defaultShare": 1
     *         },
     *         {
     *         "personId": 102,
     *         "fname": "Bob",
     *         "lname": "Brown",
     *         "emailAddr": "bob@example.com",
     *         "defaultShare": 2
     *         }
     *         ]
     *         }
     *
     *         Response (403 FORBIDDEN): If user is not a member of the team.
     *         Response (404 NOT FOUND): If team or operations not found.
     */
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamWithMembersDTO> getTeamById(
            HttpServletRequest request,
            @PathVariable Integer teamId) throws Exception {
        Integer meId = (Integer) request.getAttribute("personId");
        Optional<TeamWithMembersDTO> team = teamService.getTeamWithMembersById(teamId, meId);
        if (team.isPresent()) {
            return new ResponseEntity<>(team.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * POST /api/me/teams/{teamId}/members
     *
     * Adds a new person to the team by email address.
     *
     * @param authorizationHeader Bearer token
     * @param teamId              Team ID
     * @param memberData          JSON with:
     *                            {
     *                            "emailAddr": "newuser@example.com",
     *                            "shares": "1"
     *                            }
     * @return JSON with success message or error.
     *
     *         Example response:
     *         {
     *         "message": "Team member added"
     *         }
     *         Response (403 FORBIDDEN): If user is not a member of the team.
     *         Response (500 INTERNAL_SERVER_ERROR): Internal error
     *
     */
    @PostMapping("/teams/{teamId}/members")
    public ResponseEntity<Map<String, String>> addTeamMember(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> memberData) throws Exception {

        try {
            String message = teamService.saveTeamMember(teamId, memberData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/me/join/{inviteCode}
     *
     * Adds a new person to the team by invite code.
     *
     * @param authorizationHeader Bearer token
     * @param inviteCode          Invite code
     * @return JSON with success message or error.
     *
     *         Example response:
     *         {
     *         "message": "Team member added"
     *         }
     *         Response (403 FORBIDDEN): If user is already a member of that team
     *         Response (404 NOT FOUND): If no team with said invite code found.
     *
     */
    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<Map<String, String>> addTeamMemberInviteCode(
            HttpServletRequest request,
            @PathVariable String inviteCode) throws BadRequestException, NotFoundException {
        
        Integer meId = (Integer) request.getAttribute("personId");
        String message = teamService.saveTeamMemberInviteCode(inviteCode, meId);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    /**
     * DELETE /api/me/teams/{teamId}/members/{personId}
     *
     * Removes a person from the team.
     *
     * @param authorizationHeader Bearer token
     * @param teamId              Team ID
     * @param personId            ID of person to remove
     * @return JSON with success message or error.
     *
     *         Example response:
     *         {
     *         "message": "Team member removed"
     *         }
     *
     *         Response (400 BAD_REQUEST): If user's balanse is not settled
     *         Response (403 FORBIDDEN): If user is not a member of the team.
     *         Response (500 INTERNAL_SERVER_ERROR): Internal error
     *
     */
    @DeleteMapping("/teams/{teamId}/members/{personId}")
    public ResponseEntity<Map<String, String>> removeTeamMember(
            HttpServletRequest request,
            @PathVariable Integer teamId,
            @PathVariable Integer personId) throws BadRequestException {

        String message = teamService.removeTeamMember(teamId, personId);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }
}
