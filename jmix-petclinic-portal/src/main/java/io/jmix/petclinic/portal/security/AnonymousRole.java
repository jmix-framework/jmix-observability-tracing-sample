package io.jmix.petclinic.portal.security;

import io.jmix.petclinic.portal.registration.UserRegistrationForm;
import io.jmix.petclinic.portal.service.OwnerBackendRegistration;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "AnonymousRole", code = AnonymousRole.CODE, scope = "UI")
public interface AnonymousRole {
    String CODE = "anonymous-role";

    @ViewPolicy(viewIds = {"petclinic_UsageHelpView", "portal_UserRegistrationForm.detail"})
    void views();

    @EntityAttributePolicy(entityClass = UserRegistrationForm.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = UserRegistrationForm.class, actions = EntityPolicyAction.ALL)
    void userRegistrationForm();

    @EntityAttributePolicy(entityClass = OwnerBackendRegistration.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = OwnerBackendRegistration.class, actions = EntityPolicyAction.ALL)
    void ownerBackendRegistration();

}