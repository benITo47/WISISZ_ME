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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader)
            {

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
                        "token", newAccessToken
                ));

            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Unauthorized"));
        }
    }

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
     * /api/me/teams, POST - Create a team
     * 
     * @param authorizationHeader - token from the authorization header.
     * @param teamData - JSON body containing following operation data: 
     *      "teamName": name of the team
     * @return ResponseEntity containing either OK status or an error message.
     */
    @PostMapping("/teams")
    public ResponseEntity<Map<String, String>> createTeam(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, String> teamData) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");

        try{
            String message = teamService.saveTeam(meId, teamData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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
     * /api/me/teams/{teamid}/members, POST - Add person to the team via email
     * 
     * @param authorizationHeader - token from the authorization header.
     * @param teamId - teamId from path.
     * @param memberData - JSON body containing following operation data: 
     *      "emailAddr": email address of the new member
     *      "shares": shares in the team
     * @return ResponseEntity containing either OK status or an error message.
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

        try{
            String message = teamService.saveTeamMember(teamId, memberData);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * /api/me/teams/{teamid}/members/{personId}, DELETE - Add person to the team via email
     * 
     * @param authorizationHeader - token from the authorization header.
     * @param teamId - teamId from path.
     * @param personId - personId from path.
     * @return ResponseEntity containing either OK status or an error message.
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

        try{
            String message = teamService.removeTeamMember(teamId, personId);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
