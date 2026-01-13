/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.actiontypes;

import com.nomagic.magicdraw.actions.ActionsStateUpdater;
import com.nomagic.magicdraw.actions.MDStateAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action which represents choice between several options.
 * One option maps to one action. All such actions should be collected in one actions category.
 *
 * @author Donatas Simkunas
 */
public class SimpleChoiceAction extends MDStateAction
{
	/**
	 * Current choice value. Value is shared between several actions.
	 */
	private final String[] currentChoice;

	/**
	 * Constructor for SimpleChoiceAction.
	 *
	 * @param id            Action id
	 * @param name          action name
	 * @param currentChoice shared choice value
	 */
	public SimpleChoiceAction(String id, String name, String[] currentChoice)
	{
		super(id, name, null, null);
		setGrouped(true);
		this.currentChoice = currentChoice;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// changing choice only when it is not already selected.
		if (!getState())
		{
			currentChoice[0] = getName();
			// showing information about changes
			JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), "Selected:" + getName());
		}
		// updating other actions state, making only one action from group selected.
		// This can be done in several ways, one way is to call for every action updateState() method.
		// If action execution will create session, actions will be updated automatically.
		ActionsStateUpdater.updateActionsState();
	}

	/**
	 * @see com.nomagic.actions.NMAction#updateState()
	 */
	@Override
	public void updateState()
	{
		// setting this action state to true only when it is current choice.
		setState(getName().equals(currentChoice[0]));
	}

}