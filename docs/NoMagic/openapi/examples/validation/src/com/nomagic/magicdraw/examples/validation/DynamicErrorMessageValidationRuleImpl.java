package com.nomagic.magicdraw.examples.validation;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.validation.ElementValidationRuleImpl;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfigProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import javax.annotation.CheckForNull;
import java.util.*;

/**
 * @author Dainius Jankunas
 */
@SuppressWarnings("unused")
public class DynamicErrorMessageValidationRuleImpl implements ElementValidationRuleImpl, SmartListenerConfigProvider
{
	private static final int MIN_NAME_LENGTH = 5;

	@Override
	public void init(Project project, Constraint constraint)
	{

	}

	@Override
	public Set<Annotation> run(Project project, Constraint constraint, Collection<? extends Element> elements)
	{
		Set<Annotation> result = new HashSet<>();

		for (Element element : elements)
		{
			if (!isValid(element))
			{
				List<NMAction> actions = new ArrayList<>();
				actions.add(new MyAction("MY_ACTION_ID", "Test", null));
				result.add(new Annotation(element, constraint, getErrorText(element), actions));
			}
		}

		return result;
	}

	private static String getErrorText(Element element)
	{
		return "Name for [" + element.getID() + "] should be longer than " + MIN_NAME_LENGTH + " symbols.";
	}

	private static boolean isValid(Element element)
	{
		NamedElement el = (NamedElement) element;
		return el.getName().length() > MIN_NAME_LENGTH;
	}

	@Override
	public void dispose()
	{

	}

	@CheckForNull
	@Override
	public Map<java.lang.Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
	{
		return Collections.singletonMap(Class.class, Collections.singletonList(SmartListenerConfig.NAME_CONFIG));
	}
}
