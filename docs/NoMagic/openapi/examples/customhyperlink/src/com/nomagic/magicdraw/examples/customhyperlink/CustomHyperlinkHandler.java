/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customhyperlink;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.hyperlinks.Hyperlink;
import com.nomagic.magicdraw.hyperlinks.HyperlinkHandler;
import com.nomagic.magicdraw.hyperlinks.ui.HyperlinkEditor;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import javax.swing.*;

/**
 * Custom hyperlink handler.
 *
 * @author Martynas Lelevicius
 */
class CustomHyperlinkHandler implements HyperlinkHandler
{
	private final Icon mIcon;

	CustomHyperlinkHandler()
	{
		mIcon = new ScalableImageIcon(getClass(), "img.gif");
	}

	@Override
	public boolean isSupportedProtocol(String protocol)
	{
		return CustomHyperlink.PROTOCOL.equals(protocol);
	}

	@Override
	public Icon getIcon(Hyperlink link)
	{
		return mIcon;
	}

	@Override
	public Hyperlink create(@CheckForNull String text, String url, @CheckForNull Project context)
	{
		return new CustomHyperlink(text, url);
	}

	@Override
	public HyperlinkEditor getEditor()
	{
		return new CustomHyperlinkPanel(this);
	}

	@Override
	public void activate(@CheckForNull Element element, Hyperlink link)
	{
		Application.getInstance().getGUILog().showMessage("Custom hyperlink activated: " + link.getUrl());
	}
}
