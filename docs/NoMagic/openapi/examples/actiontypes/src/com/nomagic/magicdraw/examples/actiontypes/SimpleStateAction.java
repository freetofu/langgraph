/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.actiontypes;

import com.nomagic.magicdraw.actions.MDStateAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action which represents state : selected or unselected.
 *
 * @author Donatas Simkunas
 */
public class SimpleStateAction extends MDStateAction
{
	private boolean iAmSelected;

	/**
	 * Constructor for SimpleStateAction.
	 *
	 * @param id   action id
	 * @param name action name
	 */
	public SimpleStateAction(String id, String name)
	{
		super(id, name, null, null);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// changing state
		iAmSelected = !iAmSelected;
		// showing changes
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), "This is:" + getName() + " checked:" + iAmSelected);
	}

	/**
	 * @see com.nomagic.actions.NMAction#updateState()
	 */
	@Override
	public void updateState()
	{
		setState(iAmSelected);
	}

}