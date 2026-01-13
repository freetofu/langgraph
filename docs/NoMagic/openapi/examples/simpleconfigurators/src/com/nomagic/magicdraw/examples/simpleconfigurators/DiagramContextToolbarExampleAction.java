/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.magicdraw.ui.actions.DiagramContextToolbarAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.ui.ScalableImageIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Martynas Lelevicius
 */
class DiagramContextToolbarExampleAction extends DiagramContextToolbarAction
{
	DiagramContextToolbarExampleAction()
	{
		super("Diagram Context Toolbar Example Action", "Diagram Context Toolbar Example Action",
			  new ScalableImageIcon(DiagramContextToolbarExampleAction.class, "diagram_toolbar_icon.gif"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), getSelected().size() + " elements selected");
	}
}
