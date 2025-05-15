package me.wisisz.service;

import me.wisisz.dto.OperationDTO;
import me.wisisz.dto.TeamMemberDTO;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
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

    public Optional<TeamWithMembersDTO> getTeamWithMembersById(Integer teamId) {
        return teamRepository.findById(teamId).map(t -> new TeamWithMembersDTO(t));
    }

    public Optional<List<OperationDTO>> getTeamOperationsView(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations().stream().map(o -> new OperationDTO(o)).toList());
    }

    public Optional<List<Operation>> getTeamOperations(Integer teamId) {
        return teamRepository.findById(teamId)
                .map(t -> t.getOperations());
    }

    public boolean isPersonInTeam(Integer personId, Integer teamId) {
        return teamMemberRepository.existsByPersonIdAndTeamId(personId, teamId);
    }

    public String saveTeam(Integer meId, Map<String, String> data) throws Exception{
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

    public String saveTeamMember(Integer teamId, Map<String, String> data) throws Exception{

        TeamMember newTeamMember = new TeamMember();
        Optional<Team> team = teamRepository.findById(teamId);
        newTeamMember.setTeam(team.get());
        Optional<Person> person = personRepository.findByEmailAddr(data.get("emailAddr"));
        newTeamMember.setPerson(person.get());
        newTeamMember.setDefaultShare(new BigDecimal(data.get("shares")));
        teamMemberRepository.save(newTeamMember);

        return "Team member added";
    }

    public String removeTeamMember(Integer teamId, Integer personId) throws Exception{ //TODO: failsave; can't remove if balance not settled
        Optional<TeamMember> member = teamMemberRepository.findByPerson_IdAndTeam_Id(personId, teamId);
        teamMemberRepository.delete(member.get());
        return "Team member removed";
    }

    public String saveTeamOperation(Integer meId, Integer teamId, Map<String, String> data) throws Exception {

        BigDecimal totalAmount = new BigDecimal(data.get("totalAmount"));
        Optional<Team> team = teamRepository.findById(teamId);
        Optional<Category> category = categoryRepository.findById(Integer.parseInt((data.get("categoryId"))));

        Operation newOperation = new Operation();
        newOperation.setTeam(team.get());
        newOperation.setOperationDate(OffsetDateTime.now());
        newOperation.setDescription(data.get("description"));
        newOperation.setTotalAmount(totalAmount);
        newOperation.setCategory(category.get());
        newOperation.setCurrencyCode(data.get("currencyCode"));
        newOperation.setOperationType(data.get("operationType"));

        operationRepository.save(newOperation);
        
        switch (data.get("operationType")) {
            case "expense": { // Create operation entries for all members
                Optional<TeamWithMembersDTO> teamWithMembers = getTeamWithMembersById(teamId);
                BigDecimal totalShare = teamWithMembers
                                        .map(teamDTO -> teamDTO.members().stream()
                                        .map(TeamMemberDTO::defaultShare)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                        .orElse(BigDecimal.ZERO);
                BigDecimal oneShare = totalAmount.divide(totalShare, 2, RoundingMode.HALF_UP);

                teamWithMembers.ifPresent(teamDTO -> {
                    for (TeamMemberDTO memberDTO : teamDTO.members()) {

                        Optional<TeamMember> teamMember = teamMemberRepository.findById(memberDTO.personId());
                        BigDecimal memberShare = oneShare.multiply(memberDTO.defaultShare()).negate();
                        if(memberDTO.personId() == meId){
                            memberShare = memberShare.add(totalAmount);
                        }
                        
                        OperationEntry entry = new OperationEntry();
                        entry.setOperation(newOperation);
                        entry.setTeamMember(teamMember.get());
                        entry.setAmount(memberShare);

                        operationEntryRepository.save(entry);
                    }
                });
                break;
            }

            case "income": { // Create operation entries for all members
                Optional<TeamWithMembersDTO> teamWithMembers = getTeamWithMembersById(teamId);
                BigDecimal totalShare = teamWithMembers
                                        .map(teamDTO -> teamDTO.members().stream()
                                        .map(TeamMemberDTO::defaultShare)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                        .orElse(BigDecimal.ZERO);
                BigDecimal oneShare = totalAmount.divide(totalShare, 2, RoundingMode.HALF_UP);

                teamWithMembers.ifPresent(teamDTO -> {
                    for (TeamMemberDTO memberDTO : teamDTO.members()) {

                        Optional<TeamMember> teamMember = teamMemberRepository.findById(memberDTO.personId());
                        BigDecimal memberShare = oneShare.multiply(memberDTO.defaultShare());
                        if(memberDTO.personId() == meId){
                            memberShare = memberShare.subtract(totalAmount);
                        }
                        
                        OperationEntry entry = new OperationEntry();
                        entry.setOperation(newOperation);
                        entry.setTeamMember(teamMember.get());
                        entry.setAmount(memberShare);

                        operationEntryRepository.save(entry);
                    }
                });
                break;
            }

            case "transfer": { // Create operation entries for 2 members - TODO: Greed algorithm to be added once team balance is implemented
                Optional<TeamMember> sender = teamMemberRepository.findById(meId);
                Optional<TeamMember> recipient = teamMemberRepository.findById(Integer.parseInt(data.get("recipientID")));
                    
                OperationEntry entry = new OperationEntry();
                entry.setOperation(newOperation);
                entry.setTeamMember(sender.get());
                entry.setAmount(totalAmount);

                operationEntryRepository.save(entry);


                entry = new OperationEntry();
                entry.setOperation(newOperation);
                entry.setTeamMember(recipient.get());
                entry.setAmount(totalAmount.negate());

                operationEntryRepository.save(entry);
                break;
            }

            default: {
                throw new IllegalArgumentException("Invalid operation type.");
            }

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
