package io.jmix.petclinic.service;

import io.jmix.core.DataManager;
import io.jmix.petclinic.entity.OwnerRegistration;
import io.jmix.petclinic.entity.owner.Owner;
import io.jmix.rest.annotation.RestHttpMethod;
import io.jmix.rest.annotation.RestMethod;
import io.jmix.rest.annotation.RestService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@RestService("petclinic_OwnerRegistrationService")
public class OwnerRegistrationService {

    @Autowired
    protected DataManager dataManager;
    @Autowired
    protected Validator validator;

    @RestMethod(value = "registerOwner", httpMethods = RestHttpMethod.POST)
    public Owner registerOwner(OwnerRegistration ownerRegistration) {

        Set<ConstraintViolation<OwnerRegistration>> validationResult = validator.validate(ownerRegistration);

        if (!validationResult.isEmpty()) {
            throw new ConstraintViolationException(validationResult);
        }

        Owner owner = dataManager.create(Owner.class);
        owner.setId(ownerRegistration.getId());
        owner.setFirstName(ownerRegistration.getFirstName());
        owner.setLastName(ownerRegistration.getLastName());
        owner.setEmail(ownerRegistration.getEmail());
        owner.setTelephone(ownerRegistration.getTelephone());
        owner.setAddress("");
        owner.setCity("");
        return dataManager.save(owner);
    }
}
