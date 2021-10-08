package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import no.hvl.dat250.jpa.basicexample.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;

    @Autowired
    public PollController(PollService pollService){
        this.pollService = pollService;
    }

    @GetMapping
    public List<PollDTO> getAllPolls(){
        List<PollDTO> allPollsDTO = new ArrayList<>();
        pollService.getAllPolls().forEach(poll -> allPollsDTO.add(poll.convertToDTO()));
        return allPollsDTO;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPoll(@PathVariable Long id) {
        var poll = pollService.getPoll(id);
        if (poll.isPresent()) {
            return new ResponseEntity<>(poll.get().convertToDTO(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not find poll with id " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createPoll(@RequestBody PollDTO poll){
        var newPoll = pollService.createPoll(poll.convertToEntity());
        return ResponseEntity.created(URI.create("/polls/" + newPoll.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoll(@PathVariable Long id, @RequestBody PollDTO updatedPoll){
        var poll = pollService.updatePoll(id, updatedPoll);
        if(id.equals(poll.getId())){
            return ResponseEntity.ok(poll.convertToDTO());
        }else{
            return ResponseEntity.created(URI.create("/polls/" + poll.getId())).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoll(@PathVariable Long id){
        if(pollService.getPoll(id).isEmpty()){
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        pollService.deletePoll(id);
        return new ResponseEntity<>("Deleted poll with id " + id, HttpStatus.OK);
    }


    @GetMapping("/{id}/votes")
    public ResponseEntity<?> getAllVotes(@PathVariable Long id){
        var votes = pollService.getAllVotes(id);
        if (votes.isPresent()) {
            List<VoteDTO> allVotesInPollDTO = new ArrayList<>();
            votes.get().forEach(vote -> allVotesInPollDTO.add(vote.convertToDTO()));
            return new ResponseEntity<>(allVotesInPollDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not find poll with id " + id, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<?> getVote(@PathVariable Long pollId, @PathVariable Long voteId){
        var vote = pollService.getVote(pollId, voteId);
        if(vote.isPresent()){
            return new ResponseEntity<>(vote.get().convertToDTO(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Could not find vote with id " + voteId, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<?> addVote(@PathVariable Long id, @RequestBody VoteDTO vote) {
        var res = pollService.addVote(id, vote);
        if (res.isEmpty()) {
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.created(URI.create("/polls/" + id + "/votes/" + res.get().getId())).build();
    }


    @PutMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<?> updateVote(@PathVariable Long pollId, @PathVariable Long voteId, @RequestBody VoteDTO updatedVote){
        var vote = pollService.updateVote(pollId, voteId, updatedVote);
        if(vote.isPresent()){
            if(vote.get().getId().equals(voteId)){
                return new ResponseEntity<>(vote.get().convertToDTO(), HttpStatus.OK);

            }else{
                return ResponseEntity.created(URI.create("/polls/" + pollId + "/votes/" + vote.get().getId())).build();
            }
        }else{
            return new ResponseEntity<>("Could not find poll with id " + pollId, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<?> deleteVote(@PathVariable Long pollId, @PathVariable Long voteId){
        if(pollService.getPoll(pollId).isEmpty()){
            return new ResponseEntity<>("Couldn't find poll with id " + pollId, HttpStatus.NOT_FOUND);
        }else if(pollService.getVote(pollId, voteId).isEmpty()){
            return new ResponseEntity<>("Couldn't find vote with id " + voteId, HttpStatus.NOT_FOUND);
        }
        pollService.deleteVote(voteId);
        return new ResponseEntity<>("Deleted vote with id " + voteId, HttpStatus.OK);
    }
    
}
