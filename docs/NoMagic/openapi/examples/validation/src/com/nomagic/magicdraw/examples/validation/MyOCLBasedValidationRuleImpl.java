package com.nomagic.magicdraw.examples.validation;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.validation.DefaultValidationRuleImpl;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;

import java.util.*;

/**
 * This is an example of OCL based validation rule. Implementation specifies which property changes of model elements
 * are important. The validation will be triggered only on changes of the specified properties.
 * In order to plug your own solving actions you have to override
 * {@link com.nomagic.magicdraw.validation.DefaultValidationRuleImpl#createAnnotation(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint)}
 * method.
 *
 * @author Rimvydas Vaidelis
 */
public class MyOCLBasedValidationRuleImpl extends DefaultValidationRuleImpl
{
    private final List<NMAction> mActions;

    public MyOCLBasedValidationRuleImpl()
    {
        mActions = new ArrayList<>();
        NMAction action = new MyAction("MY_ACTION_ID", "Test", null);
        mActions.add(action);
    }

    /**
     * Returns a map of classes and smart listener configurations.
     *
     * @return smart listener configurations by class.
     */
    @Override
    public Map<Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
    {
        return Collections.singletonMap(InstanceSpecification.class, Collections.singletonList(SmartListenerConfig.NAME_CONFIG));
    }

    /**
     * Creates and returns an <code>Annotation</code> object.
     *
     * @param element    a model element or presentation element that annotation is assigned to.
     * @param constraint a constraint that is violated.
     * @return annotation object.
     */
    @Override
    protected Annotation createAnnotation(Element element, Constraint constraint)
    {
        return new Annotation(element, constraint, mActions);
    }
}
