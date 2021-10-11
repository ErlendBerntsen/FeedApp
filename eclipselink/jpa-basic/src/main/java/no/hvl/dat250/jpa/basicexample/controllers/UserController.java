package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.dto.UserDTO;
import no.hvl.dat250.jpa.basicexample.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RequestMapping("/users")
@RestController
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers(){
        List<UserDTO> allUsersDTO = new ArrayList<>();
        userService.getAllUsers().forEach(user -> allUsersDTO.add(user.convertToDTO()));
        return allUsersDTO;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        var user = userService.getUser(id);
        if(user.isPresent()){
            return new ResponseEntity<>(user.get().convertToDTO(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO user){
        var newUser = userService.createUser(user.convertToEntity());
        return ResponseEntity.created(URI.create("/users/" + newUser.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUser){
        var user = userService.updateUser(id, updatedUser);
        if(id.equals(user.getId())){
            return ResponseEntity.ok(user.convertToDTO());
        }else{
            return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        if(userService.getUser(id).isEmpty()){
            return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("Deleted user with id " + id, HttpStatus.OK);
    }
}
