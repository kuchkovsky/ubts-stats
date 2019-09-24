package ua.org.ubts.stats.service.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.org.ubts.stats.entity.GroupEntity;
import ua.org.ubts.stats.entity.OrganizationEntity;
import ua.org.ubts.stats.entity.ProgramEntity;
import ua.org.ubts.stats.entity.UserEntity;
import ua.org.ubts.stats.exception.DatabaseItemNotFoundException;
import ua.org.ubts.stats.exception.GroupNotFoundException;
import ua.org.ubts.stats.repository.GroupRepository;
import ua.org.ubts.stats.repository.OrganizationRepository;
import ua.org.ubts.stats.repository.ProgramRepository;
import ua.org.ubts.stats.repository.UserRepository;
import ua.org.ubts.stats.service.SynchronizationService;
import ua.org.ubts.stats.service.UserService;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SynchronizationServiceImpl implements SynchronizationService {

    private static final String GROUP_NOT_FOUND_ERROR_MESSAGE = "Requested group could not be found";

    private static final String UBTS_NOT_FOUND_ERROR_MESSAGE = "Where's UBTS? It's time to fire our dev(ops)";

    @Value("${UBTS_STATS_LDAP_BASE}")
    private String ldapBase;

    @Value("${UBTS_STATS_LDAP_STUDENTS_BASE}")
    private String ldapStudentsBase;

    @Value("${UBTS_STATS_LDAP_GROUPS_BASE}")
    private String ldapGroupsBase;

    @Value("${UBTS_STATS_LDAP_PROGRAMS_BASE}")
    private String ldapProgramsBase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LdapTemplate ldapTemplate;


    @Data
    private static class User {
        private String login;
        private String firstName;
        private String lastName;
        private String phone1;
        private String phone2;
    }

    private static class UserAttributesMapper implements AttributesMapper<User> {

        public User mapFromAttributes(Attributes attrs) throws NamingException {
            User user = new User();
            user.setLogin((String) attrs.get("sAMAccountName").get());
            user.setFirstName((String) attrs.get("givenName").get());
            user.setLastName((String) attrs.get("sn").get());
            Attribute phoneAttr = attrs.get("telephoneNumber");
            if (phoneAttr != null) {
                String phoneValue = (String) phoneAttr.get();
                if (phoneValue != null) {
                    String[] phones = phoneValue.split("[\\s;,]+");
                    if (phones.length > 0) {
                        user.setPhone1(phones[0]);
                        if (phones.length > 1) {
                            user.setPhone2(phones[1]);
                        }
                    }
                }
            }
            return user;
        }

    }

    @Data
    private static class Group {
        private String name;
        private List<String> members;
    }

    private static class GroupAttributesMapper implements AttributesMapper<Group> {

        @SuppressWarnings("unchecked")
        public Group mapFromAttributes(Attributes attrs) throws NamingException {
            Group group = new Group();
            group.setName((String) attrs.get("name").get());
            group.setMembers(Collections.list((Enumeration<String>) attrs.get("member").getAll()));
            return group;
        }

    }

    @Data
    private static class Program {
        private String name;
        private List<String> groups;
    }

    private static class ProgramAttributesMapper implements AttributesMapper<Program> {

        @SuppressWarnings("unchecked")
        public Program mapFromAttributes(Attributes attrs) throws NamingException {
            Program program = new Program();
            program.setName((String) attrs.get("name").get());
            program.setGroups(Collections.list((Enumeration<String>) attrs.get("member").getAll())
                    .stream().filter(member -> member.contains("Groups"))
                    .collect(Collectors.toList()));
            return program;
        }

    }

    private GroupEntity getGroup(String name) {
        return groupRepository.findByName(name)
                .orElseGet(() -> new GroupEntity(name));
    }

    private GroupEntity getGroupOrThrow(String name) {
        return groupRepository.findByName(name)
                .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND_ERROR_MESSAGE));
    }

    private ProgramEntity getProgram(String name) {
        return programRepository.findByName(name)
                .orElseGet(() -> new ProgramEntity(name));
    }

    private OrganizationEntity getUbtsOrThrow() {
        return organizationRepository.findByName("УБТС")
                .orElseThrow(() -> new DatabaseItemNotFoundException(UBTS_NOT_FOUND_ERROR_MESSAGE));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeStudents() {
        log.info("Synchronizing users...");

        List<User> ldapUsers = ldapTemplate.search(
                query().base(ldapStudentsBase)
                        .where("objectClass").is("user"),
                new UserAttributesMapper());
        List<UserEntity> savedLdapUsers = userService.getLdapUsers();

        ldapUsers.stream()
                .filter(ldapUser -> savedLdapUsers.stream()
                        .noneMatch(userEntity -> userEntity.getLogin().equals(ldapUser.getLogin())))
                .forEach(ldapUser -> {
                    log.info("Adding user: {}", ldapUser.getLogin());
                    UserEntity userEntity = new UserEntity();
                    userEntity.setLogin(ldapUser.getLogin());
                    userEntity.setFirstName(ldapUser.getFirstName());
                    userEntity.setLastName(ldapUser.getLastName());
                    userEntity.setPassword("N/A");
                    userEntity.setLdapUser(true);
                    userEntity.setPhone1(ldapUser.getPhone1());
                    userEntity.setPhone2(ldapUser.getPhone2());
                    userService.createUser(userEntity);
                });
        ldapUsers.stream().filter(ldapUser -> savedLdapUsers.stream()
                .anyMatch(userEntity -> userEntity.getLogin().equals(ldapUser.getLogin())))
                .forEach(ldapUser -> {
                    log.info("Updating user: {}", ldapUser.getLogin());
                    UserEntity userEntity = userService.getUser(ldapUser.getLogin());
                    userEntity.setPhone1(ldapUser.getPhone1());
                    userEntity.setPhone2(ldapUser.getPhone2());
                    userRepository.save(userEntity);
                });
        userService.getLdapUsers().stream()
                .filter(userEntity -> ldapUsers.stream()
                        .noneMatch(ldapUser -> ldapUser.getLogin().equals(userEntity.getLogin())))
                .forEach(userEntity -> {
                    log.info("Deleting user: {}", userEntity.getLogin());
                    userService.deleteUser(userEntity.getId());
                });

        log.info("User synchronization complete.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeGroups() {
        log.info("Synchronizing groups...");

        List<Group> ldapGroups = ldapTemplate.search(
                query().base(ldapGroupsBase)
                        .where("objectClass").is("group"),
                new GroupAttributesMapper());
        List<GroupEntity> savedLdapGroups = groupRepository.findAll();

        ldapGroups.forEach(ldapGroup -> {
            log.info("Creating/updating group {}", ldapGroup.getName());
            GroupEntity groupEntity = getGroup(ldapGroup.getName());
            List<UserEntity> users = new ArrayList<>();
            ldapGroup.getMembers().forEach(memberDn -> {
                String strippedMemberDn = memberDn.replace("," + ldapBase, "");
                User user = ldapTemplate.lookup(strippedMemberDn, new UserAttributesMapper());
                log.info("Assigning user {} to group {}", user.getLogin(), ldapGroup.getName());
                UserEntity userEntity = userService.getUser(user.getLogin());
                userEntity.setGroup(groupEntity);
                users.add(userEntity);
            });
            groupEntity.setUsers(users);
            groupRepository.save(groupEntity);
            userRepository.saveAll(users);
        });
        savedLdapGroups.stream()
                .filter(groupEntity -> ldapGroups.stream()
                        .noneMatch(ldapGroup -> ldapGroup.getName().equals(groupEntity.getName())))
                .forEach(groupEntity -> {
                    log.info("Deleting group: {}", groupEntity.getName());
                    groupRepository.deleteById(groupEntity.getId());
                });

        log.info("Group synchronization complete.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizePrograms() {
        log.info("Synchronizing programs...");

        List<Program> ldapPrograms = ldapTemplate.search(
                query().base(ldapProgramsBase)
                        .where("objectClass").is("group"),
                new ProgramAttributesMapper());
        List<ProgramEntity> savedLdapPrograms = programRepository.findAll();

        ldapPrograms.forEach(ldapProgram -> {
            log.info("Creating/updating program {}", ldapProgram.getName());
            ProgramEntity programEntity = getProgram(ldapProgram.getName());
            List<GroupEntity> groups = new ArrayList<>();
            ldapProgram.getGroups().forEach(groupDn -> {
                String strippedGroupDn = groupDn.replace("," + ldapBase, "");
                Group group = ldapTemplate.lookup(strippedGroupDn, new GroupAttributesMapper());
                log.info("Assigning group {} to program {}", group.getName(), ldapProgram.getName());
                GroupEntity groupEntity = getGroupOrThrow(group.getName());
                groupEntity.setProgram(programEntity);
                groups.add(groupEntity);
            });
            programEntity.setGroups(groups);
            programEntity.setOrganization(getUbtsOrThrow());
            programRepository.save(programEntity);
            groupRepository.saveAll(groups);
        });
        savedLdapPrograms.stream()
                .filter(programEntity -> ldapPrograms.stream()
                        .noneMatch(ldapProgram -> ldapProgram.getName().equals(programEntity.getName())))
                .forEach(programEntity -> {
                    log.info("Deleting program: {}", programEntity.getName());
                    groupRepository.deleteById(programEntity.getId());
                });

        log.info("Program synchronization complete.");
    }

    @Override
    public void synchronizeLdap() {
        log.info("Initiating LDAP synchronization");
        synchronizeStudents();
        synchronizeGroups();
        synchronizePrograms();
        log.info("LDAP synchronization complete.");
    }

}
