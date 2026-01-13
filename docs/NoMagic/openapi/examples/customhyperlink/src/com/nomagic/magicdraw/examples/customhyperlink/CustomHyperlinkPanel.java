/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customhyperlink;

import com.nomagic.magicdraw.hyperlinks.HyperlinkHandler;
import com.nomagic.magicdraw.hyperlinks.ui.HyperlinkEditorPanel;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;

/**
 * Custom hyperlink editor panel.
 *
 * @author Martynas Lelevicius
 */
class CustomHyperlinkPanel extends HyperlinkEditorPanel
{
	public CustomHyperlinkPanel(HyperlinkHandler handler)
	{
		super("Custom hyperlink", "Type custom link", true, handler, CustomHyperlink.PROTOCOL);
	}

	@Override
	public boolean isProjectScope()
	{
		return false;
	}

	@Override
	protected void browse()
	{
		final String url = JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogOwner(),
													   "Enter custom hyperlink URL:", CustomHyperlink.PROTOCOL + "://");
		if (url != null)
		{
			setHyperlinkUrlText(url);
		}
	}
}
