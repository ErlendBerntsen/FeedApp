package no.hvl.dat250.jpa.basicexample.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;

public class CredentialsDTO {

    private final Username username;
    private final Password password;

    public CredentialsDTO(Username username, Password password) {
        this.username = username;
        this.password = password;
    }

    @JsonCreator
    public CredentialsDTO(String username, String password){
        this.username = new Username(username);
        this.password = new Password(password);
    }


    @JsonGetter
    public String getUsername() {
        return username.getUsername();
    }

    @JsonGetter
    public String getPassword() {
        return password.getPassword();
    }
}
