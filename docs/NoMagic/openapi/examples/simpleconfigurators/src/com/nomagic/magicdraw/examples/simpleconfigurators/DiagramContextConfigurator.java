/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;

/**
 * Class for configuring diagram context menu actions.
 *
 * @author Donatas Simkunas
 */
class DiagramContextConfigurator implements DiagramContextAMConfigurator
{
	/**
	 * Action will be added to diagram context menu.
	 */
	private final DefaultDiagramAction action;

	/**
	 * Creates configurator which adds given action.
	 *
	 * @param action action to be added to manager.
	 */
	DiagramContextConfigurator(DefaultDiagramAction action)
	{
		this.action = action;
	}

	@Override
	public void configure(ActionsManager manager, DiagramPresentationElement diagram, PresentationElement[] selected, PresentationElement requestor)
	{
		final ActionsCategory category = new MDActionsCategory();
		category.addAction(action);
		manager.addCategory(category);
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}
}
