package no.hvl.dat250.jpa.basicexample.auth;


import java.security.Principal;

public class UsernameIdPrincipal implements Principal {
    private final Long id;
    private final String username;

    public UsernameIdPrincipal(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
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
