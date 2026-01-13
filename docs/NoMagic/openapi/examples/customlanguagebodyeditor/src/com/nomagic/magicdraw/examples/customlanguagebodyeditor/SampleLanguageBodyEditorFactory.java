/*
 * Copyright (c) 2017 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customlanguagebodyeditor;

import com.nomagic.magicdraw.ui.languagebody.LanguageBodyEditor;
import com.nomagic.magicdraw.ui.languagebody.LanguageBodyEditorFactory;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * @author Martynas Lelevicius
 */
class SampleLanguageBodyEditorFactory implements LanguageBodyEditorFactory
{
	@Override
	public LanguageBodyEditor createEditor(Element element)
	{
		return new SampleLanguageBodyEditor();
	}

	@Override
	public String getInstructions()
	{
		return "Sample language instructions.";
	}

}
