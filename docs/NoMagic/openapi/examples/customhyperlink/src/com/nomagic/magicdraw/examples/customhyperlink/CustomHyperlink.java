/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customhyperlink;

import com.nomagic.magicdraw.hyperlinks.Hyperlink;

import javax.annotation.CheckForNull;

/**
 * Custom hyperlink.
 *
 * @author Martynas Lelevicius
 */
class CustomHyperlink implements Hyperlink
{
	public final static String PROTOCOL = "customprotocol";
	private final String mText;
	private final String mUrl;

	public CustomHyperlink(@CheckForNull String text, String url)
	{
		mText = text;
		mUrl = url;
	}

	@Override
	public String getUrl()
	{
		return mUrl;
	}

	@CheckForNull
	@Override
	public String getText()
	{
		return mText;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public String getProtocol()
	{
		return PROTOCOL;
	}

	@Override
	public String getTypeText()
	{
		return "Custom protocol";
	}

	@Override
	public String toString()
	{
		return getUrl();
	}
}
