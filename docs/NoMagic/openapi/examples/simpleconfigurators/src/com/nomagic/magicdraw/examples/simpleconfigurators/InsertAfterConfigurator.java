/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDAction;

import java.util.List;

/**
 * Class for configuring main menu. It adds new action after action with given id.
 *
 * @author Donatas Simkunas
 */
class InsertAfterConfigurator implements AMConfigurator
{
	/**
	 * Action will be added to manager.
	 */
	private final MDAction actionToInsert;

	/**
	 * After this action id action will be inserted.
	 */
	private final String insertAfterThisID;

	/**
	 * Creates configurator.
	 *
	 * @param actionToInsert action to be added to main menu.
	 */
	InsertAfterConfigurator(MDAction actionToInsert, String insertAfterThisID)
	{
		this.actionToInsert = actionToInsert;
		this.insertAfterThisID = insertAfterThisID;
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(ActionsManager)
	 * Methods adds action after some other given action.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		// searching for action after which insert should be done.
		final NMAction found = manager.getActionFor(insertAfterThisID);
		if (found != null)
		{
			// action found, inserting
			final ActionsCategory category = (ActionsCategory) manager.getActionParent(found);
			if (category != null)
			{
				final List<NMAction> actionsInCategory = category.getActions();
				final int indexOfFound = actionsInCategory.indexOf(found);
				actionsInCategory.add(indexOfFound + 1, actionToInsert);
				category.setActions(actionsInCategory);
			}
		}
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}
