package com.iscp.backend.security;

import com.iscp.backend.models.Role;
import com.iscp.backend.models.Users;
import com.iscp.backend.repositories.UsersRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtHelper jwtHelper;

    private final LdapUserDetailsMapper ldapUserDetailsMapper;
    private final LdapTemplate ldapTemplate;

    private final TokenBlacklist tokenBlacklist;
    private final UsersRepository usersRepository;

    public JwtAuthenticationFilter(JwtHelper jwtHelper, LdapUserDetailsMapper ldapUserDetailsMapper, LdapTemplate ldapTemplate, TokenBlacklist tokenBlacklist, UsersRepository usersRepository) {
        this.jwtHelper = jwtHelper;
        this.ldapUserDetailsMapper = ldapUserDetailsMapper;
        this.ldapTemplate = ldapTemplate;
        this.tokenBlacklist = tokenBlacklist;
        this.usersRepository = usersRepository;
    }

    //FUNCTION TO FILTER EACH INCOMING HTTP REQUEST TO VALIDATE JWT TOKEN
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            log.debug("JWT token extracted: {}", token);
            log.debug("Authorization header detected: {}", requestHeader);

            try {
                username = jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                log.warn("JWT token has expired", e);
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                log.warn("Invalid JWT token", e);
                e.printStackTrace();
            } catch (Exception e) {
                log.error("Unable to process JWT token", e);
                e.printStackTrace();
            }
        } else {
            log.info("Invalid Header Value");
        }

        // If username is not null and the user is not already authenticated, validate the token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details by username
            UserDetails userDetails = loadUserDetailsFromLdapAndDatabase(username);

            // Validate the JWT token against the user details
            if(userDetails!=null && jwtHelper.validateToken(token,userDetails) && !tokenBlacklist.isTokenBlacklisted(token))
            {
                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            else {
                log.info("Validation Fails");
            }

        }
        filterChain.doFilter(request, response);
    }


    // FUNCTION TO LOAD USERS FROM LDAP SERVER AND DATABASE
    private UserDetails loadUserDetailsFromLdapAndDatabase(String username) {
        try
        {
            // Create an LDAP filter to search by email (assuming email is used as the username)
            Filter filter=new EqualsFilter("mail",username);

            // Search for the user in the LDAP directory
            List<UserDetails> users=ldapTemplate.search(
                    "",
                    filter.encode(),
                    (ContextMapper<UserDetails>)ctx->{
                        DirContextOperations contextOperations=(DirContextOperations) ctx;
                        List<GrantedAuthority> authorities=fetchRolesFromDatabase(username);
                        return  ldapUserDetailsMapper.mapUserFromContext((DirContextOperations) ctx,username,authorities);
                    }
            );

            // Return the first matching user, or null if not found
            return users.isEmpty()?null:users.get(0);
        }
        catch(Exception e)
        {
            log.error("Error loading user details from LDAP",e);
            return null;
        }
    }

    private List<GrantedAuthority> fetchRolesFromDatabase(String username)
    {
        List<GrantedAuthority> authorities=new ArrayList<>();
        Users user = usersRepository.findByUserEmailId(username);
        if(user!=null && user.getRoles()!=null)
        {
            for (Role role:user.getRoles())
            {
                authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
            }
        }
        return authorities;
    }
}
