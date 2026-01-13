/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.validationhelper;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.validation.RuleViolationResult;
import com.nomagic.magicdraw.validation.ValidationHelper;
import com.nomagic.magicdraw.validation.ValidationRunData;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.annotation.CheckForNull;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;

/**
 * Validation example plugin for showing how to work with
 * {@link ValidationHelper} class. Example plugin adds 3 actions in Analyze menu group.
 * 1. for validating suite.
 * 2. for validating constraint.
 * 3. for validating element.
 * <p>
 * NOTE: For running example you need to open standard UML project.
 *
 * @author Modestas Mikuckas
 */
public class ValidationHelperExample extends Plugin
{
	@Override
	public void init()
	{
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		createMenuConfiguration(manager);
	}

	/**
	 * Adds inner category with actions to menu analyze group.
	 *
	 * @param configuratorsManager instance of {@link ActionsConfiguratorsManager}
	 */
	private static void createMenuConfiguration(ActionsConfiguratorsManager configuratorsManager)
	{
		AMConfigurator mainMenuConfigurator = new AMConfigurator()
		{
			@Override
			public void configure(ActionsManager manager)
			{
				ActionsCategory category = (ActionsCategory) manager.getActionFor(ActionsID.ANALYZE);
				if (category != null)
				{
					ActionsCategory innerCategory = new ActionsCategory();
					innerCategory.addAction(new ValidateSuiteAction());
					innerCategory.addAction(new ValidateConstraintsAction());
					innerCategory.addAction(new ValidateElementAction());
					category.addAction(innerCategory);
				}
			}

			@Override
			public int getPriority()
			{
				return AMConfigurator.HIGH_PRIORITY;
			}
		};
		configuratorsManager.addMainMenuConfigurator(mainMenuConfigurator);
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}

	/**
	 * Action for showing how to use {@link ValidationHelper} validate method.
	 * Validation runs UML Correctness suite.
	 */
	private static class ValidateSuiteAction extends NMAction
	{
		/**
		 * Action ID
		 */
		private static final String VALIDATION_SUITE_ACTION_ID = "VALIDATION_SUITE_ACTION_ID";

		/**
		 * Constructor.
		 */
		public ValidateSuiteAction()
		{
			super(VALIDATION_SUITE_ACTION_ID, "Validate suite", null);
		}

		@Override
		public void actionPerformed(@CheckForNull ActionEvent e)
		{
			Project project = Application.getInstance().getProject();
			//noinspection ConstantConditions
			Element elementWithPath = Finder.byQualifiedName()
					.find(project, "UML Standard Profile::Validation Profile::Active Validation::UML Correctness");
			if (elementWithPath instanceof Package && project != null)
			{
				ValidationRunData runData = new ValidationRunData((Package) elementWithPath,
																  Annotation.getSeverityLevel(project, Annotation.ERROR), false);
				ValidationHelper.validate(runData, "Validating..", null);
			}
		}

		@Override
		public void updateState()
		{
			setEnabled(Application.getInstance().getProject() != null);
		}
	}

	/**
	 * Action for showing how to use {@link ValidationHelper} validate method then
	 * Collection of constraints are passed.
	 * After validation is done opens validation window.
	 */
	private static class ValidateConstraintsAction extends NMAction
	{
		/**
		 * Validate Constraints action id.
		 */
		private static final String VALIDATE_CONSTRAINTS_ACTION_ID = "VALIDATE_CONSTRAINTS_ACTION_ID";

		/**
		 * Constructor.
		 */
		public ValidateConstraintsAction()
		{
			super(VALIDATE_CONSTRAINTS_ACTION_ID, "Validate Constraints", null);
		}

		@Override
		public void actionPerformed(@CheckForNull ActionEvent e)
		{
			Project project = Application.getInstance().getProject();
			//noinspection ConstantConditions
			Element elementWithPath = Finder.byQualifiedName()
					.find(project, "UML Standard Profile::Validation Profile::UML correctness constraints::Invalid Connector");
			if (elementWithPath instanceof Constraint && project != null)
			{
				ValidationRunData runData = new ValidationRunData(Collections.singletonList((Constraint) elementWithPath), "UML correctness constraints",
																  Annotation.getSeverityLevel(project, Annotation.ERROR), false);
				Collection<RuleViolationResult> results = ValidationHelper.validate(runData, "Validating..", null);
				ValidationHelper.openValidationWindow(runData, "CUSTOM_VALIDATION_ID", results);
			}
		}

		@Override
		public void updateState()
		{
			setEnabled(Application.getInstance().getProject() != null);
		}
	}

	/**
	 * Action for showing how to use {@link ValidationHelper} validateElement method.
	 */
	private static class ValidateElementAction extends NMAction
	{
		/**
		 * Validate Element action id.
		 */
		private static final String VALIDATE_ELEMENT_ACTION_ID = "VALIDATE_ELEMENT_ACTION_ID";

		/**
		 * Constructor.
		 */
		public ValidateElementAction()
		{
			super(VALIDATE_ELEMENT_ACTION_ID, "Validate Element", null);
		}

		@Override
		public void actionPerformed(@CheckForNull ActionEvent e)
		{
			Project project = Application.getInstance().getProject();
			//noinspection ConstantConditions
			Element elementWithPath = Finder.byQualifiedName()
					.find(project, "UML Standard Profile::Validation Profile::UML correctness constraints::Invalid Connector");
			if (elementWithPath instanceof Constraint && project != null)
			{
				ValidationHelper.validateElement(project.getPrimaryModel(), Collections.singletonList((Constraint) elementWithPath), true);
			}
		}

		@Override
		public void updateState()
		{
			setEnabled(Application.getInstance().getProject() != null);
		}
	}
}
