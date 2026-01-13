/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.selectionactions;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsID;

/**
 * Class for configuring manager and add actions to file category.
 * @author Donatas Simkunas
 */
public class Configurator implements AMConfigurator
{
	/**
	 * Action to be added.
	 */
	private final NMAction action;

	/**
	 * Creates configurator.
	 * @param action action to be added to manager.
	 */
	public Configurator(NMAction action)
	{
		this.action = action;
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(ActionsManager)
	 *  Method  adds new action.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		ActionsCategory files = (ActionsCategory) manager.getActionFor(ActionsID.FILE);
		if( files != null )
		{
			files.addAction(action);
		}
	}
	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}