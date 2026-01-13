/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

/**
 * Class for configuring diagram toolbar.
 *
 * @author Donatas Simkunas
 */
class DiagramToolbarConfigurator implements AMConfigurator
{
	/**
	 * Action will be added to diagram toolbar.
	 */
	private final DefaultDiagramAction action;

	DiagramToolbarConfigurator(DefaultDiagramAction action)
	{
		this.action = action;
	}

	/**
	 * Configuring toolbar.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		if (manager.getActionFor(action.getID()) == null)
		{
			ActionsCategory category = (ActionsCategory) manager.getActionFor(ActionsID.CLASS_DIAGRAM_ELEMENTS);
			if (category != null)
			{
				category.addAction(action);
			}
		}
	}
}
