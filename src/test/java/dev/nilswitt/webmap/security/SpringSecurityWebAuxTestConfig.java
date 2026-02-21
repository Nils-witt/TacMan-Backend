package dev.nilswitt.webmap.security;

import dev.nilswitt.webmap.entities.SecurityGroup;
import dev.nilswitt.webmap.entities.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        SecurityGroup securityGroup = new SecurityGroup("MockAdmin",new HashSet<>(SecurityGroup.availableRoles()));

        User adminUser = new User("mock.admin", "admin@mock.local", "Admin","Mock");
        adminUser.setPassword("{noop}admin");
        adminUser.getSecurityGroups().add(securityGroup);


        return new InMemoryUserDetailsManager(List.of(adminUser));
    }
}
