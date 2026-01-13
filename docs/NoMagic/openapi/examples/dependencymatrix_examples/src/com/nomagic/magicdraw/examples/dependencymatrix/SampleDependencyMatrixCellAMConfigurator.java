/*
 * Copyright (c) 2019 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixCellAMConfigurator;
import com.nomagic.magicdraw.dependencymatrix.persistence.PersistenceManager;
import com.nomagic.magicdraw.dependencymatrix.ui.DependencyMatrixSelection;

/**
 * Configurator shows how to add sample action to dependency matrix cell menu.
 * This action will be added if single or multiple cells are selected.
 *
 * @author Tomas Lukosius
 */
public class SampleDependencyMatrixCellAMConfigurator implements DependencyMatrixCellAMConfigurator
{
	@Override
	public void configure(ActionsManager manager, PersistenceManager settings, DependencyMatrixSelection selection)
	{
		String categoryName = "Sample Actions";
		ActionsCategory category = new ActionsCategory(categoryName, categoryName);
		manager.addCategory(category);

		if (selection.isMultipleCellsSelected())
		{
			category.addAction(new SampleMultipleCellAction(selection));
		}
	}

	@Override
	public int getPriority()
	{
		return MEDIUM_PRIORITY;
	}
}
