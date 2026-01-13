/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.AbstractMatrixCell;
import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.DependencyEntry;
import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.MatrixCellView;
import com.nomagic.magicdraw.dependencymatrix.ui.table.renderer.RendererHelper;
import com.nomagic.magicdraw.ui.Icons;
import com.nomagic.magicdraw.ui.zoom.ZoomHelper;
import com.nomagic.magicdraw.ui.zoom.Zoomable;
import com.nomagic.ui.ResizableIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This renderer displays dependency causing element icon in the cell instead of directed dependency icon
 */
public class SampleRenderer extends DefaultTableCellRenderer implements Zoomable
{
    private final Color lightGray = new Color(224, 224, 224);

    private Font originalFont;
    private float zoomFactor;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column)
    {
        //dependencies in the dependency matrix model are stored as MatrixCellView elements, which
        //have references to the AbstractMatrixCell instance
        JLabel cellContents = (JLabel) super
                .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String cellText = "";
        cellContents.setIcon(null);
        if (!isSelected)
        {
            cellContents.setBackground(Color.white);
        }
        if (value instanceof MatrixCellView matrixCellView)
        {
			AbstractMatrixCell cell = matrixCellView.getCell();
            if (cell != null)
            {
                //AbstractMatrixCell stores information about dependency direction, dependency causing elements
                //and other relevant information
                for (DependencyEntry dependencyEntry : cell.getDependencies())
                {
                    for (Element cause : dependencyEntry.getCause())
                    {
                        ResizableIcon iconFor = Icons.getIconFor(cause);
                        cellContents.setIcon(ZoomHelper.getScaledIcon(iconFor, zoomFactor));
                    }
                }
                if (!cell.getDependencies().isEmpty() && !isSelected)
                {
                    cellContents.setBackground(lightGray);
                }
            }
        }
        else if (value instanceof Integer)
        {
            //Dependency count entries are stored as simple Integer instances
            cellText = value.toString();
        }
        else
        {
            //Otherwise it is Row/Column header elements
            Element element = RendererHelper.getHeaderElement(table, row, column);
            if (element instanceof NamedElement namedElement)
            {
				cellText = RendererHelper.getNameExtension(table, row, column, namedElement.getName());
                if (RendererHelper.isExpandedColumnGroupHeader(column))
                {
                    cellContents.setBackground(new Color(200, 200, 200));
                }
                else
                {
                    if (isGrey(row, column))
                    {
                        cellContents.setBackground(lightGray);
                        cellContents.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                    }
                    else
                    {
                        cellContents.setBackground(Color.WHITE);
                        cellContents.setForeground(isSelected ? lightGray : Color.BLACK);
                    }
                }
            }
        }

        cellContents.setText(cellText);
        return cellContents;
    }

    private boolean isGrey(int row, int column)
    {
        boolean isGrey = row % 2 == 0;
        if (isGrey && column != 0)
		{
			isGrey = column % 2 == 0;
		}
        return isGrey;
    }

    @Override
    public void zoomTo(float zoomFactor)
    {
        if (zoomFactor != this.zoomFactor)
        {
            this.zoomFactor = zoomFactor;
            setScaledFont();
        }
    }

    @Override
    public void setFont(Font font)
    {
        if (font != originalFont)
        {
            originalFont = font;
            setScaledFont();
        }
    }

    private void setScaledFont()
    {
        super.setFont(ZoomHelper.getScaledFont(originalFont, zoomFactor));
    }

}
