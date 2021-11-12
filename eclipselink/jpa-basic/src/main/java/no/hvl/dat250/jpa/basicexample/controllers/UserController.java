package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.CredentialsDTO;
import no.hvl.dat250.jpa.basicexample.dto.Mapper;
import no.hvl.dat250.jpa.basicexample.dto.UserDTO;
import no.hvl.dat250.jpa.basicexample.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;
    private final Mapper mapper;


    @Autowired
    public UserController(UserService userService, Mapper mapper){
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers(){
        List<UserDTO> allUsersDTO = new ArrayList<>();
        userService.getAllUsers().forEach(user -> allUsersDTO.add(mapper.convertUserEntityToDTO(user)));
        return allUsersDTO;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or authentication.principal.getId() == #id)")
    public ResponseEntity<Object> getUser(@PathVariable UUID id){
        var user = userService.getUser(id);
        if(user.isPresent()){
            return new ResponseEntity<>(mapper.convertUserEntityToDTO(user.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody CredentialsDTO credentials){
        var userMaybe = userService.getUserByUsername(new Username(credentials.getUsername()));
        if(userMaybe.isPresent()){
            return new ResponseEntity<>("That username is already taken. Please choose another", HttpStatus.CONFLICT);
        }
        var newUser = userService.createUser(mapper.convertCredentialsDTOToUserEntity(credentials));
        return ResponseEntity.created(URI.create("/users/" + newUser.getId())).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or authentication.principal.getId() == #id)")
    public ResponseEntity<Object> updateUser(@PathVariable UUID id, @RequestBody UserDTO updatedUser){
        var userMaybe = userService.getUser(id);
        if(userMaybe.isEmpty()){
            return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
        }
        var updatedUserMaybe = userService.updateUser(userMaybe.get(), updatedUser);
        if(updatedUserMaybe.isEmpty()){
            return new ResponseEntity<>("That username is already taken. Please choose another", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(mapper.convertUserEntityToDTO(updatedUserMaybe.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or authentication.principal.getId() == #id)")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id){
        if(userService.getUser(id).isEmpty()){
            return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("Deleted user with id " + id, HttpStatus.OK);
    }
}

