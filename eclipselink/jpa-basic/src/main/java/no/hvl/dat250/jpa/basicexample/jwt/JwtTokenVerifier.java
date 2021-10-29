package no.hvl.dat250.jpa.basicexample.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import no.hvl.dat250.jpa.basicexample.UserType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JwtTokenVerifier extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerToken = request.getHeader("Authorization");

        //Check if the http header is missing a proper JWT authorization header and deny it if it missing or wrong
        if(headerToken == null || Strings.isEmpty(headerToken) || !headerToken.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String jwtToken = headerToken.replace("Bearer ", "");

            //TODO make this more secure and make it a environment variable
            String key = "makethismoresecureyouabsolutedumbmonkeyomgnowigotabugsincethiswasntstrongenoughwowgreatjob";

            //Verify that the token matches
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);

            Claims body = jwsClaims.getBody();
            String username = body.getSubject();

            //TODO make this depend on the users authorities
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = UserType.REGULAR.getGrantedAuthorities();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null, //Should this be null? Who knows
                    simpleGrantedAuthorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (JwtException e){
            throw new IllegalStateException("Token cannot be trusted");
        }

        filterChain.doFilter(request, response);
    }
}
