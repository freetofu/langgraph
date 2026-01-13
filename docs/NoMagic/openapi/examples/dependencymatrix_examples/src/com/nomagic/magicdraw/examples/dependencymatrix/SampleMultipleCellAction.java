/*
 * Copyright (c) 2019 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.dependencymatrix.datamodel.ElementNode;
import com.nomagic.magicdraw.dependencymatrix.ui.DependencyMatrixSelection;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Action shows how to iterate through dependency matrix selection, how to find all selected cells.
 * Also use {{@link com.nomagic.magicdraw.dependencymatrix.configuration.MatrixDataHelper#getMatrixData(Diagram)}} to get matrix data,
 * from which you can obtain cell object like so: {{@link com.nomagic.magicdraw.dependencymatrix.datamodel.MatrixData#getValue(ElementNode, ElementNode)}}
 * if needed.
 *
 * @author Tomas Lukosius
 */
public class SampleMultipleCellAction extends NMAction
{
	public static final String TEXT = "List Selected Cells";
	private final DependencyMatrixSelection selection;

	public SampleMultipleCellAction(DependencyMatrixSelection selection)
	{
		super(TEXT, TEXT, null, null);
		this.selection = selection;
	}

	@Override
	public void actionPerformed(@CheckForNull ActionEvent e)
	{
		Collection<DependencyMatrixSelection.CellDescriptor> selectedCells = selection.getSelectedCells();
		StringBuilder text = new StringBuilder("Selected cells: [");
		for (DependencyMatrixSelection.CellDescriptor cellDescriptor : selectedCells)
		{
			ElementNode row = cellDescriptor.getRow();
			ElementNode column = cellDescriptor.getColumn();
			text.append("(").append(getHumanName(row)).append(":").append(getHumanName(column)).append(")");
		}
		text.append("]");
		MainFrame mainFrame = Application.getInstance().getMainFrame();
		JOptionPane.showMessageDialog(mainFrame, text.toString());
	}

	private static String getHumanName(ElementNode row)
	{
		Element element = row.getElement();
		if (element == null)
		{
			return "None";
		}
		return element.getHumanName();
	}
}
