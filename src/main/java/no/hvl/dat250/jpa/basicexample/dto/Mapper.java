package no.hvl.dat250.jpa.basicexample.dto;

import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Mapper {

    private final UserDAO userDAO;

    @Autowired
    public Mapper(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Poll convertPollDTOToEntity(PollDTO pollDTO){
        var poll = new Poll();
        poll.setQuestion(pollDTO.getQuestion());
        poll.setVotingStart(pollDTO.getVotingStart());
        poll.setVotingEnd(pollDTO.getVotingEnd());
        poll.setIsPrivate(pollDTO.getIsPrivate());
        var creatorMaybe = userDAO.findById(pollDTO.getCreatorId());
        var creator = creatorMaybe.orElse(null);
        poll.addCreator(creator);
        return poll;
    }

    public PollDTO convertPollEntityToDTO(Poll poll){
        List<UUID> votesId = new ArrayList<>();
        poll.getVotes().forEach(vote -> votesId.add(vote.getId()));
        var pollCreator = poll.getCreator() == null? null : poll.getCreator().getId();
        return new PollDTO(poll.getId(),
                poll.getQuestion(),
                poll.getVotingStart(),
                poll.getVotingEnd(),
                poll.getIsPrivate(),
                poll.getCode(),
                pollCreator,
                votesId);
    }

    public UserDTO convertUserEntityToDTO(UserClass user){
        List<UUID> createdPollsId = new ArrayList<>();
        user.getCreatedPolls().forEach(poll -> createdPollsId.add(poll.getId()));
        List<UUID> votesId = new ArrayList<>();
        user.getVotes().forEach(vote -> votesId.add(vote.getId()));
        return new UserDTO(user.getId(),
                user.getUsername(),
                null, //purposely not specifying the password since that is confidential information
                user.getUserType(),
                createdPollsId,
                votesId);
    }

    public Vote convertVoteDTOToEntity(VoteDTO voteDTO){
        var vote = new Vote();
        vote.setOptionChosen(voteDTO.getOptionChosen());
        vote.setVoteType(voteDTO.getVoteType());
        if(voteDTO.getVoterId() != null){
            var voterMaybe = userDAO.findById(voteDTO.getVoterId());
            var voter = voterMaybe.orElse(null);
            vote.addVoter(voter);
        }
        return vote;
    }

    public VoteDTO convertVoteEntityToDTO(Vote vote){
        var voter = vote.getVoter() == null? null : vote.getVoter().getId();
        var poll = vote.getPoll() == null? null : vote.getPoll().getId();
        return new VoteDTO(vote.getId(),
                vote.getOptionChosen(),
                vote.getVoteType(),
                voter,
                poll);
    }

    public UserClass convertCredentialsDTOToUserEntity(CredentialsDTO credentialsDTO){
        var user = new UserClass();
        user.setUsername(new Username(credentialsDTO.getUsername()));
        user.setPassword(new Password(credentialsDTO.getPassword()));
        return user;
    }
}
