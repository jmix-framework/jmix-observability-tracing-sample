package io.jmix.petclinic.portal.service;

import io.jmix.petclinic.portal.entity.Owner;
import io.jmix.restds.annotation.RemoteService;

@RemoteService(store = "petclinic", remoteName = "petclinic_OwnerRegistrationService")
public interface OwnerBackendRegistrationService {

    Owner registerOwner(OwnerBackendRegistration ownerBackendRegistration);
}