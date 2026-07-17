package io.jmix.petclinic.portal.view.registration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.router.Route;
import io.jmix.core.Copier;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.core.entity.EntityValues;
import io.jmix.flowui.view.DefaultMainViewParent;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.Install;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.Target;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.petclinic.portal.entity.User;
import io.jmix.petclinic.portal.registration.UserRegistrationForm;
import io.jmix.petclinic.portal.registration.UserRegistrationService;

@Route(value = "user-registration/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "portal_UserRegistrationForm.detail")
@ViewDescriptor(path = "user-registration-form-detail-view.xml")
@EditedEntityContainer("userRegistrationFormDc")
public class UserRegistrationFormDetailView extends StandardDetailView<UserRegistrationForm> {

    @Autowired
    private Copier copier;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private UserRegistrationService userRegistrationService;

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(final SaveContext saveContext) {

        User user = userRegistrationService.registerUser(getEditedEntity());
        return Set.of(user);
    }
}
