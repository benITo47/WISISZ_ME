package me.wisisz.controller;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.TeamMemberDTO;
import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.service.AuthenticationService;
import me.wisisz.service.PersonService;
import me.wisisz.service.TeamService;

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
    public ResponseEntity<Person> getProfile(@RequestHeader("Authorization") String authorizationHeader)
            throws Exception {

        Map<String, Object> personInfo = authenticationService.validateToken(authorizationHeader);
        Optional<Person> person = personService.getPersonById((Integer) personInfo.get("personId"));

        if (person.isPresent()) {
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    @PostMapping("/teams")
    public ResponseEntity<Map<String, String>> createTeam(
            @RequestHeader("Authorization") String authorizationHeader,
            Map<String, String> teamInfo) {

        throw new UnsupportedOperationException("TODO");
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

    @PostMapping("/teams/{teamId}/members")
    public ResponseEntity<List<TeamMemberDTO>> addTeamMember(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @RequestBody Map<String, String> emailAddr) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        throw new UnsupportedOperationException("TODO");
    }

    @DeleteMapping("/teams/{teamId}/members/{personId}")
    public ResponseEntity<List<TeamMemberDTO>> removeTeamMember(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer teamId,
            @PathVariable Integer personId,
            @RequestBody Map<String, String> emailAddr) throws Exception {

        Map<String, Object> meInfo = authenticationService.validateToken(authorizationHeader);
        Integer meId = (Integer) meInfo.get("personId");
        if (!teamService.isPersonInTeam(meId, teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        throw new UnsupportedOperationException("TODO");
    }
}
