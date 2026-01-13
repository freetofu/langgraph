package com.nomagic.magicdraw.examples.validation;

import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.validation.DefaultValidationRuleImpl;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.impl.PropertyNames;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validation rule that checks whether names of attributes in classes and interfaces conforms to
 * Java naming convention.
 *
 * @author Rimvydas Vaidelis
 */
public class JavaConstantNameValidationRuleImpl extends DefaultValidationRuleImpl
{
    private static final Pattern pattern = Pattern.compile("[A-Z_\\d]*");

    @Override
	public Map<java.lang.Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
    {
        Map<java.lang.Class<? extends Element>, Collection<SmartListenerConfig>> configMap = new HashMap<>(2);
        SmartListenerConfig config = new SmartListenerConfig();
        config.listenToNested(PropertyNames.ATTRIBUTE).listenTo(PropertyNames.IS_STATIC).
                listenTo(PropertyNames.IS_READ_ONLY).listenTo(PropertyNames.NAME);
        Collection<SmartListenerConfig> configs = Collections.singletonList(config);
        configMap.put(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class, configs);
        configMap.put(com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface.class, configs);
        return configMap;
    }

    @Override
	protected Collection<? extends Element> getInvalidElements(Constraint constraint,
                                                               Collection<? extends Element> elements)
    {
        Collection<Element> result = new ArrayList<>();
        for (Element element : elements)
        {
            if (element instanceof Classifier aClass)
            {
				Collection<Property> attributes = aClass.getAttribute();
                for (Property attribute : attributes)
                {
                    if (isJavaConstant(attribute))
                    {
                        String name = attribute.getName();
                        if (!isJavaConstantNameValid(name))
                        {
                            result.add(element);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean isJavaConstantNameValid(String name)
    {
        return pattern.matcher(name).matches();
    }

    public static boolean isJavaConstant(Property attribute)
    {
        return attribute.isStatic() && attribute.isReadOnly();
    }

    @Override
    protected Annotation createAnnotation(Element element, Constraint constraint)
    {
        FixJavaConstantNamesAction action = new FixJavaConstantNamesAction((Classifier) element);
        return new Annotation(element, constraint, Collections.singletonList(action));
    }
}
