package com.iscp.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@Getter
@Setter
public class LdapConfig {


    @Value("${ldapUrl}")
    private String ldapUrl;

    @Value("${ldapBase}")
    private String ldapBase;

    @Value("${ldapUserDn}")
    private String ldapUserDn;


    @Value("${ldapPassword}")
    private String ldapPassword;



    // Define the LdapTemplate bean to  interact with the LDAP server and perform common LDAP operations and return instance of `LdapTemplate` that can be used for LDAP operations.
//    @Bean
//    public LdapTemplate ldapTemplate()
//    {
//        return new LdapTemplate(contextSource());
//    }


    // Define the LdapContextSource bean to configures the connection details to the LDAP server and @return an instance of `LdapContextSource` that represents the connection to the LDAP server.
    @Bean
    public LdapContextSource contextSource()
    {
        LdapContextSource ldapContextSource=new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.setBase(ldapBase);
        ldapContextSource.setUserDn(ldapUserDn);
        ldapContextSource.setPassword(ldapPassword);
        return ldapContextSource;
    }
}
