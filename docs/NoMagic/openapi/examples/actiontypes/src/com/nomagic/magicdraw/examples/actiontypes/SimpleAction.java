/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.actiontypes;

import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action which displays its name.
 *
 * @author Donatas Simkunas
 */
class SimpleAction extends MDAction
{
	public SimpleAction(@CheckForNull String id, String name)
	{
		super(id, name, null, null);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), "This is:" + getName());
	}

}