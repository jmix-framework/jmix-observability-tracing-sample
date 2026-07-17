package io.jmix.petclinic.portal.registration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticated;
import io.jmix.petclinic.portal.entity.User;
import io.jmix.petclinic.portal.security.OwnerRole;
import io.jmix.petclinic.portal.service.OwnerBackendRegistration;
import io.jmix.petclinic.portal.service.OwnerBackendRegistrationService;
import io.jmix.security.role.assignment.RoleAssignmentModel;
import io.jmix.security.role.assignment.RoleAssignmentRoleType;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@Component
public class UserRegistrationService {

    private final OwnerBackendRegistrationService ownerBackendRegistrationService;
    private final DataManager dataManager;
    private final PasswordEncoder passwordEncoder;
    private final ObservationRegistry observationRegistry;

    public UserRegistrationService(OwnerBackendRegistrationService ownerBackendRegistrationService, DataManager dataManager, PasswordEncoder passwordEncoder, ObservationRegistry observationRegistry) {
        this.ownerBackendRegistrationService = ownerBackendRegistrationService;
        this.dataManager = dataManager;
        this.passwordEncoder = passwordEncoder;
        this.observationRegistry = observationRegistry;
    }

    @Authenticated
    public User registerUser(UserRegistrationForm userRegistrationForm) {
        return Observation.createNotStarted("registerUser", observationRegistry)
                .observe(() -> performRegisterUser(userRegistrationForm));

    }

    private User performRegisterUser(UserRegistrationForm userRegistrationForm) {
        User user = savePortalUser(userRegistrationForm);

        OwnerBackendRegistration ownerBackendRegistration = dataManager.create(OwnerBackendRegistration.class);

        ownerBackendRegistration.setId(user.getId());
        ownerBackendRegistration.setFirstName(userRegistrationForm.getFirstName());
        ownerBackendRegistration.setLastName(userRegistrationForm.getLastName());
        ownerBackendRegistration.setEmail(userRegistrationForm.getEmail());
        ownerBackendRegistration.setTelephone(userRegistrationForm.getTelephone());

        ownerBackendRegistrationService.registerOwner(ownerBackendRegistration);

        return user;
    }

    private User savePortalUser(UserRegistrationForm userRegistrationForm) {
        User ownerUser = dataManager.create(User.class);

        ownerUser.setFirstName(userRegistrationForm.getFirstName());
        ownerUser.setLastName(userRegistrationForm.getLastName());
        ownerUser.setEmail(userRegistrationForm.getEmail());
        ownerUser.setUsername(userRegistrationForm.getEmail());
        String encodedPassword = passwordEncoder.encode(userRegistrationForm.getPassword());
        ownerUser.setPassword(encodedPassword);

        RoleAssignmentModel ownerRole = dataManager.create(RoleAssignmentModel.class);
        ownerRole.setUsername(ownerUser.getUsername());
        ownerRole.setRoleCode(OwnerRole.CODE);
        ownerRole.setRoleType(RoleAssignmentRoleType.RESOURCE);

        return dataManager.save(ownerUser, ownerRole).get(ownerUser);
    }
}