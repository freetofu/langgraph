/*
 * Copyright (c) 2017 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customlanguagebodyeditor;

import com.nomagic.magicdraw.ui.languagebody.LanguageBodyEditor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Martynas Lelevicius
 */
class SampleLanguageBodyEditor implements LanguageBodyEditor
{
	private final JTextArea textArea;
	private final JScrollPane scrollPane;

	public SampleLanguageBodyEditor()
	{
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
	}

	@Override
	public String getBody()
	{
		return textArea.getText();
	}

	@Override
	public void setBody(String body)
	{
		textArea.setText(body);
	}

	@Override
	public void setEditable(boolean editable)
	{
		textArea.setEditable(editable);
	}

	@Override
	public Component getComponent()
	{
		return scrollPane;
	}
}
