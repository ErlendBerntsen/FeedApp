package no.hvl.dat250.jpa.basicexample.controllers;

import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public List<UserClass> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        var user = userService.getUser(id);
        if(user.isPresent()){
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Couldn't find user with id " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserClass userClass){
        var user = userService.createUser(userClass);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserClass> updateUser(@PathVariable Long id, @RequestBody UserClass userClass){
        var user = userService.updateUser(id, userClass);
        if(id.equals(user.getId())){
            return ResponseEntity.ok(user);
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
