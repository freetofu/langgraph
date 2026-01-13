package com.nomagic.magicdraw.examples.customgenerictable;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.generictable.GenericTableManager;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.actions.SingleModelElementAction;
import com.nomagic.magicdraw.utils.MDLog;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;

import java.awt.event.ActionEvent;

/**
 * Demonstrates how Generic Table can be customized from an external plugin.
 *
 * @author Mindaugas Genutis
 */
public class CustomGenericTableExamplePlugin extends Plugin
{
	/**
	 * ID of the action.
	 */
	private static final String CUSTOM_ACTION_ID = "CUSTOM_ACTION_ID";

	/**
	 * Name of the action.
	 */
	public static final String CUSTOM_ACTION_NAME = "Custom Action";

	@Override
	public void init()
	{
		GenericTableManager.addGenericTableToolbarConfigurator(manager -> {
			MDAction action = new SingleModelElementAction(CUSTOM_ACTION_ID, CUSTOM_ACTION_NAME, null, "")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Diagram genericTableDiagram = (Diagram) getElement();
					if (genericTableDiagram != null)
					{
						MDLog.getGeneralLog().info("CustomGenericTableExamplePlugin.actionPerformed : " + genericTableDiagram.getHumanName());
					}
				}
			};

			ActionsCategory category = new ActionsCategory();
			category.addAction(action);
			manager.addCategory(category);
		});
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