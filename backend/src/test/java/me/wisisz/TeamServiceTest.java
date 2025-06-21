package me.wisisz;

import me.wisisz.dto.OperationParticipantDTO;
import me.wisisz.dto.TeamOperationRequestDTO;
import me.wisisz.dto.TeamWithMembersDTO;
import me.wisisz.exception.AppException;
import me.wisisz.model.*;
import me.wisisz.repository.*;
import me.wisisz.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void testGetAllTeams() {
        List<Team> mockTeams = List.of(new Team(), new Team());
        when(teamRepository.findAll()).thenReturn(mockTeams);

        List<Team> result = teamService.getAllTeams();

        assertEquals(2, result.size());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    public void testGetTeamWithMembersById_TeamExists() {
        Team mockTeam = new Team();
        int teamId = 1;
        int meId = 2;
        mockTeam.setMemberships(List.of());

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(operationRepository.findFirstMeOperation(teamId, meId)).thenReturn(Optional.empty());


        Optional<TeamWithMembersDTO> result = teamService.getTeamWithMembersById(teamId, meId);

        assertTrue(result.isPresent());
    }

    @Test
    public void testSaveTeam() {
        Map<String, String> data = Map.of("teamName", "My Team");
        int meId = 1;

        Person mockPerson = new Person();
        when(personRepository.findById(meId)).thenReturn(Optional.of(mockPerson));

        String result = teamService.saveTeam(meId, data);

        assertEquals("Team added", result);
        verify(teamRepository).save(any(Team.class));
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    public void testRemoveTeamMember_BalanceNotZero_ShouldThrow() {
        int teamId = 1;
        int personId = 2;

        TeamMemberBalances balance = new TeamMemberBalances();
        balance.setBalance(BigDecimal.ONE);
        when(teamMemberBalancesRepository.findByTeam_IdAndPerson_Id(teamId, personId))
                .thenReturn(Optional.of(balance));

        assertThrows(AppException.BadRequestException.class, () -> teamService.removeTeamMember(teamId, personId));
    }

    @Test
    public void testSaveTeamOperation_InvalidAmounts_ShouldThrow() {
        TeamOperationRequestDTO dto = new TeamOperationRequestDTO();
        dto.setTotalAmount("100");
        dto.setParticipants(List.of(createParticipant("1", "40"), createParticipant("2", "30"))); // Sum ≠ 100

        assertThrows(AppException.BadRequestException.class,
                () -> teamService.saveTeamOperation(1, 1, dto));
    }

    // Helper
    private OperationParticipantDTO createParticipant(String personId, String owedAmount) {
        OperationParticipantDTO p = new OperationParticipantDTO();
        p.setPersonId(personId);
        p.setOwedAmount(owedAmount);
        return p;
    }
}
