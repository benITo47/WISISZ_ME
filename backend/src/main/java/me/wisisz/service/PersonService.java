package me.wisisz.service;

import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> getPersonById(Integer personId) {
        return personRepository.findById(personId);
    }

    public Optional<List<Team>> getPersonTeams(Integer personId) {
        return personRepository.findById(personId).map(p -> p.getTeams());
    }
}
