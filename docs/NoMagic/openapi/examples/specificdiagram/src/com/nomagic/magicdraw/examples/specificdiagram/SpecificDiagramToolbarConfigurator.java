/*
 * Copyright (c) 2001 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.DiagramInnerToolbarConfiguration;
import com.nomagic.magicdraw.ui.actions.BaseDiagramToolbarConfigurator;

/**
 * Configure specific diagram toolbar.
 *
 * @author Mindaugas Ringys
 */
public class SpecificDiagramToolbarConfigurator extends BaseDiagramToolbarConfigurator
{
	private final String superType;

	public SpecificDiagramToolbarConfigurator(String superType)
	{
		this.superType = superType;
	}

	/**
	 * Configure given manager.
	 *
	 * @param manager the manager for configuration.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		final ActionsManager actions = SpecificDiagramActions.getActions();
		manager.addCategory(createSelectionToolbar(actions, superType));
		manager.addCategory(createToolsToolbar(actions, superType));
		manager.addCategory(createCommonToolbarConfiguration(actions, superType));

		final DiagramInnerToolbarConfiguration category = new DiagramInnerToolbarConfiguration("SPECIFIC_DIAGRAM_ELEMENTS", null, "Specific Diagram Elements", true);
		manager.addCategory(category);

		category.addAction(actions.getActionFor(EntityAction.DRAW_ENTITY_ACTION));
		category.addAction(actions.getActionFor(MyClassAction.DRAW_MY_CLASS_ACTION));
		category.addAction(actions.getActionFor(ActionsID.ADD_PACKAGE));
	}
}