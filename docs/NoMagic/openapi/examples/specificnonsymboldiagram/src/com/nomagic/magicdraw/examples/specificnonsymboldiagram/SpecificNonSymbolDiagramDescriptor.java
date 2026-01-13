/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificnonsymboldiagram;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.DiagramAction;
import com.nomagic.magicdraw.icons.IconsFactory;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.diagrams.NonSymbolDiagramDescriptor;
import com.nomagic.magicdraw.uml.diagrams.NonSymbolDiagramUtil;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.ui.ResizableIcon;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

/**
 * Specific non symbol diagram descriptor.
 *
 * @author Martynas Lelevicius
 */
class SpecificNonSymbolDiagramDescriptor extends NonSymbolDiagramDescriptor<SpecificPanel>
{
	private static final String ICON_FILE = "icons/specificnonsymboldiagram.svg";

	@Override
	public boolean isCreatable()
	{
		return true;
	}

	@Override
	public String getDiagramTypeId()
	{
		return "SpecificNonSymbolDiagram";
	}

	@Override
	public String getSingularDiagramTypeHumanName()
	{
		return "Specific Non Symbol Diagram";
	}

	@Override
	public String getPluralDiagramTypeHumanName()
	{
		return "Specific Non Symbol Diagrams";
	}

	@Override
	public ResizableIcon getSVGIcon()
	{
		return IconsFactory.getNotScaledIcon(getSVGIconURL());
	}

	@Override
	public String getKind()
	{
		return "Specific Diagram";
	}

	/**
	 * URL to small icon for diagram.
	 */
	@Override
	public URL getSmallIconURL()
	{
		return getSVGIconURL();
	}

	@CheckForNull
	@Override
	public AMConfigurator getDiagramShortcutsConfigurator()
	{
		return getDiagramCommandBarConfigurator();
	}

	@CheckForNull
	@Override
	public AMConfigurator getDiagramCommandBarConfigurator()
	{
		return manager -> {
			final ActionsCategory category = new ActionsCategory();
			manager.addCategory(category);
			category.addAction(new ShowNameAction());
		};
	}

	@Override
	public SpecificNonSymbolDiagramContent createDiagramContent(
			DiagramPresentationElement diagramPresentationElement)
	{
		return new SpecificNonSymbolDiagramContent();
	}

	private URL getSVGIconURL()
	{
		return getClass().getResource(ICON_FILE);
	}

	private static class ShowNameAction extends NMAction implements DiagramAction
	{
		@CheckForNull
		private DiagramPresentationElement mDiagram;

		public ShowNameAction()
		{
			super("SHOW_NAME", "Sow Name", KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), null);
		}

		@Override
		public void setDiagram(@CheckForNull DiagramPresentationElement diagram)
		{
			mDiagram = diagram;
		}

		@Override
		public void actionPerformed(@CheckForNull ActionEvent e)
		{
			final Component component = getComponent();
			if (component instanceof SpecificPanel)
			{
				JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(),
											  ((SpecificPanel) component).getNameLabel().getText());
			}
		}

		@CheckForNull
		private Component getComponent()
		{
			return mDiagram != null ? NonSymbolDiagramUtil.getDiagramContentComponent(mDiagram) : null;
		}

		@Override
		public void updateState()
		{
			final Component component = getComponent();
			setEnabled(component instanceof SpecificPanel &&
					   ((SpecificPanel) component).getNameLabel().getText().trim().length() > 0);
		}
	}

}
