package me.wisisz.service;

import me.wisisz.dto.TransactionDTO;
import me.wisisz.exception.AppException.UnexpectedException;
import me.wisisz.model.TeamMemberBalances;
import me.wisisz.model.Team;
import me.wisisz.model.Person;
import me.wisisz.model.TeamMember;
import me.wisisz.repository.TeamMemberBalancesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamMemberBalancesServiceTest {

    @Mock
    private TeamMemberBalancesRepository teamMemberBalancesRepository;

    @InjectMocks
    private TeamMemberBalancesService teamMemberBalancesService;

    private Team testTeam;
    private Person testPerson1;
    private Person testPerson2;
    private TeamMember testTeamMember1;
    private TeamMember testTeamMember2;
    private TeamMemberBalances testTeamMemberBalances1;
    private TeamMemberBalances testTeamMemberBalances2;
    private List<TeamMemberBalances> balancesList;

    @BeforeEach
    void setUp() {
        // Set up test team
        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setTeamName("Test Team");

        // Set up test persons
        testPerson1 = new Person();
        testPerson1.setId(1);
        testPerson1.setEmailAddr("test1@example.com");
        testPerson1.setFname("Test1");
        testPerson1.setLname("User1");

        testPerson2 = new Person();
        testPerson2.setId(2);
        testPerson2.setEmailAddr("test2@example.com");
        testPerson2.setFname("Test2");
        testPerson2.setLname("User2");

        // Set up test team members
        testTeamMember1 = new TeamMember();
        testTeamMember1.setPerson(testPerson1);
        testTeamMember1.setTeam(testTeam);
        testTeamMember1.setDefaultShare(new BigDecimal(1));

        testTeamMember2 = new TeamMember();
        testTeamMember2.setPerson(testPerson2);
        testTeamMember2.setTeam(testTeam);
        testTeamMember2.setDefaultShare(new BigDecimal(1));

        // Set up test team member balances
        testTeamMemberBalances1 = new TeamMemberBalances(testTeam, testTeamMember1, testPerson1, testPerson1.getFname(), testPerson1.getLname(), new BigDecimal(-100));
        testTeamMemberBalances2 = new TeamMemberBalances(testTeam, testTeamMember2, testPerson2, testPerson2.getFname(), testPerson2.getLname(), new BigDecimal(100));

        // Set up balances list
        balancesList = new ArrayList<>();
        balancesList.add(testTeamMemberBalances1);
        balancesList.add(testTeamMemberBalances2);
    }

    @Test
    void getBalancesByTeam_ShouldReturnBalances() {
        when(teamMemberBalancesRepository.findByTeamId(anyLong())).thenReturn(balancesList);

        List<TeamMemberBalances> result = teamMemberBalancesService.getBalancesByTeam(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTeamMemberBalances1, result.get(0));
        assertEquals(testTeamMemberBalances2, result.get(1));
        verify(teamMemberBalancesRepository).findByTeamId(1L);
    }

    @Test
    void getTeamTransactions_ShouldReturnTransactions() throws UnexpectedException {
        when(teamMemberBalancesRepository.findByTeamId(anyLong())).thenReturn(balancesList);

        List<TransactionDTO> result = teamMemberBalancesService.getTeamTransactions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPerson1.getFname(), result.get(0).fromFirstName());
        assertEquals(testPerson1.getLname(), result.get(0).fromLastName());
        assertEquals(testPerson1.getEmailAddr(), result.get(0).fromEmailAddr());
        assertEquals(testPerson2.getFname(), result.get(0).toFirstName());
        assertEquals(testPerson2.getLname(), result.get(0).toLastName());
        assertEquals(testPerson2.getEmailAddr(), result.get(0).toEmailAddr());
        assertEquals(new BigDecimal(100), result.get(0).amount());
        verify(teamMemberBalancesRepository).findByTeamId(1L);
    }

    @Test
    void getTeamTransactions_ShouldThrowExceptionWhenInvalidBalance() {
        testTeamMemberBalances1.setBalance(new BigDecimal(100)); // Both balances are positive
        when(teamMemberBalancesRepository.findByTeamId(anyLong())).thenReturn(balancesList);

        assertThrows(UnexpectedException.class, () -> teamMemberBalancesService.getTeamTransactions(1L));
        verify(teamMemberBalancesRepository).findByTeamId(1L);
    }

    @Test
    void getTeamTransactions_ShouldReturnEmptyListWhenNoBalances() throws UnexpectedException {
        when(teamMemberBalancesRepository.findByTeamId(anyLong())).thenReturn(new ArrayList<>());

        List<TransactionDTO> result = teamMemberBalancesService.getTeamTransactions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teamMemberBalancesRepository).findByTeamId(1L);
    }
}
