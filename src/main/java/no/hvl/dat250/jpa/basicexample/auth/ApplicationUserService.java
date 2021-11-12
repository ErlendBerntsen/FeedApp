package no.hvl.dat250.jpa.basicexample.auth;

import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public ApplicationUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserClass user = userDAO.findByUsername(new Username(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", username)));

        return new ApplicationUser(user.getId(),
                user.getUsername().getUsername(),
                user.getPassword().getPassword(),
                user.getUserType(),
                user.getUserType().getGrantedAuthorities(),
                true ,
                true ,
                true ,
                true);
    }

}
