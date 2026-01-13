/*
 *
 * Copyright (c) 2003 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsCreator;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.nomagic.magicdraw.actions.MDActionsManager;

/**
 * Actions used in specific diagram.
 *
 * @author Mindaugas Ringys
 */
public class SpecificDiagramActions
{
	private static final MDActionsManager ACTIONS = new MDActionsManager();

	static
	{
		ActionsCategory category = new ActionsCategory();
		ACTIONS.addCategory(category);

		ActionsCreator creator = ActionsProvider.getInstance().getCreator();
		ActionsManager classDiagramActions = creator.createClassDiagramActions().clone();

		creator.createCommonDiagramsActions(category);

		category.addAction(new EntityAction());
		category.addAction(new MyClassAction());
		category.addAction(classDiagramActions.getActionFor(ActionsID.ADD_PACKAGE));
	}

	public static MDActionsManager getActions()
	{
		return ACTIONS;
	}
}
