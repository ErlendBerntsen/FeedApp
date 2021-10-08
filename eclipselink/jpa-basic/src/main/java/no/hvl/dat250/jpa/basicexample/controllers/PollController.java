package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import no.hvl.dat250.jpa.basicexample.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public List<Poll> getAllPolls(){
        return pollService.getAllPolls();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPoll(@PathVariable Long id) {
        var poll = pollService.getPoll(id);
        if (poll.isPresent()) {
            return new ResponseEntity<>(poll.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not find poll with " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createPoll(@RequestBody Poll poll){
        var newPoll = pollService.createPoll(poll);
        return ResponseEntity.created(URI.create("/polls/" + newPoll.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable Long id, @RequestBody Poll updatedPoll){
        var poll = pollService.updatePoll(id, updatedPoll);
        if(id.equals(poll.getId())){
            return ResponseEntity.ok(poll);
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

    @PostMapping("/{id}/votes")
    public ResponseEntity<?> addVote(@PathVariable Long id, @RequestBody Vote vote) {

        Optional<Vote> res = pollService.addVote(id, vote);
        if (res.isEmpty()) {
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.created(URI.create("/polls/" + id + "/votes/" + res.get().getId())).build();
    }
}
