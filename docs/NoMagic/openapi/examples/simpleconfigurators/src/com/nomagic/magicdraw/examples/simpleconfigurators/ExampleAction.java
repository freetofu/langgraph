/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.ui.ScalableImageIcon;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Example action class, shows message with text "This is Example Action.".
 *
 * @author Donatas Simkunas
 */
class ExampleAction extends MDAction
{
	ExampleAction(String id, String name, @CheckForNull KeyStroke stroke, @CheckForNull String group)
	{
		super(id, name, stroke, group);
	}

	/**
	 * Creates action with name "Example Action", and with key stroke E +CTRL+SHIFT
	 */
	public ExampleAction()
	{
		super("Example Action", "Example Action", KeyStroke.getKeyStroke(KeyEvent.VK_E, NMAction.MENU_SHORTCUT_MASK + KeyEvent.SHIFT_MASK), null);
		setLargeIcon(new ScalableImageIcon(getClass(), "main_toolbar_icon.gif"));
	}

	/**
	 * Method is called when action should be performed. Showing simple message.
	 *
	 * @param e event causes action call.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), "This is: " + getName());
	}

}
