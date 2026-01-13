/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;

/**
 * Class for configuring main toolbar. It adds action to File toolbar.
 *
 * @author Donatas Simkunas
 */
class MainToolbarConfigurator implements AMConfigurator
{

	/**
	 * Action which will be added to main toolbar.
	 */
	private final MDAction action;

	/**
	 * Creates configurator with given action.
	 *
	 * @param action action to be added to main toolbar.
	 */
	MainToolbarConfigurator(MDAction action)
	{
		this.action = action;
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(ActionsManager)
	 * Method adds action to File category.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		// searching for Help action category
		for (ActionsCategory category : manager.getCategories())
		{
			// adding action to found category.
			if (category.getID().equals(ActionsID.FILE))
			{
				category.addAction(action);
			}
		}
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}