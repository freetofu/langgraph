/*
 * Copyright (c) 2002 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.accessors;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.uml2.BehavioralFeatures;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.impl.ElementsFactory;

import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Plugin for generating setters/getters for selected class in browser tree classifiers (for all members).
 *
 * Using This plugin
 * 1. Create class or interface.
 * 2. Create attribute and set type of it.
 * 3. Right click at created Class or Interface selected Create setters/getters.
 *
 * @author Nerijus Jankevicius
 */
public class AccessorsGenerator extends Plugin
{

	/**
	 * Action, that do all the work.
	 */
	private GenerateAction generateAction;

	/**
	 * Initializing the plugin.
	 */
	@Override
	public void init()
	{
		generateAction = new GenerateAction();

		// add action into containment tree context
		ActionsConfiguratorsManager.getInstance()
				.addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
				{
					@Override
					public void configure(ActionsManager manager, Tree browser)
					{
						if (generateAction.canBeUsed(browser))
						{
							MDActionsCategory category = new MDActionsCategory();
							category.addAction(generateAction);
							manager.addCategory(category);
						}
					}

					@Override
					public int getPriority()
					{
						return AMConfigurator.MEDIUM_PRIORITY;
					}
				});
	}

	/**
	 * Action that generates accessors.
	 */
	static class GenerateAction extends DefaultBrowserAction
	{
		/**
		 * default constructor.
		 */
		public GenerateAction()
		{
			super("", "Create Setters/Getters", null, null);
		}

		/**
		 * Performs accessors generating.
		 *
		 * @param evt the action event.
		 */
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			// perform generate action for every selected classifier.
			Tree tree = getTree();
			if (tree != null)
			{
				for (Classifier classifier : collectClassifierInterfaces(tree))
				{
					generateAccessors(classifier);
				}
			}
		}

		/**
		 * Creates accessors for given classifier interface.
		 *
		 * @param ci the classifier interface.
		 */
		public void generateAccessors(Classifier ci)
		{
			// temporary collection for created operations.
			// later all of them must be added into classifier.

			Project project = Project.getProject(ci);

			// new session must be created.
			// All actions until session closing will be added into one command in CommandHistory.
			SessionManager.getInstance().createSession(project, "Create setters/getters");

			try // because ReadOnlyElement
			{
				// iterate all attributes and create accessors for every one.
				List<Operation> created = new ArrayList<>();
				for (Iterator<Property> propIterator = ModelHelper.attributes(ci); propIterator.hasNext(); )
				{
					Property at = propIterator.next();
					String attName = at.getName();

					// uppercase the first letter of attribute name.
					attName = attName.toUpperCase(java.util.Locale.ENGLISH).substring(0, 1) +
							  attName.substring(1);

					//  create setter

					// setter name.
					String setterName = "set" + attName;

					ElementsFactory elementsFactory = Project.getProject(ci).getElementsFactory();
					Operation op = elementsFactory.createOperationInstance();
					op.setName(setterName);

					// create new parameter of attribute type and name.
					Parameter par = elementsFactory.createParameterInstance();
					par.setName(at.getName());
					par.setType(at.getType());

					// add parameter into operation.
					ModelElementsManager.getInstance().addElement(par, op);

					// checking for already existing operations.
					boolean exists = false;
					for (Iterator<Operation> opIterator = ModelHelper.operations(ci); opIterator.hasNext() && !exists; )
					{
						// use this method to compare two operations.
						exists = BehavioralFeatures.isEqual(op, opIterator.next());
					}

					// add new operation only if such one do not exists.
					if (exists)
					{
						op.dispose();
					}
					else
					{
						created.add(op);
					}

					//   create getter

					// name of the getter. Specific name for boolean getter.
					//noinspection ConstantConditions
					String getterName = (at.getType().getName().equals("boolean") ? "is" : "get") + attName;

					op = elementsFactory.createOperationInstance();
					op.setName(getterName);

					// create return parameter.
					par = elementsFactory.createParameterInstance();
					par.setType(at.getType());
					par.setDirection(ParameterDirectionKindEnum.RETURN);

					// add parameter into operation.
					ModelElementsManager.getInstance().addElement(par, op);

					exists = false;
					for (Iterator<Operation> opIterator = ModelHelper.operations(ci); opIterator.hasNext() && !exists; )
					{
						exists = BehavioralFeatures.isEqual(opIterator.next(), op);
					}

					if (exists)
					{
						op.dispose();
					}
					else
					{
						created.add(op);
					}
				}

				// add all operations into classifier.
				for (Operation operation : created)
				{
					ModelElementsManager.getInstance().addElement(operation, ci);
				}
			}
			catch (ReadOnlyElementException e)
			{
				e.printStackTrace();
			}

			// close edit session (execute all commands).
			SessionManager.getInstance().closeSession(project);
		}

		/**
		 * Action can be used only if there are selected classifiers in the browser.
		 */
		public boolean canBeUsed(Tree tree)
		{
			return collectClassifierInterfaces(tree).size() > 0;
		}

		/**
		 * Updates menu item.
		 */
		@Override
		public void updateState()
		{
			boolean enable = false;
			Tree tree = getTree();
			if (tree != null)
			{
				enable = canBeUsed(tree);
			}
			setEnabled(enable);
		}

		/**
		 * Checks all selected browser nodes and collects only classifier interfaces.
		 */
		public Collection<Classifier> collectClassifierInterfaces(Tree tree)
		{
			Collection<Classifier> classifiers = new ArrayList<>();

			// iterate selected nodes.
			//noinspection Convert2streamapi
			for (Node node : Arrays.asList(tree.getSelectedNodes()))
			{
				// checks type of the node, because can be selected and code engineering sets.
				if (node.getUserObject() instanceof Element el)
				{

					// we need only classifier interfaces.
					if (el instanceof Classifier)
					{
						classifiers.add((Classifier) el);
					}
				}
			}

			return classifiers;
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
