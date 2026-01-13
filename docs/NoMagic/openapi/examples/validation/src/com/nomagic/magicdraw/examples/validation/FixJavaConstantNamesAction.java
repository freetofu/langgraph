package com.nomagic.magicdraw.examples.validation;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.annotation.AnnotationAction;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Example of the action that can be performed on multiple targets.
 *
 * @author Rimvydas Vaidelis
 * @version 1.0
 */
public class FixJavaConstantNamesAction extends NMAction implements AnnotationAction
{
	@Nonnull
	private final Classifier mClassifier;

	/**
	 * Constructor.
	 *
	 * @param classifier classifier.
	 */
	public FixJavaConstantNamesAction(@Nonnull Classifier classifier)
	{
		super("FIX_JAVA_CONSTANT_NAME", "Fix Java Constant Name", 0);
		mClassifier = classifier;
	}

	/**
	 * Executes the action.
	 *
	 * @param e event caused execution.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Project project = Project.getProject(mClassifier);

		final SessionManager sm = SessionManager.getInstance();
		sm.createSession(project,"Renaming attributes");
		try
		{
			fixJavaConstantNames(mClassifier);
			sm.closeSession(project);
		}
		catch (Exception exc)
		{
			logError(exc);
			sm.cancelSession(project);
		}
	}

	/**
	 * Executes the action on specified targets.
	 *
	 * @param annotations action targets.
	 */
	@Override
	public void execute(Collection<Annotation> annotations)
	{
		if (annotations == null || annotations.isEmpty())
		{
			return;
		}
		Project project = Project.getProject(mClassifier);
		final SessionManager sm = SessionManager.getInstance();
		sm.createSession(project,"Renaming attributes");
		try
		{
			for (Annotation annotation : annotations)
			{
				final BaseElement baseElement = annotation.getTarget();
				if (baseElement instanceof Classifier)
				{
					fixJavaConstantNames((Classifier) baseElement);
				}
			}
			sm.closeSession(project);
		}
		catch (Exception e)
		{
			logError(e);
			sm.cancelSession(project);
		}
	}

	/**
	 * Renames all attributes of the specified classifier.
	 *
	 * @param classifier classifier that attributes should be renamed.
	 */
	private static void fixJavaConstantNames(@Nonnull Classifier classifier)
	{
		final Collection<Property> attributes = classifier.getAttribute();
		for (Property attribute : attributes)
		{
			if (JavaConstantNameValidationRuleImpl.isJavaConstant(attribute))
			{
				final String name = attribute.getName();
				if (!JavaConstantNameValidationRuleImpl.isJavaConstantNameValid(name))
				{
					attribute.setName(toJavaConstantName(name));
				}
			}
		}
	}

	/**
	 * Returns java constant name.
	 *
	 * @param name a name of the attribute.
	 * @return java constant name.
	 */
	private static String toJavaConstantName(@Nonnull String name)
	{
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < name.length(); i++)
		{
			final char charAt = name.charAt(i);
			boolean wasLower = false;
			if (Character.isLetter(charAt) && Character.isLowerCase(charAt))
			{
				builder.append(Character.toUpperCase(charAt));
				wasLower = true;
			}
			else
			{
				builder.append(charAt);
			}
			if (wasLower && i + 1 < name.length() && Character.isUpperCase(name.charAt(i + 1)))
			{
				builder.append('_');
			}
		}
		return builder.toString();
	}

	@Override
	public boolean canExecute(Collection<Annotation> annotations)
	{
		return true;
	}

	/**
	 * Log exception message.
	 *
	 * @param exception to log.
	 */
	private static void logError(@Nonnull Exception exception)
	{
		LogManager.getLogger("VALIDATION_EXAMPLE").error(exception.getMessage(), exception);
	}
}
