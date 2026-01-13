/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.BaseElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action which can be added to the browser tree.
 * This action shows type and name of selected nodes objects.
 *
 * @author Donatas Simkunas
 */
class BrowserExampleAction extends DefaultBrowserAction
{
	/**
	 * Creates action with name "Browser Example Action"
	 */
	BrowserExampleAction()
	{
		super("Browser Example Action", "Browser Example Action", KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK + KeyEvent.SHIFT_MASK), null);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final Tree tree = getTree();
		if (tree != null)
		{
			StringBuilder text = new StringBuilder("Selected elements:");
			final Node[] selectedNodes = tree.getSelectedNodes();
			for (Node node : selectedNodes)
			{
				final Object userObject = node.getUserObject();
				if (userObject instanceof BaseElement)
				{
					text.append("\n").append(((BaseElement) userObject).getHumanName());
				}
			}
			JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), text.toString());
		}
	}

	/**
	 * @see com.nomagic.actions.NMAction#updateState()
	 * Enables action when there are selected nodes in tree.
	 */
	@Override
	public void updateState()
	{
		boolean enable = false;
		Tree tree = getTree();
		if (tree != null)
		{
			enable = tree.getSelectedNode() != null;
		}
		setEnabled(enable);
	}

}