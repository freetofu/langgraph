/*
 * Copyright (c) 2017 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customlanguagebodyeditor;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.languagebody.LanguageBodyEditorManager;

/**
 * @author Martynas Lelevicius
 */
public class CustomLanguageBodyEditorPlugin extends Plugin
{
	@Override
	public void init()
	{
		LanguageBodyEditorManager.getInstance().addFactory("Sample Language", new SampleLanguageBodyEditorFactory());
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}
