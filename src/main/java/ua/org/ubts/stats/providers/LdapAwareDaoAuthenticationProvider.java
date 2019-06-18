package ua.org.ubts.stats.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.org.ubts.stats.entity.UserEntity;
import ua.org.ubts.stats.repository.UserRepository;

@Component
public class LdapAwareDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private static final String LDAP_USER_FILTER_TEMPLATE = "(sAMAccountName=%s)";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LdapTemplate ldapTemplate;

    private boolean authenticateLdapUser(Authentication authentication) {
        final String userName = String.format(LDAP_USER_FILTER_TEMPLATE, authentication.getName());
        final String password = (String) authentication.getCredentials();
        return ldapTemplate.authenticate("", userName, password);
    }

    @Autowired
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Autowired
    @Lazy
    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        UserEntity userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));
        if (userEntity.isLdapUser()) {
            if (authenticateLdapUser(authentication)) {
                String password = (String) authentication.getCredentials();
                UserDetails user = getUserDetailsService().loadUserByUsername(login);
                return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            }
            return null;
        }
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }

}
