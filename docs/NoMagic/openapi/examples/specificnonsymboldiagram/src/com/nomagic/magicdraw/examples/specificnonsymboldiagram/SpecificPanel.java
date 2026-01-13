/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificnonsymboldiagram;

import javax.swing.*;
import java.awt.*;

/**
 * Display project model name.
 *
 * @author Martynas Lelevicius
 */
class SpecificPanel extends JPanel
{
	private final JTextField mNameLabel;

	SpecificPanel()
	{
		super(new GridBagLayout());
		mNameLabel = new JTextField();
		mNameLabel.setEditable(false);
		mNameLabel.setBorder(null);

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 0);
		gbc.weighty = 1;
		add(new JLabel("Project Model name:"), gbc);

		gbc.weightx = 1;
		gbc.insets = new Insets(10, 0, 10, 10);
		add(mNameLabel, gbc);
	}

	JTextField getNameLabel()
	{
		return mNameLabel;
	}
}
