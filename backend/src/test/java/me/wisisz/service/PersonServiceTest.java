package me.wisisz.service;

import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.model.Person;
import me.wisisz.model.Team;
import me.wisisz.model.TeamMember;
import me.wisisz.model.Operation;
import me.wisisz.model.Category;
import me.wisisz.repository.PersonRepository;
import me.wisisz.repository.OperationRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;
    private Team testTeam;
    private TeamMember testTeamMember;
    private Operation testOperation;
    private List<Person> personList;

    @BeforeEach
    void setUp() {
        // Set up test person
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setEmailAddr("test@example.com");
        testPerson.setPasswordHash("password");
        testPerson.setFname("Test");
        testPerson.setLname("User");

        // Set up test team
        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setTeamName("Test Team");
        testTeam.setMemberships(new ArrayList<>());

        // Set up test team member
        testTeamMember = new TeamMember();
        testTeamMember.setPerson(testPerson);
        testTeamMember.setTeam(testTeam);

        // Set up test category
        Category testCategory = new Category();
        testCategory.setId(1);
        testCategory.setCategoryName("Test Category");

        // Set up test operation
        testOperation = new Operation();
        testOperation.setId(1);
        testOperation.setTeam(testTeam);
        testOperation.setCategory(testCategory);
        testOperation.setTitle("Test Operation");
        testOperation.setDescription("Test Description");
        testOperation.setTotalAmount(new BigDecimal(100));
        testOperation.setOperationDate(OffsetDateTime.now());
        testOperation.setCurrencyCode("USD");
        testOperation.setOperationType("expense");
        testOperation.setEntries(new ArrayList<>());

        // Set up person with memberships
        List<TeamMember> memberships = new ArrayList<>();
        memberships.add(testTeamMember);
        testPerson.setMemberships(memberships);

        // Add team member to team
        testTeam.getMemberships().add(testTeamMember);

        // Set up person list
        personList = new ArrayList<>();
        personList.add(testPerson);
    }

    @Test
    void getAllPersons_ShouldReturnAllPersons() {
        when(personRepository.findAll()).thenReturn(personList);

        List<Person> result = personService.getAllPersons();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPerson, result.get(0));
    }

    @Test
    void getPersonById_ShouldReturnPersonWhenExists() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));

        Optional<Person> result = personService.getPersonById(1);

        assertTrue(result.isPresent());
        assertEquals(testPerson, result.get());
    }

    @Test
    void getPersonById_ShouldReturnEmptyWhenNotExists() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Person> result = personService.getPersonById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void getPersonByEmail_ShouldReturnPersonWhenExists() {
        when(personRepository.findByEmailAddr(anyString())).thenReturn(Optional.of(testPerson));

        Optional<Person> result = personService.getPersonByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testPerson, result.get());
    }

    @Test
    void getPersonByEmail_ShouldReturnEmptyWhenNotExists() {
        when(personRepository.findByEmailAddr(anyString())).thenReturn(Optional.empty());

        Optional<Person> result = personService.getPersonByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void getPersonTeams_ShouldReturnTeamsWhenPersonExists() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        when(operationRepository.findFirstByTeamIdOrderByOperationDateDesc(anyInt())).thenReturn(Optional.of(testOperation));

        Optional<List<TeamWithMembersDTO>> result = personService.getPersonTeams(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testTeam.getId(), result.get().get(0).teamId());
    }

    @Test
    void getPersonTeams_ShouldReturnEmptyWhenPersonNotExists() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<List<TeamWithMembersDTO>> result = personService.getPersonTeams(999);

        assertFalse(result.isPresent());
    }

    @Test
    void savePerson_ShouldReturnSavedPerson() {
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        Person result = personService.savePerson(testPerson);

        assertNotNull(result);
        assertEquals(testPerson, result);
        verify(personRepository).save(testPerson);
    }
}
