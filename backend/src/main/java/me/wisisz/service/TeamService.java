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
import me.wisisz.model.Category;

import me.wisisz.repository.PersonRepository;
import me.wisisz.repository.TeamMemberRepository;
import me.wisisz.repository.TeamRepository;
import me.wisisz.repository.OperationRepository;
import me.wisisz.repository.OperationEntryRepository;
import me.wisisz.repository.CategoryRepository;

import me.wisisz.exception.AppException.UserNotInTeamException;
import me.wisisz.exception.AppException.BadRequestException;
import me.wisisz.exception.AppException.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private OperationEntryRepository operationEntryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<TeamWithMembersDTO> getTeamWithMembersById(Integer teamId, Integer meId) {
        return teamRepository.findById(teamId).map(t -> new TeamWithMembersDTO(t, operationRepository.findFirstMeOperation(teamId, meId)));
    }

    public Optional<List<OperationDTO>> getTeamOperationsView(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations().stream().map(o -> new OperationDTO(o)).toList());
    }

    public Optional<List<OperationSummaryDTO>> getTeamOperationsSummaryView(Integer teamId) {
        return teamRepository.findById(teamId).map(t -> t.getOperations().stream().map(o -> new OperationSummaryDTO(o)).toList());
    }

    public Optional<OperationDetailDTO> getSingleTeamOperationView(Integer teamId, Integer operationId) {
        return operationRepository.findByIdAndTeamId(operationId, teamId).map(o -> new OperationDetailDTO(o));
    }

    public Optional<List<Operation>> getTeamOperations(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations());
    }

    public boolean isPersonInTeam(Integer personId, Integer teamId) {
        return teamMemberRepository.existsByPersonIdAndTeamId(personId, teamId);
    }

    public String saveTeam(Integer meId, Map<String, String> data) {
        Team newTeam = new Team();
        newTeam.setTeamName(data.get("teamName"));
        teamRepository.save(newTeam);

        TeamMember newTeamMember = new TeamMember();
        newTeamMember.setTeam(newTeam);
        Optional<Person> person = personRepository.findById(meId);
        newTeamMember.setPerson(person.get());
        newTeamMember.setDefaultShare(new BigDecimal(1));
        teamMemberRepository.save(newTeamMember);

        return "Team added";
    }

    public String saveTeamMember(Integer teamId, Map<String, String> data) {

        TeamMember newTeamMember = new TeamMember();
        Optional<Team> team = teamRepository.findById(teamId);
        newTeamMember.setTeam(team.get());
        Optional<Person> person = personRepository.findByEmailAddr(data.get("emailAddr"));
        newTeamMember.setPerson(person.get());
        newTeamMember.setDefaultShare(new BigDecimal(data.get("shares")));
        teamMemberRepository.save(newTeamMember);

        return "Team member added";
    }

    public String saveTeamMemberInviteCode(String inviteCode, Integer personId) throws BadRequestException, NotFoundException {
        TeamMember newTeamMember = new TeamMember();
        Optional<Team> team = teamRepository.findByInviteCode(inviteCode);
        if (team.isEmpty()){
            throw new NotFoundException("Team not found for invite code " + inviteCode);
        }
        newTeamMember.setTeam(team.get());
        Optional<Person> person = personRepository.findById(personId);
        newTeamMember.setPerson(person.get());
        newTeamMember.setDefaultShare(new BigDecimal(1));

        boolean alreadyMember = teamMemberRepository
                .findByPerson_IdAndTeam_Id(personId, team.get().getId())
                .isPresent();
        if (alreadyMember) {
            throw new BadRequestException("Person is already a member of the team");
        }

        teamMemberRepository.save(newTeamMember);

        return "Team member added";
    }

    public String removeTeamMember(Integer teamId, Integer personId) { // TODO: failsave; can't remove
                                                                                        // if balance not settled
        Optional<TeamMember> member = teamMemberRepository.findByPerson_IdAndTeam_Id(personId, teamId);
        teamMemberRepository.delete(member.get());
        return "Team member removed";
    }

    public String saveTeamOperation(Integer meId, Integer teamId, TeamOperationRequestDTO data) throws BadRequestException, UserNotInTeamException {

        BigDecimal totalAmount = new BigDecimal(data.getTotalAmount());

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        Category category = categoryRepository.findById(Integer.parseInt(data.getCategoryId()))
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Operation newOperation = new Operation();
        newOperation.setTeam(team);
        newOperation.setOperationDate(OffsetDateTime.now());
        newOperation.setDescription(data.getDescription());
        newOperation.setTitle(data.getTitle());
        newOperation.setTotalAmount(totalAmount);
        newOperation.setCategory(category);
        newOperation.setCurrencyCode(data.getCurrencyCode());
        newOperation.setOperationType(data.getOperationType());

        List<OperationParticipantDTO> participants = data.getParticipants();
        boolean senderIncluded = participants.stream()
            .anyMatch(p -> Integer.valueOf(p.getPersonId()).equals(meId));
        if (!senderIncluded) {
            throw new BadRequestException("Current user (sender) is not listed among participants");
        }

        List<OperationEntry> allOperationEntries = new ArrayList<>();
        
        switch (data.getOperationType()) {
            case "expense": {
                BigDecimal totalShare = participants.stream()
                    .map(p -> new BigDecimal(p.getShare()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal oneShare = totalAmount.divide(totalShare, 2, RoundingMode.HALF_UP);

                for (OperationParticipantDTO participant : participants) {
                    Integer personId = Integer.valueOf(participant.getPersonId());
                    BigDecimal share = new BigDecimal(participant.getShare());

                    Optional<TeamMember> teamMember = teamMemberRepository.findByPerson_IdAndTeam_Id(personId, teamId);

                    if (teamMember.isEmpty()) {
                        throw new UserNotInTeamException("Person with ID " + personId + " is not in the team");
                    }

                    BigDecimal memberShare = oneShare.multiply(share).negate();
                    if (personId.equals(meId)) {
                        memberShare = memberShare.add(totalAmount);
                    }

                    OperationEntry entry = new OperationEntry();
                    entry.setOperation(newOperation);
                    entry.setTeamMember(teamMember.get());
                    entry.setAmount(memberShare);

                    allOperationEntries.add(entry);
                }
                break;
            }
            
            case "income": {
                BigDecimal totalShare = participants.stream()
                    .map(p -> new BigDecimal(p.getShare()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal oneShare = totalAmount.divide(totalShare, 2, RoundingMode.HALF_UP);

                for (OperationParticipantDTO participant : participants) {
                    Integer personId = Integer.valueOf(participant.getPersonId());
                    BigDecimal share = new BigDecimal(participant.getShare());

                    Optional<TeamMember> teamMember = teamMemberRepository.findByPerson_IdAndTeam_Id(personId, teamId);
                    if (teamMember.isEmpty()) {
                        throw new UserNotInTeamException("Person with ID " + personId + " is not in the team");
                    }

                    BigDecimal memberShare = oneShare.multiply(share);
                    if (personId.equals(meId)) {
                        memberShare = memberShare.subtract(totalAmount);
                    }

                    OperationEntry entry = new OperationEntry();
                    entry.setOperation(newOperation);
                    entry.setTeamMember(teamMember.get());
                    entry.setAmount(memberShare);

                    allOperationEntries.add(entry);
                }
                break;
            }

            case "transfer": { 
                if (participants.size() != 2) {
                    throw new BadRequestException("Transfer operation must involve exactly 2 participants");
                }

                for (OperationParticipantDTO participant : participants) {
                    Integer personId = Integer.valueOf(participant.getPersonId());

                    Optional<TeamMember> teamMember = teamMemberRepository.findByPerson_IdAndTeam_Id(personId, teamId);
                    if (teamMember.isEmpty()) {
                        throw new UserNotInTeamException("Person with ID " + personId + " is not in the team");
                    }
                    
                    OperationEntry entry = new OperationEntry();
                    entry.setOperation(newOperation);
                    entry.setTeamMember(teamMember.get());
                    entry.setAmount(totalAmount);

                    if (personId.equals(meId)) {
                        entry.setAmount(totalAmount);
                    } else {
                        entry.setAmount(totalAmount.negate());
                    }

                    allOperationEntries.add(entry);
                }
                break;
            }

            default: {
                throw new BadRequestException("Invalid operation type.");
            }

        }

        operationRepository.save(newOperation);
        for (OperationEntry newEntry : allOperationEntries)
        {
            operationEntryRepository.save(newEntry);
        }

        return "Operation added";
    }

    @Transactional
    public void deleteTeamById(Integer teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team with ID " + teamId + " does not exist.");
        }
        teamRepository.deleteById(teamId);
    }
}
