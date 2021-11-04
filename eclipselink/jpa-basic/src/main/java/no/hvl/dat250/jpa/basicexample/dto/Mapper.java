package no.hvl.dat250.jpa.basicexample.dto;

import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private final PollDAO pollDAO;
    private final UserDAO userDAO;

    @Autowired
    public Mapper(PollDAO pollDAO, UserDAO userDAO) {
        this.pollDAO = pollDAO;
        this.userDAO = userDAO;
    }

    public Poll convertPollToEntity(PollDTO pollDTO){
        Poll poll = new Poll();
        poll.setQuestion(pollDTO.getQuestion());
        poll.setVotingStart(pollDTO.getVotingStart());
        poll.setVotingEnd(pollDTO.getVotingEnd());
        poll.setIsPrivate(pollDTO.getIsPrivate());
        var creatorMaybe = userDAO.findById(pollDTO.getCreatorId());
        var creator = creatorMaybe.orElse(null);
        poll.addCreator(creator);
        return poll;
    }
}
