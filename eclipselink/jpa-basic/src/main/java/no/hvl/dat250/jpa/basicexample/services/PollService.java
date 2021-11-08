package no.hvl.dat250.jpa.basicexample.services;

import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.dao.VoteDAO;
import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.dto.ResultDTO;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PollService {

    private final PollDAO pollDao;
    private final VoteDAO voteDao;
    private final UserDAO userDao;

    @Autowired
    public PollService(PollDAO pollDAO, VoteDAO voteDao, UserDAO userDAO){
        this.pollDao = pollDAO;
        this.voteDao = voteDao;
        this.userDao = userDAO;
    }

    public List<Poll> getAllPolls() {
        return pollDao.findAll();
    }

    public List<Poll> getAllPublicPolls() {
        return pollDao.findByIsPrivate(false);
    }

    public Optional<Poll> getPollByCode(Integer code){
        return pollDao.findByCode(code);
    }

    public Optional<Poll> getPoll(long id) {
        return pollDao.findById(id);
    }

    public Poll createPoll(Poll poll ) {
        return pollDao.save(poll);
    }

    public Poll updatePoll(Long id, PollDTO updatedPoll){
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
            return createPoll(updatedPoll.convertToEntity());
        }
    }

    public void deletePoll(Long id) {
        var pollMaybe = getPoll(id);
        if(pollMaybe.isPresent()){
            var poll = pollMaybe.get();
            poll.removeCreator();
            pollDao.deleteById(id);
        }
    }

    public Optional<List<Vote>> getAllVotes(Long id){
        var poll = getPoll(id);
        return poll.map(Poll::getVotes);
    }

    public Optional<Vote> addVote(Long id, VoteDTO vote) {
        var poll = getPoll(id);
        if(poll.isPresent()) {
            var pollToVote = poll.get();
            var newVote = vote.convertToEntity();
            if(newVote.getVoteType().equals(VoteType.USER) && vote.getVoterId() != null){
                newVote.addVoter(userDao.findById(vote.getVoterId()).get());
            }
            newVote.addPoll(pollToVote);
            voteDao.save(newVote);
            return Optional.of(newVote);
        }
        return Optional.empty();
    }

    public Optional<Vote> getVote(Long pollId, Long voteId) {
        //TODO make ids of votes only unique inside a poll and not globally unique?
        return voteDao.findById(voteId);
    }

    public Optional<Vote> updateVote(Long pollId, Long voteId, VoteDTO updatedVote){
        if(getPoll(pollId).isEmpty()){
            return Optional.empty();
        }
        var vote = getVote(pollId, voteId);
        if(vote.isPresent()){
            var voteToUpdate = vote.get();
            voteToUpdate.setOptionChosen(updatedVote.getOptionChosen());
            if(!voteToUpdate.getVoteType().equals(VoteType.USER)
                && updatedVote.getVoteType().equals(VoteType.USER)){
                voteToUpdate.addVoter(userDao.findById(updatedVote.getVoterId()).get());
            }else if(voteToUpdate.getVoteType().equals(VoteType.USER)
                    && !updatedVote.getVoteType().equals(VoteType.USER)){
                voteToUpdate.setVoter(null);
            }
            voteToUpdate.setVoteType(updatedVote.getVoteType());
            return Optional.of(voteToUpdate);
        }
        return addVote(pollId, updatedVote);
    }

    public void deleteVote(Long voteId) {
        voteDao.deleteById(voteId);
    }

    public boolean isCreator(Long pollId, Long userId){
        var poll = getPoll(pollId);
        if(poll.isEmpty()){
            return false;
        }
        var creator = poll.get().getCreator();
        if(creator == null){
            return false;
        }
        return userId.equals(creator.getId());
    }

    public ResultDTO getResult(Long id) {
        var votes = getAllVotes(id);
        var yesCounter = 0;
        var noCounter = 0;
        for(Vote vote : votes.get()){
            if("yes".equalsIgnoreCase(vote.getOptionChosen())){
                yesCounter++;
            }
            if("no".equalsIgnoreCase(vote.getOptionChosen())){
                noCounter++;
            }
        }
        return new ResultDTO(yesCounter, noCounter);

    }
}
