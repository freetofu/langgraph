/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificnonsymboldiagram;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.diagrams.NonSymbolDiagramContent;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.impl.PropertyNames;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Specific non symbol diagram content.
 *
 * @author Martynas Lelevicius
 */
class SpecificNonSymbolDiagramContent implements NonSymbolDiagramContent<SpecificPanel>
{
	private SpecificPanel mPanel;
	private NameListener mNameListener;

	@Override
	public SpecificPanel createComponent()
	{
		mPanel = new SpecificPanel();
		return mPanel;
	}

	@Override
	public void activate()
	{
		if (mNameListener == null)
		{
			final Project project = Application.getInstance().getProject();
			if (project != null)
			{
				// initialize panel
				final com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package model = project.getPrimaryModel();
				final JTextField nameLabel = mPanel.getNameLabel();
				nameLabel.setText(model.getName());
				mNameListener = new NameListener(nameLabel);
				model.addPropertyChangeListener(mNameListener);
			}
		}
	}

	@Override
	public void dispose()
	{
		final Project project = Application.getInstance().getProject();
		if (project != null && mNameListener != null)
		{
			project.getPrimaryModel().removePropertyChangeListener(mNameListener);
			mNameListener = null;
		}
	}

	@Override
	public Dimension getComponentFullSize(SpecificPanel panel)
	{
		return panel.getPreferredSize();
	}

	@Override
	public Rectangle getPaintableBounds(SpecificPanel panel)
	{
		return new Rectangle(getComponentFullSize(panel));
	}

	private static class NameListener implements PropertyChangeListener
	{
		private final JTextField mField;

		public NameListener(JTextField field)
		{
			mField = field;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (PropertyNames.NAME.equals(evt.getPropertyName()))
			{
				mField.setText(((Model) evt.getSource()).getName());
			}
		}
	}
}
