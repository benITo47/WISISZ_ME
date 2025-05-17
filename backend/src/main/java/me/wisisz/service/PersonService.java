package me.wisisz.service;

import me.wisisz.dto.TeamDTO;

import me.wisisz.model.Person;
import me.wisisz.repository.PersonRepository;
import me.wisisz.repository.OperationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private OperationRepository operationRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> getPersonById(Integer personId) {
        return personRepository.findById(personId);
    }

    public Optional<Person> getPersonByEmail(String emailAddr) {
        return personRepository.findByEmailAddr(emailAddr);
    }

    public Optional<List<TeamDTO>> getPersonTeams(Integer personId) {
        return personRepository.findById(personId).map(p -> p.getTeams().stream().map(t -> new TeamDTO(t, operationRepository.findFirstByTeamIdOrderByOperationDateDesc(t.getId()))).toList());
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }
}
