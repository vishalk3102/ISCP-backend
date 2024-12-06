package com.iscp.backend.services.impl;

import com.iscp.backend.dto.JwtRequest;
import com.iscp.backend.dto.JwtResponse;
import com.iscp.backend.dto.ReCaptchaResponse;
import com.iscp.backend.exceptions.BadCredentialsException;
import com.iscp.backend.models.Department;
import com.iscp.backend.models.Permission;
import com.iscp.backend.models.Role;
import com.iscp.backend.models.Users;
import com.iscp.backend.repositories.UsersRepository;
import com.iscp.backend.security.JwtHelper;
import com.iscp.backend.security.TokenBlacklist;
import com.iscp.backend.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;

    private  final JwtHelper jwtHelper;

    private final TokenBlacklist tokenBlacklist;

    private final UsersRepository usersRepository;

    @Value("${google.recaptcha.secret.key}")
    private String recaptchaSecret;

    private static  final String GOOGLE_RECAPTCHA_VERIFY_URL= "https://www.google.com/recaptcha/api/siteverify";


    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtHelper jwtHelper, TokenBlacklist tokenBlacklist, UsersRepository usersRepository,@Value("${google.recaptcha.secret.key}") String recaptchaSecret) {
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
        this.tokenBlacklist = tokenBlacklist;
        this.usersRepository = usersRepository;
        this.recaptchaSecret = recaptchaSecret;
    }

    public JwtResponse generateAndAuthenticateToken(JwtRequest jwtRequest) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),jwtRequest.getPassword());
        log.info("Attempting to authenticate user: {}", jwtRequest.getUsername());
        Authentication authentication=authenticationManager.authenticate(authenticationToken);

        if(authentication==null || !authentication.isAuthenticated())
        {
            throw new BadCredentialsException();
        }

        //Set the authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);


        // Fetch user details from the database based on the username from authentication
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        Users user= usersRepository.findByUserEmailId(userDetails.getUsername());

        if(user==null)
        {
            throw new BadCredentialsException();
        }

        //Fetch user roles and permission associated with authenticated users.
        String userId=user.getUserId();
        Set<Role> roles=user.getRoles();
        Set<Permission> permissions=roles.stream().flatMap(role -> role.getPermissions().stream()).collect(Collectors.toSet());
        Set<Department> departments=user.getDepartments();

        //Flatten roles and permission for JWT token
        List<String> roleNames=roles.stream().map(role -> role.getRoleName().name()).toList();
        List<String> permissionNames=permissions.stream().map(permission -> permission.getRolePermissions().name()).toList();
        List<String> departmentNames=departments.stream().map(department -> department.getDepartmentName().name()).toList();

        //Generate JWT after successful LDAP authentication
        String token=this.jwtHelper.generateToken((UserDetails) authentication.getPrincipal(),userId,roleNames,permissionNames,departmentNames);
        JwtResponse response=JwtResponse.builder().jwtToken(token).username(jwtRequest.getUsername()).build();
        log.info("Authentication successful for user: {}", jwtRequest.getUsername());
        return response;
    }


// FUNCTION TO BLACKLIST TOKEN
    @Override
    public Boolean blacklistToken(String token) {
        if(token!=null && token.startsWith("Bearer "))
        {
            String jwtToken=token.substring(7);
            tokenBlacklist.addToBlacklistTokenList(jwtToken);
            log.info("Token blacklisted successfully");
            return true;
        }
        log.warn("Invalid token format for blacklisting");
        return false;
    }

    // FUNCTION TO VALIDATE CAPTCHA TOKEN
    @Override
    public boolean validateCaptcha(String captchaToken) {
        if (captchaToken == null || captchaToken.trim().isEmpty()) {
            log.warn("Empty CAPTCHA token received");
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Prepare parameters
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", recaptchaSecret);
            map.add("response", captchaToken);

            // Create request entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // Make POST request
            ReCaptchaResponse response = restTemplate.postForObject(
                    GOOGLE_RECAPTCHA_VERIFY_URL,
                    request,
                    ReCaptchaResponse.class
            );

            if (response == null) {
                log.error("Null response received from reCAPTCHA verification");
                return false;
            }

            if (!response.isSuccess()) {
                log.warn("reCAPTCHA verification failed. Error codes: {}",
                        response.getErrorCodes() != null ? String.join(", ", response.getErrorCodes()) : "none");
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("Error during reCAPTCHA verification: ", e);
            return false;
        }
    }
}
