package me.wisisz.service;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.OperationSummaryDTO;
import me.wisisz.dto.TeamOperationRequestDTO;
import me.wisisz.dto.OperationParticipantDTO;
import me.wisisz.dto.OperationDetailDTO;
import me.wisisz.dto.TeamWithMembersDTO;

import me.wisisz.model.Operation;
import me.wisisz.model.OperationEntry;
import me.wisisz.model.Team;
import me.wisisz.model.Person;
import me.wisisz.model.TeamMember;
import me.wisisz.model.TeamMemberBalances;
import me.wisisz.model.Category;

import me.wisisz.repository.PersonRepository;
import me.wisisz.repository.TeamMemberRepository;
import me.wisisz.repository.TeamMemberBalancesRepository;
import me.wisisz.repository.TeamRepository;
import me.wisisz.repository.OperationRepository;
import me.wisisz.repository.OperationEntryRepository;
import me.wisisz.repository.CategoryRepository;

import me.wisisz.exception.AppException.UserNotInTeamException;
import me.wisisz.exception.AppException.BadRequestException;
import me.wisisz.exception.AppException.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberBalancesRepository teamMemberBalancesRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationEntryRepository operationEntryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private Person testPerson;
    private TeamMember testTeamMember;
    private Operation testOperation;
    private OperationEntry testOperationEntry;
    private Category testCategory;
    private TeamMemberBalances testTeamMemberBalances;
    private List<Team> teamList;
    private List<Operation> operationList;
    private List<OperationEntry> operationEntryList;
    private Map<String, String> teamData;
    private Map<String, String> memberData;
    private TeamOperationRequestDTO operationRequestDTO;

    @BeforeEach
    void setUp() {
        // Set up test team
        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setTeamName("Test Team");
        testTeam.setInviteCode("ABC123");
        testTeam.setMemberships(new ArrayList<>());

        // Set up test person
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setEmailAddr("test@example.com");
        testPerson.setPasswordHash("password");
        testPerson.setFname("Test");
        testPerson.setLname("User");

        // Set up test team member
        testTeamMember = new TeamMember();
        testTeamMember.setPerson(testPerson);
        testTeamMember.setTeam(testTeam);
        testTeamMember.setDefaultShare(new BigDecimal(1));

        // Set up test category
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setCategoryName("Test Category");

        // Set up test operation
        testOperation = new Operation();
        testOperation.setId(1);
        testOperation.setTeam(testTeam);
        testOperation.setTitle("Test Operation");
        testOperation.setDescription("Test Description");
        testOperation.setTotalAmount(new BigDecimal(100));
        testOperation.setOperationDate(OffsetDateTime.now());
        testOperation.setCategory(testCategory);
        testOperation.setCurrencyCode("USD");
        testOperation.setOperationType("expense");
        testOperation.setEntries(new ArrayList<>());

        // Set up test operation entry
        testOperationEntry = new OperationEntry();
        testOperationEntry.setId(1);
        testOperationEntry.setOperation(testOperation);
        testOperationEntry.setTeamMember(testTeamMember);
        testOperationEntry.setAmount(new BigDecimal(100));

        // Set up test team member balances
        testTeamMemberBalances = new TeamMemberBalances(testTeam, testTeamMember, testPerson, testPerson.getFname(), testPerson.getLname(), BigDecimal.ZERO);

        // Set up team list
        teamList = new ArrayList<>();
        teamList.add(testTeam);

        // Set up operation list
        operationList = new ArrayList<>();
        operationList.add(testOperation);
        testTeam.setOperations(operationList);

        // Set up operation entry list
        operationEntryList = new ArrayList<>();
        operationEntryList.add(testOperationEntry);

        // Add operation entry to operation
        testOperation.getEntries().add(testOperationEntry);

        // Add team member to team
        testTeam.getMemberships().add(testTeamMember);

        // Set up team data
        teamData = new HashMap<>();
        teamData.put("teamName", "New Team");

        // Set up member data
        memberData = new HashMap<>();
        memberData.put("emailAddr", "test@example.com");
        memberData.put("shares", "1");

        // Set up operation request DTO
        List<OperationParticipantDTO> participants = new ArrayList<>();
        OperationParticipantDTO participant1 = new OperationParticipantDTO();
        participant1.setPersonId("1");
        participant1.setPaidAmount("50");
        participants.add(participant1);

        OperationParticipantDTO participant2 = new OperationParticipantDTO();
        participant2.setPersonId("2");
        participant2.setPaidAmount("50");
        participants.add(participant2);

        operationRequestDTO = new TeamOperationRequestDTO();
        operationRequestDTO.setTitle("New Operation");
        operationRequestDTO.setDescription("New Description");
        operationRequestDTO.setTotalAmount("100");
        operationRequestDTO.setCategoryId("1");
        operationRequestDTO.setCurrencyCode("USD");
        operationRequestDTO.setOperationType("expense");
        operationRequestDTO.setParticipants(participants);
    }

    @Test
    void getAllTeams_ShouldReturnAllTeams() {
        when(teamRepository.findAll()).thenReturn(teamList);

        List<Team> result = teamService.getAllTeams();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTeam, result.get(0));
        verify(teamRepository).findAll();
    }

    @Test
    void getTeamWithMembersById_ShouldReturnTeamWhenExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(testTeam));
        when(operationRepository.findFirstMeOperation(anyInt(), anyInt())).thenReturn(Optional.of(testOperation));

        Optional<TeamWithMembersDTO> result = teamService.getTeamWithMembersById(1, 1);

        assertTrue(result.isPresent());
        assertEquals(testTeam.getId(), result.get().teamId());
        assertEquals(testTeam.getTeamName(), result.get().teamName());
        verify(teamRepository).findById(1);
        verify(operationRepository).findFirstMeOperation(1, 1);
    }

    @Test
    void getTeamWithMembersById_ShouldReturnEmptyWhenNotExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TeamWithMembersDTO> result = teamService.getTeamWithMembersById(999, 1);

        assertFalse(result.isPresent());
        verify(teamRepository).findById(999);
        verify(operationRepository, never()).findFirstMeOperation(anyInt(), anyInt());
    }

    @Test
    void getTeamOperationsView_ShouldReturnOperationsWhenTeamExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(testTeam));

        Optional<List<OperationDTO>> result = teamService.getTeamOperationsView(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testOperation.getId(), result.get().get(0).operationId());
        verify(teamRepository).findById(1);
    }

    @Test
    void getTeamOperationsView_ShouldReturnEmptyWhenTeamNotExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<List<OperationDTO>> result = teamService.getTeamOperationsView(999);

        assertFalse(result.isPresent());
        verify(teamRepository).findById(999);
    }

    @Test
    void getTeamOperationsSummaryView_ShouldReturnSummaryWhenTeamExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(testTeam));

        Optional<List<OperationSummaryDTO>> result = teamService.getTeamOperationsSummaryView(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testOperation.getId(), result.get().get(0).operationId());
        verify(teamRepository).findById(1);
    }

    @Test
    void getTeamOperationsSummaryView_ShouldReturnEmptyWhenTeamNotExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<List<OperationSummaryDTO>> result = teamService.getTeamOperationsSummaryView(999);

        assertFalse(result.isPresent());
        verify(teamRepository).findById(999);
    }

    @Test
    void getSingleTeamOperationView_ShouldReturnOperationWhenExists() {
        when(operationRepository.findByIdAndTeamId(anyInt(), anyInt())).thenReturn(Optional.of(testOperation));

        Optional<OperationDetailDTO> result = teamService.getSingleTeamOperationView(1, 1);

        assertTrue(result.isPresent());
        assertEquals(testOperation.getTitle(), result.get().title());
        verify(operationRepository).findByIdAndTeamId(1, 1);
    }

    @Test
    void getSingleTeamOperationView_ShouldReturnEmptyWhenNotExists() {
        when(operationRepository.findByIdAndTeamId(anyInt(), anyInt())).thenReturn(Optional.empty());

        Optional<OperationDetailDTO> result = teamService.getSingleTeamOperationView(1, 999);

        assertFalse(result.isPresent());
        verify(operationRepository).findByIdAndTeamId(999, 1);
    }

    @Test
    void getTeamOperations_ShouldReturnOperationsWhenTeamExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(testTeam));

        Optional<List<Operation>> result = teamService.getTeamOperations(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testOperation, result.get().get(0));
        verify(teamRepository).findById(1);
    }

    @Test
    void getTeamOperations_ShouldReturnEmptyWhenTeamNotExists() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<List<Operation>> result = teamService.getTeamOperations(999);

        assertFalse(result.isPresent());
        verify(teamRepository).findById(999);
    }

    @Test
    void isPersonInTeam_ShouldReturnTrueWhenPersonInTeam() {
        when(teamMemberRepository.existsByPersonIdAndTeamId(anyInt(), anyInt())).thenReturn(true);

        boolean result = teamService.isPersonInTeam(1, 1);

        assertTrue(result);
        verify(teamMemberRepository).existsByPersonIdAndTeamId(1, 1);
    }

    @Test
    void isPersonInTeam_ShouldReturnFalseWhenPersonNotInTeam() {
        when(teamMemberRepository.existsByPersonIdAndTeamId(anyInt(), anyInt())).thenReturn(false);

        boolean result = teamService.isPersonInTeam(999, 1);

        assertFalse(result);
        verify(teamMemberRepository).existsByPersonIdAndTeamId(999, 1);
    }

    @Test
    void saveTeam_ShouldReturnSuccessMessage() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(testTeamMember);

        String result = teamService.saveTeam(1, teamData);

        assertEquals("Team added", result);
        verify(teamRepository).save(any(Team.class));
        verify(personRepository).findById(1);
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    void saveTeamMember_ShouldReturnSuccessMessage() {
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(testTeam));
        when(personRepository.findByEmailAddr(anyString())).thenReturn(Optional.of(testPerson));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(testTeamMember);

        String result = teamService.saveTeamMember(1, memberData);

        assertEquals("Team member added", result);
        verify(teamRepository).findById(1);
        verify(personRepository).findByEmailAddr("test@example.com");
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    void saveTeamMemberInviteCode_ShouldReturnSuccessMessage() throws BadRequestException, NotFoundException {
        when(teamRepository.findByInviteCode(anyString())).thenReturn(Optional.of(testTeam));
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        when(teamMemberRepository.findByPerson_IdAndTeam_Id(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(testTeamMember);

        String result = teamService.saveTeamMemberInviteCode("ABC123", 1);

        assertEquals("Team member added", result);
        verify(teamRepository).findByInviteCode("ABC123");
        verify(personRepository).findById(1);
        verify(teamMemberRepository).findByPerson_IdAndTeam_Id(1, 1);
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    void saveTeamMemberInviteCode_ShouldThrowExceptionWhenTeamNotFound() {
        when(teamRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> teamService.saveTeamMemberInviteCode("INVALID", 1));
        verify(teamRepository).findByInviteCode("INVALID");
        verify(personRepository, never()).findById(anyInt());
        verify(teamMemberRepository, never()).save(any(TeamMember.class));
    }

    @Test
    void saveTeamMemberInviteCode_ShouldThrowExceptionWhenAlreadyMember() {
        when(teamRepository.findByInviteCode(anyString())).thenReturn(Optional.of(testTeam));
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        when(teamMemberRepository.findByPerson_IdAndTeam_Id(anyInt(), anyInt())).thenReturn(Optional.of(testTeamMember));

        assertThrows(BadRequestException.class, () -> teamService.saveTeamMemberInviteCode("ABC123", 1));
        verify(teamRepository).findByInviteCode("ABC123");
        verify(personRepository).findById(1);
        verify(teamMemberRepository).findByPerson_IdAndTeam_Id(1, 1);
        verify(teamMemberRepository, never()).save(any(TeamMember.class));
    }

    @Test
    void removeTeamMember_ShouldReturnSuccessMessage() throws BadRequestException {
        testTeamMemberBalances.setBalance(BigDecimal.ZERO);
        when(teamMemberBalancesRepository.findByTeam_IdAndPerson_Id(anyInt(), anyInt())).thenReturn(Optional.of(testTeamMemberBalances));
        when(teamMemberRepository.findByPerson_IdAndTeam_Id(anyInt(), anyInt())).thenReturn(Optional.of(testTeamMember));

        String result = teamService.removeTeamMember(1, 1);

        assertEquals("Team member removed", result);
        verify(teamMemberBalancesRepository).findByTeam_IdAndPerson_Id(1, 1);
        verify(teamMemberRepository).findByPerson_IdAndTeam_Id(1, 1);
        verify(teamMemberRepository).delete(testTeamMember);
    }

    @Test
    void removeTeamMember_ShouldThrowExceptionWhenBalanceNotFound() {
        when(teamMemberBalancesRepository.findByTeam_IdAndPerson_Id(anyInt(), anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> teamService.removeTeamMember(1, 1));
        verify(teamMemberBalancesRepository).findByTeam_IdAndPerson_Id(1, 1);
        verify(teamMemberRepository, never()).findByPerson_IdAndTeam_Id(anyInt(), anyInt());
        verify(teamMemberRepository, never()).delete(any(TeamMember.class));
    }

    @Test
    void removeTeamMember_ShouldThrowExceptionWhenBalanceNotSettled() {
        testTeamMemberBalances.setBalance(new BigDecimal(100));
        when(teamMemberBalancesRepository.findByTeam_IdAndPerson_Id(anyInt(), anyInt())).thenReturn(Optional.of(testTeamMemberBalances));

        assertThrows(BadRequestException.class, () -> teamService.removeTeamMember(1, 1));
        verify(teamMemberBalancesRepository).findByTeam_IdAndPerson_Id(1, 1);
        verify(teamMemberRepository, never()).findByPerson_IdAndTeam_Id(anyInt(), anyInt());
        verify(teamMemberRepository, never()).delete(any(TeamMember.class));
    }

    @Test
    void deleteTeamById_ShouldDeleteTeam() {
        when(teamRepository.existsById(anyInt())).thenReturn(true);
        doNothing().when(teamRepository).deleteById(anyInt());

        teamService.deleteTeamById(1);

        verify(teamRepository).existsById(1);
        verify(teamRepository).deleteById(1);
    }

    @Test
    void deleteTeamById_ShouldThrowExceptionWhenTeamNotExists() {
        when(teamRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> teamService.deleteTeamById(999));
        verify(teamRepository).existsById(999);
        verify(teamRepository, never()).deleteById(anyInt());
    }
}
