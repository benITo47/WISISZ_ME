package me.wisisz.controller;

import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.service.AuthenticationService;
import me.wisisz.service.PersonService;
import me.wisisz.service.TeamService;

import me.wisisz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private AuthenticationService authenticationService;

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
    public ResponseEntity<?> getProfile(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Unauthorized"));
        }

        try {
            Map<String, Object> personInfo = authenticationService.validateToken(authorizationHeader);
            Optional<Person> person = personService.getPersonById((Integer) personInfo.get("personId"));

            if (person.isPresent()) {
                Person p = person.get();
                String newAccessToken = JwtUtil.generateAccessToken(p.getId());
                return ResponseEntity.ok(Map.of(
                        "fname", p.getFname(),
                        "lname", p.getLname(),
                        "emailAddr", p.getEmailAddr(),
                        "id", p.getId(),
                        "token", newAccessToken));

            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Unauthorized"));
        }
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
     *         "teamName": "Flatmates"
     *         },
     *         {
     *         "id": 2,
     *         "teamName": "Work Project"
     *         }
     *         ]
     *         Response (404 NOT FOUND): If person not found.
     */

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeams(@RequestHeader("Authorization") String authorizationHeader)
            throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Optional<List<Team>> teams = personService.getPersonTeams((Integer) meInfo.get("personId"));
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
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, String> teamData) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");

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
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<TeamWithMembersDTO> team = teamService.getTeamWithMembersById(teamId);
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
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> memberData) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String message = teamService.saveTeamMember(teamId, memberData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /api/me/teams/{teamId}/members/{personId}
     *
     * Removes a person from the team.
     * 
     * NOTE: Does not currently validate whether balances are settled.
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
     *         Response (403 FORBIDDEN): If user is not a member of the team.
     *         Response (500 INTERNAL_SERVER_ERROR): Internal error
     *
     */
    @DeleteMapping("/teams/{teamId}/members/{personId}")
    public ResponseEntity<Map<String, String>> removeTeamMember(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @PathVariable Integer personId) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String message = teamService.removeTeamMember(teamId, personId);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
