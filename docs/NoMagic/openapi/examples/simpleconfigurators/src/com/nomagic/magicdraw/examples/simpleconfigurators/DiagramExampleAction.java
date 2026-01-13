/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.ui.ScalableImageIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action shows message: how many symbols are selected in diagram.
 *
 * @author Donatas Simkunas
 */
class DiagramExampleAction extends DefaultDiagramAction
{
	/**
	 * Creates diagram action with name "Diagram Example Action".
	 */
	DiagramExampleAction()
	{
		super("Diagram Example Action", "Diagram Example Action", KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK), null);
		setDescription("Diagram Example Action");
		setLargeIcon(new ScalableImageIcon(getClass(), "diagram_toolbar_icon.gif"));
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * Displaying how many symbols are selected  in diagram.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), getSelected().size() + " elements selected");
	}

	/**
	 * @see com.nomagic.actions.NMAction#updateState()
	 * Enabling action only when there are selected symbols in diagram.
	 */
	@Override
	public void updateState()
	{
		setEnabled(getSelected().size() > 0);
	}
}
