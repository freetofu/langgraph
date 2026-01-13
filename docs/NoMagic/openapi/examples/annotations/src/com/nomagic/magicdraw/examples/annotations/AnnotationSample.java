/*
 * Copyright (c) 2010 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.annotations;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.annotation.AnnotationAction;
import com.nomagic.magicdraw.annotation.AnnotationManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.BaseElement;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Plugin for adding/removing annotation to some element.
 * <p>
 * Using this plugin
 * 1. Create any element
 * 3. Right-click on this element in Containment Tree.
 * 4. Select "Create Annotation" to create an Annotation for an Element
 * 4. Select "Remove Annotation" to remove an Annotation from an Element
 *
 * @author Mindaugas Ringys
 */
public class AnnotationSample extends Plugin
{

	/**
	 * Action, that do all the work.
	 */
	private CreateAnnotationAction createAnnotationAction;
	private RemoveAnnotationAction removeAnnotationAction;

	/**
	 * Initializing the plugin.
	 */
	@Override
	public void init()
	{
		createAnnotationAction = new CreateAnnotationAction();
		removeAnnotationAction = new RemoveAnnotationAction();

		// add action into containment tree context
		ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
		{
			@Override
			public void configure(ActionsManager manager, Tree browser)
			{
				MDActionsCategory category = new MDActionsCategory();
				category.addAction(createAnnotationAction);
				category.addAction(removeAnnotationAction);
				manager.addCategory(category);
			}

			@Override
			public int getPriority()
			{
				return AMConfigurator.MEDIUM_PRIORITY;
			}
		});
	}

	/**
	 * Action that creates an annotation for an Element.
	 */
	static class CreateAnnotationAction extends DefaultBrowserAction
	{
		/**
		 * default constructor.
		 */
		public CreateAnnotationAction()
		{
			super("", "Create Annotation", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement element = (BaseElement) getSelectedObject();
			Project project = Project.getProject(element);
			AnnotationManager instance = AnnotationManager.getInstance(project);
			//noinspection ConstantConditions
			Annotation annotation1 = new Annotation(Annotation.getSeverityLevel(project, Annotation.ERROR), "sample", "Annotation Sample 1", element);
			//noinspection ConstantConditions
			Annotation annotation2 = new Annotation(Annotation.getSeverityLevel(project, Annotation.ERROR), "sample", "Annotation Sample 2", element,
													Collections.singletonList(new AnnotationSampleAction(element)));
			instance.add(annotation1);
			instance.add(annotation2);
			instance.update();
		}

		/**
		 * Updates menu item.
		 */
		@Override
		public void updateState()
		{
			setEnabled(getSelectedObject() instanceof BaseElement);
		}
	}

	/**
	 * Actions used in annotation sample.
	 */
	private static final class AnnotationSampleAction extends MDAction implements AnnotationAction
	{
		private final BaseElement target;

		private AnnotationSampleAction(BaseElement target)
		{
			super("ANNOTATION_SAMPLE_ACTION", "Annotation Sample Action", null, null);
			this.target = target;
		}

		@Override
		/*
		 * This method is called when action is selected on multiple elements with the same annotation
		 */
		public void execute(Collection<Annotation> annotations)
		{
			for (Annotation annotation : annotations)
			{
				BaseElement target = annotation.getTarget();
				if (target != null)
				{
					Application.getInstance().getGUILog().showMessage("Annotation Sample action executed for " + target.getHumanName());
				}
			}
		}

		@Override
		/*
		 * This method is called when action is selected on multiple elements with the same annotation
		 */
		public boolean canExecute(Collection<Annotation> annotations)
		{
			return true;
		}

		@Override
		/*
		 * This method is called when action is selected a single element with annotation
		 */
		public void actionPerformed(ActionEvent e)
		{
			Application.getInstance().getGUILog().showMessage("Annotation Sample action executed for " + target.getHumanName());
		}
	}

	/**
	 * Action that removes an annotation for an Element.
	 */
	static class RemoveAnnotationAction extends DefaultBrowserAction
	{
		/**
		 * default constructor.
		 */
		public RemoveAnnotationAction()
		{
			super("", "Remove Annotation", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement element = (BaseElement) getSelectedObject();
			AnnotationManager instance = AnnotationManager.getInstance(element);
			//noinspection ConstantConditions
			List<Annotation> list = instance.getAnnotations(element);
			for (Annotation annotation : list)
			{
				if (annotation.getKind().equals("sample"))
				{
					instance.remove(annotation);
				}
			}
			instance.update();
		}

		/**
		 * Updates menu item.
		 */
		@Override
		public void updateState()
		{
			setEnabled(getSelectedObject() instanceof BaseElement);
		}
	}

	/**
	 * Return true always, because this plugin does not have any close specific actions.
	 */
	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}
}