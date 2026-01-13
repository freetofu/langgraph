/*
 * Copyright (c) 2002 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.hierarchyremover;

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
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plugin for removing some generalizable element with all derived elements.
 * @author Nerijus Jankevicius
 *
 */
public class HierarchyRemover extends Plugin
{
        /**
         * Action, that do all the work.
         */
        private RemoveAction removeAction;

        /**
         * Initializing the plugin.
         */
        @Override
		public void init()
        {
            removeAction = new RemoveAction();

	        // register action as containment tree context action.
			ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
			{
				@Override
				public void configure(ActionsManager manager, Tree browser)
				{
					if (removeAction.canBeUsed(browser))
					{
						MDActionsCategory category = new MDActionsCategory();
						category.addAction(removeAction);
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
         * Browser action, responsible for removing selected generalizable element
         * with all derived elements.
         */
		static class RemoveAction extends DefaultBrowserAction
        {
            public RemoveAction()
            {
                super("", "Remove with hierarchy", null, null);
            }

            @Override
			public void actionPerformed(ActionEvent evt)
            {
				Tree tree = getTree();
				if (tree != null)
				{
					// perform generate action for every selected generalizable element.
					for (Classifier classifier : collectGeneralizable(tree))
					{
						removeHierarchy(classifier);
					}
				}
	        }

            /**
             * Removes classifier with all derived classifiers.
             * @param el the generalizable element.
             */
            public void removeHierarchy(Classifier el)
            {
				Project project = Project.getProject(el);
                // new session must be created.
                // All actions until session closing will be added into one command in CommandHistory.
                SessionManager.getInstance().createSession(project, "Remove hierarchy");

                try // because ReadOnlyElement
                {
					// temporary collection for classifiers that must be removed.
					List<Classifier> toRemove = new ArrayList<>();
					toRemove.add(el);
                    collectChildren(el, toRemove);

                    // remove all
					for (int i = toRemove.size() - 1; i >= 0; --i)
					{
						ModelElementsManager.getInstance().removeElement(toRemove.get(i));
                    }
                }
                catch(ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }

	            // close edit session (execute all commands).
                SessionManager.getInstance().closeSession(project);

            }

            /**
             * Collects all elements, derived from passed Generalizable element.
             */
            public void collectChildren(Classifier parent, Collection<Classifier> c)
            {
                // iterator for all generalization links from this parent
				for (Generalization generalization : parent.get_generalizationOfGeneral())
				{
					Classifier child = generalization.getSpecific();
					if (child != null)
					{
						c.add(child);
						// collect children of this element.
						collectChildren(child, c);
					}
				}
            }

            /**
             * Action can be used only if there are selected generalizable elements in the browser.
             */
            public boolean canBeUsed(Tree tree)
            {
                return collectGeneralizable(tree).size()>0;
            }

            /**
             * Update menu item.
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
             * Checks all selected tree nodes and collects only generalizable elements.
             * @param tree the tree for checking.
             * @return the collection of selected Generalizable Elements.
             */
            public Collection<Classifier> collectGeneralizable(Tree tree)
            {
				// iterate selected nodes.
				// checks type of the node, because can be selected and code engineering sets.
				return Arrays.stream(tree.getSelectedNodes())
						.map(Node::getUserObject)
						.filter(userObject -> userObject instanceof Classifier)
						.map(userObject -> (Classifier) userObject)
						.collect(Collectors.toList());
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
