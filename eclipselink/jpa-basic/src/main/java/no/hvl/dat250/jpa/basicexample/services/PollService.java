package no.hvl.dat250.jpa.basicexample.services;

import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.dao.VoteDAO;
import no.hvl.dat250.jpa.basicexample.dto.Mapper;
import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.dto.ResultDTO;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PollService {

    private final PollDAO pollDao;
    private final VoteDAO voteDao;
    private final UserDAO userDao;
    private final Mapper mapper;

    @Autowired
    public PollService(PollDAO pollDAO, VoteDAO voteDao, UserDAO userDAO, Mapper mapper){
        this.pollDao = pollDAO;
        this.voteDao = voteDao;
        this.userDao = userDAO;
        this.mapper = mapper;
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

    public Optional<Poll> getPoll(UUID id) {
        return pollDao.findById(id);
    }

    public Poll createPoll(Poll poll) {
        return pollDao.save(poll);
    }

    public Poll updatePoll(UUID id, PollDTO updatedPoll){
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
            return createPoll(mapper.convertPollDTOToEntity(updatedPoll));
        }
    }

    public void deletePoll(UUID id) {
        var pollMaybe = getPoll(id);
        if(pollMaybe.isPresent()){
            var poll = pollMaybe.get();
            poll.removeCreator();
            pollDao.deleteById(id);
        }
    }

    public Optional<List<Vote>> getAllVotes(UUID id){
        var poll = getPoll(id);
        return poll.map(Poll::getVotes);
    }

    public Optional<Vote> addVote(UUID id, VoteDTO vote) {
        var poll = getPoll(id);
        if(poll.isPresent() && poll.get().isPollOpenForVoting()) {
            var pollToVote = poll.get();
            var newVote = mapper.convertVoteDTOToEntity(vote);
            if(!newVote.getVoteType().equals(VoteType.USER)){
                newVote.removeVoter();
            }
            newVote.addPoll(pollToVote);
            voteDao.save(newVote);
            return Optional.of(newVote);
        }
        return Optional.empty();
    }

    public Optional<Vote> getVote(UUID voteId) {
        return voteDao.findById(voteId);
    }

    public Optional<Vote> updateVote(UUID pollId, UUID voteId, VoteDTO updatedVote){
        if(getPoll(pollId).isEmpty()){
            return Optional.empty();
        }
        var vote = getVote(voteId);
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

    public void deleteVote(UUID voteId) {
        voteDao.deleteById(voteId);
    }

    public boolean isCreator(UUID pollId, UUID userId){
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

    public ResultDTO getResult(UUID id) {
        var votes = getAllVotes(id);
        if(votes.isEmpty()){
            return new ResultDTO(0, 0);
        }
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

    @Scheduled(fixedDelay = 10000)
    public void checkVotingEndAndStart(){
        var polls = getAllPolls();
        var currentTime = Timestamp.valueOf(LocalDateTime.now());
        var previousCheckTime = Timestamp.valueOf(LocalDateTime.now().minusSeconds(10));
        polls.forEach(poll -> {
            if(previousCheckTime.before(poll.getVotingStart()) && poll.getVotingStart().before(currentTime)){
                //publish voting has started
             publishToDweet(poll, true);
            }
            if(previousCheckTime.before(poll.getVotingEnd())
                    && (poll.getVotingEnd()!= null && poll.getVotingEnd().before(currentTime))){
                //publish voting has ended
                publishToDweet(poll, false);
            }
        });
    }

    /**
     * Publishes an update to Dweet.io with a http post request
     * @param poll the poll who's information should be posted
     * @param votingStarted true if the event is voting started, false if the event is voting ended
     */
    private void publishToDweet(Poll poll, boolean votingStarted){
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            var httpPost = new HttpPost("https://dweet.io/dweet/for/" + poll.getId());
            httpPost.setEntity( getDweetPostBody(poll, votingStarted));
            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            System.out.println("Posted event: " + (votingStarted? "voting started" : "voting ended") + " to dweet.io/follow/" + poll.getId());
            System.out.println("Response statuse: "  + response.getStatusLine());

        } catch (MalformedURLException e) {
            System.err.println("Dweet.io URL is malformed");
        } catch (IOException e) {
            System.err.println("A problem occurred when attempting to post information to Dweet.io");
        }
    }

    private UrlEncodedFormEntity getDweetPostBody(Poll poll, boolean votingStarted) throws UnsupportedEncodingException {
        List<NameValuePair> params = new ArrayList<>();

        if(votingStarted){
            params.add(new BasicNameValuePair("voting", "hasStarted"));
            params.add(new BasicNameValuePair("votingEnd", poll.getVotingEnd().toString()));
        }else{
            params.add(new BasicNameValuePair("voting", "hasEnded"));
            params.add(new BasicNameValuePair("yesVotes", poll.getYesVotes().toString()));
            params.add(new BasicNameValuePair("noVotes", poll.getNoVotes().toString()));
        }
        params.add(new BasicNameValuePair("question", poll.getQuestion()));
        params.add(new BasicNameValuePair("isPrivate", poll.getIsPrivate().toString()));
        params.add(new BasicNameValuePair("code", poll.getCode().toString()));
        params.add(new BasicNameValuePair("creator", poll.getCreator() == null? "deleted" : poll.getCreator().getUsername().getUsername()));
        return new UrlEncodedFormEntity(params);
    }
}
