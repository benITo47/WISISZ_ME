package me.wisisz.controller;

import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> categories = personService.getAllPersons();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getPersonById(@PathVariable Integer personId) {
        Optional<Person> person = personService.getPersonById(personId);
        if (person.isPresent()) {
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{personId}/teams")
    public ResponseEntity<List<Team>> getPersonTeams(@PathVariable Integer personId) {
        Optional<List<Team>> teams = personService.getPersonTeams(personId);
        if (teams.isPresent()) {
            return new ResponseEntity<>(teams.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
