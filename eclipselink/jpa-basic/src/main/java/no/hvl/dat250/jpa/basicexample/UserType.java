package no.hvl.dat250.jpa.basicexample;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public enum UserType {
    REGULAR,
    ADMIN;

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }

    public SimpleGrantedAuthority getRoleAuthority(){
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }
}
