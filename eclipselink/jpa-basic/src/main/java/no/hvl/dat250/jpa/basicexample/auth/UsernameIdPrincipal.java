package no.hvl.dat250.jpa.basicexample.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.security.Principal;

@Data
public class UsernameIdPrincipal implements Principal {

    private final Long id;
    private final String username;

    public UsernameIdPrincipal(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "UsernameIdPrincipal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
