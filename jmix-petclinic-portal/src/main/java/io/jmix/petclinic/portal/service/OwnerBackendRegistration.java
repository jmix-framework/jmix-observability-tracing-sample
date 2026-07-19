package io.jmix.petclinic.portal.service;

import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

@JmixEntity(name = "petclinic_OwnerRegistration")
public class OwnerBackendRegistration {
    @JmixId
    private UUID id;

    @JmixProperty(mandatory = true)
    @NotNull
    @InstanceName
    private String firstName;

    @JmixProperty(mandatory = true)
    @NotNull
    private String lastName;

    @Email
    private String email;

    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
