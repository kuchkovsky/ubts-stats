package ua.org.ubts.stats.config;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ua.org.ubts.stats.service.SynchronizationService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class AppConfig {

    private static final String INIT_DIRECTORIES_ERROR_MESSAGE = "Could not create application directories";

    @Value("${UBTS_STATS_LDAP_URL}")
    private String ldapUrl;

    @Value("${UBTS_STATS_LDAP_BASE}")
    private String ldapBase;

    @Value("${UBTS_STATS_LDAP_USER_CN}")
    private String ldapUserCn;

    @Value("${UBTS_STATS_LDAP_USER_PASSWORD}")
    private String ldapUserPassword;

    @Autowired
    private SynchronizationService synchronizationService;

    @Bean
    public String appDirectory() {
        return System.getProperty("users.home") + File.separator + "ubts-stats";
    }

    @Bean
    public String tmpDirectory() {
        return appDirectory() + File.separator + "tmp";
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @PostConstruct
    public void initDirectories() {
        try {
            Files.createDirectories(Paths.get(appDirectory()));
            Files.createDirectories(Paths.get(tmpDirectory()));
        } catch (IOException e) {
            log.error(INIT_DIRECTORIES_ERROR_MESSAGE, e);
        }
    }

    @Scheduled(cron = "0 0 */12 * * *")
    @EventListener(ApplicationReadyEvent.class) // run immediately after application start
    public void synchronizeLdap() {
        synchronizationService.synchronizeLdap();
    }

    @Bean
    @Primary
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn("CN=" + ldapUserCn + "," + ldapBase);
        contextSource.setPassword(ldapUserPassword);

        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource());
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }

}
