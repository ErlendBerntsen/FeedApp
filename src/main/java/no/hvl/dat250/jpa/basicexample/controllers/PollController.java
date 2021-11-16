package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.auth.UsernameIdPrincipal;
import no.hvl.dat250.jpa.basicexample.dto.Mapper;
import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;
import no.hvl.dat250.jpa.basicexample.services.PollService;
import no.hvl.dat250.jpa.basicexample.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public PollController(PollService pollService, UserService userService, Mapper mapper){
        this.pollService = pollService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<Object> getAllPolls(@RequestParam Optional<Boolean> isPrivate,
                                         @RequestParam Optional<Integer> code,
                                         @RequestParam Optional<UUID> creator) {
        List<PollDTO> allPollsDTO = new ArrayList<>();
        if (isPrivate.isPresent() && Boolean.FALSE.equals(isPrivate.get())) {
            pollService.getAllPublicPolls().forEach(poll -> allPollsDTO.add(mapper.convertPollEntityToDTO(poll)));
            return new ResponseEntity<>(allPollsDTO, HttpStatus.OK);
        }

        if(code.isPresent()){
            var poll = pollService.getPollByCode(code.get());
            if (poll.isPresent()) {
                return new ResponseEntity<>(mapper.convertPollEntityToDTO(poll.get()), HttpStatus.OK);
            }
            return new ResponseEntity<>("Could not find poll with code " + code.get(), HttpStatus.NOT_FOUND);
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(creator.isPresent()){
            if(!"anonymousUser".equals(authentication.getName())){
                var principal = (UsernameIdPrincipal) authentication.getPrincipal();
                if(creator.get().equals(principal.getId())
                        || authentication.getAuthorities().contains(UserType.ADMIN.getRoleAuthority())){
                    var user = userService.getUser(creator.get());
                    if(user.isPresent()){
                        user.get().getCreatedPolls().forEach(poll -> allPollsDTO.add(mapper.convertPollEntityToDTO(poll)));
                        return new ResponseEntity<>(allPollsDTO, HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Couldn't find user with id " + creator.get(), HttpStatus.NOT_FOUND);
                    }
                }
            }
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        if (!"anonymousUser".equals(authentication.getName())
                && authentication.getAuthorities().contains(UserType.ADMIN.getRoleAuthority())) {
            pollService.getAllPolls().forEach(poll -> allPollsDTO.add(mapper.convertPollEntityToDTO(poll)));
            return new ResponseEntity<>(allPollsDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPoll(@PathVariable UUID id) {
        var poll = pollService.getPoll(id);
        if (poll.isPresent()) {
            return new ResponseEntity<>(mapper.convertPollEntityToDTO(poll.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not find poll with id " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> createPoll(@RequestBody PollDTO poll){
        var newPoll = pollService.createPoll(mapper.convertPollDTOToEntity(poll));
        var location = URI.create("/polls/" + newPoll.getId());
        var responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(mapper.convertPollEntityToDTO(newPoll), responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @pollService.isCreator(#id, authentication.principal.getId()))")
    public ResponseEntity<Object> updatePoll(@PathVariable UUID id, @RequestBody PollDTO updatedPoll){
        var poll = pollService.updatePoll(id, updatedPoll);
        if(id.equals(poll.getId())){
            return ResponseEntity.ok(mapper.convertPollEntityToDTO(poll));
        }else{
            return ResponseEntity.created(URI.create("/polls/" + poll.getId())).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @pollService.isCreator(#id, authentication.principal.getId()))")
    public ResponseEntity<Object> deletePoll(@PathVariable UUID id){
        if(pollService.getPoll(id).isEmpty()){
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        pollService.deletePoll(id);
        return new ResponseEntity<>("Deleted poll with id " + id, HttpStatus.OK);
    }


    @GetMapping("/{id}/result")
    public ResponseEntity<Object> getResults(@PathVariable UUID id){
        if(pollService.getPoll(id).isEmpty()){
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pollService.getResult(id), HttpStatus.OK);
    }

    /*
    VOTE CONTROLLER METHODS
     */

    @GetMapping("/{id}/votes")
    public ResponseEntity<Object> getAllVotes(@PathVariable UUID id){
        var votes = pollService.getAllVotes(id);
        if (votes.isPresent()) {
            List<VoteDTO> allVotesInPollDTO = new ArrayList<>();
            votes.get().forEach(vote -> allVotesInPollDTO.add(mapper.convertVoteEntityToDTO(vote)));
            return new ResponseEntity<>(allVotesInPollDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not find poll with id " + id, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<Object> getVote(@PathVariable UUID pollId, @PathVariable UUID voteId){
        var vote = pollService.getVote(voteId);
        if(vote.isPresent()){
            return new ResponseEntity<>(mapper.convertVoteEntityToDTO(vote.get()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Could not find vote with id " + voteId, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<Object> addVote(@PathVariable UUID id, @RequestBody VoteDTO vote) {
        var res = pollService.getPoll(id);
        if (res.isEmpty()) {
            return new ResponseEntity<>("Couldn't find poll with id " + id, HttpStatus.NOT_FOUND);
        }
        var voteRes = pollService.addVote(id, vote);
        if(voteRes.isEmpty()){
            return new ResponseEntity<>("The poll isn't open for voting", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.created(URI.create("/polls/" + id + "/votes/" + res.get().getId())).build();
    }


    @PutMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<Object> updateVote(@PathVariable UUID pollId, @PathVariable UUID voteId, @RequestBody VoteDTO updatedVote){
        var vote = pollService.updateVote(pollId, voteId, updatedVote);
        if(vote.isPresent()){
            if(vote.get().getId().equals(voteId)){
                return new ResponseEntity<>(mapper.convertVoteEntityToDTO(vote.get()), HttpStatus.OK);

            }else{
                return ResponseEntity.created(URI.create("/polls/" + pollId + "/votes/" + vote.get().getId())).build();
            }
        }else{
            return new ResponseEntity<>("Could not find poll with id " + pollId, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{pollId}/votes/{voteId}")
    public ResponseEntity<Object> deleteVote(@PathVariable UUID pollId, @PathVariable UUID voteId){
        if(pollService.getPoll(pollId).isEmpty()){
            return new ResponseEntity<>("Couldn't find poll with id " + pollId, HttpStatus.NOT_FOUND);
        }else if(pollService.getVote(voteId).isEmpty()){
            return new ResponseEntity<>("Couldn't find vote with id " + voteId, HttpStatus.NOT_FOUND);
        }
        pollService.deleteVote(voteId);
        return new ResponseEntity<>("Deleted vote with id " + voteId, HttpStatus.OK);
    }
    
}
