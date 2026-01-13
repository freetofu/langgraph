/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextToolbarAMConfigurator;
import com.nomagic.magicdraw.ui.actions.DiagramContextToolbarAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;

/**
 * @author Martynas Lelevicius
 */
class DiagramContextToolbarConfigurator implements DiagramContextToolbarAMConfigurator
{
	private final DiagramContextToolbarAction action;

	DiagramContextToolbarConfigurator(DiagramContextToolbarAction action)
	{
		this.action = action;
	}

	@Override
	public void configure(ActionsManager manager, PresentationElement requestor)
	{
		final ActionsCategory category = new ActionsCategory();
		manager.addCategory(category);
		category.addAction(action);
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}
