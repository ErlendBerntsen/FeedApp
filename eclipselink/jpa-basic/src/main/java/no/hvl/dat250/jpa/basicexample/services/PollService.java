package no.hvl.dat250.jpa.basicexample.services;

import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PollService {

    private final PollDAO pollDao;

    @Autowired
    public PollService(PollDAO pollDAO){
        this.pollDao = pollDAO;
    }

    public List<Poll> getAllPolls() {
        return pollDao.findAll();
    }

    public Optional<Poll> getPoll(long id) {
        return pollDao.findById(id);
    }

    public Poll createPoll(Poll poll ) {
        return pollDao.save(poll);
    }

    public Poll updatePoll(Long id, Poll updatedPoll){
        var poll = getPoll(id);
        if(poll.isPresent()){
            var pollToUpdate = poll.get();
            pollToUpdate.setQuestion(updatedPoll.getQuestion());
            pollToUpdate.setVotingStart(updatedPoll.getVotingStart());
            pollToUpdate.setVotingEnd(updatedPoll.getVotingEnd());
            pollToUpdate.setIsPrivate(updatedPoll.getIsPrivate());
            pollToUpdate.setCode(updatedPoll.getCode());
            return pollToUpdate;
        }else{
            return createPoll(updatedPoll);
        }
    }
}
