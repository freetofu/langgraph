/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.DiagramType;

/**
 * Example of using actions in MagicDraw.
 * This example shows how to:
 * <ul>
 * <li>Add action to main menu.</li>
 * <li>Add action to main toolbar.</li>
 * <li>Add action to browser tree.</li>
 * <li>Add action to (class) diagram.</li>
 * </ul>
 *
 * @author Donatas Simkunas
 */
public class ActionsExamplePlugin extends Plugin
{
	/**
	 * Adding actions on plugin init.
	 */
	@Override
	public void init()
	{
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();

		MDAction action = new ExampleAction();
		// Adding action to main menu
		manager.addMainMenuConfigurator(new MainMenuConfigurator(action));
		// Adding action to main toolbar
		manager.addMainToolbarConfigurator(new MainToolbarConfigurator(action));
		// Adding action to main menu. Action will be added after "new project" menu item
		manager.addMainMenuConfigurator(new InsertAfterConfigurator(new ExampleAction("After_NEW", "After NEW", null, null), ActionsID.NEW_PROJECT));

		// adding action to containment browser
		DefaultBrowserAction browserAction = new BrowserExampleAction();
		BrowserConfigurator configurator = new BrowserConfigurator(browserAction);
		manager.addContainmentBrowserContextConfigurator(configurator);
		manager.addContainmentBrowserShortcutsConfigurator(configurator);

		// adding action to class diagram
		final DefaultDiagramAction diagramAction = new DiagramExampleAction();
		manager.addDiagramContextConfigurator(DiagramType.UML_CLASS_DIAGRAM, new DiagramContextConfigurator(diagramAction));
		manager.addDiagramShortcutsConfigurator(DiagramType.UML_CLASS_DIAGRAM, new DiagramShortcutsConfigurator(diagramAction));
		manager.addDiagramToolbarConfigurator(DiagramType.UML_CLASS_DIAGRAM, new DiagramToolbarConfigurator(diagramAction));
		manager.addDiagramCommandBarConfigurator(DiagramType.UML_CLASS_DIAGRAM, new DiagramCommandBarConfigurator(diagramAction));
		manager.addDiagramContextToolbarConfigurator(DiagramType.UML_CLASS_DIAGRAM, new DiagramContextToolbarConfigurator(new DiagramContextToolbarExampleAction()));
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}