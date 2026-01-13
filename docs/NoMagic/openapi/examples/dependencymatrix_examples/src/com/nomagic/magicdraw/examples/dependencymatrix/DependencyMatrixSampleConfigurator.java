/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.jidesoft.plaf.xerto.VerticalLabelUI;
import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixConfigurator;
import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.DependencyExtractor;
import com.nomagic.magicdraw.dependencymatrix.datamodel.editing.DependencyEditor;
import com.nomagic.magicdraw.dependencymatrix.ui.table.renderer.RendererHelper;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Collection;

/**
 * This configurator crates specific matrix type which has sample editing and dependency extraction features.
 * Representation of each cell is also changed
 *
 * @author Vytautas Dagilis
 */
public class DependencyMatrixSampleConfigurator extends DependencyMatrixConfigurator
{

    public DependencyMatrixSampleConfigurator(String diagramType)
    {
        super(diagramType);
    }

    @Override
    public TableCellRenderer createTableCellRenderer()
    {
        //use different renderer for cells
        return new SampleRenderer();
    }

    @Override
    public TableCellRenderer createColumnCellRenderer()
    {
        //use different renderer for column header
        return new SampleRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus,
                                                           int row, int column)
            {
                Component tableCellRendererComponent = super
                        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (tableCellRendererComponent instanceof JLabel cellRendererComponent)
                {
					cellRendererComponent.setUI(getLabelUI(!RendererHelper.isExpandedColumnGroupHeader(column)));
                }
                return tableCellRendererComponent;
            }

            private BasicLabelUI getLabelUI(boolean vertical)
            {
                if (vertical)
                {
                    return new VerticalLabelUI(false);
                }
                else
                {
                    return new BasicLabelUI();
                }
            }
        };
    }

    @Override
    public TableCellRenderer createRowCellRenderer()
    {
        return new SampleRenderer();
    }

    @Override
    public void configureDependencyHandlers(Collection<DependencyExtractor> extractors,
                                            Collection<DependencyEditor> editors)
    {
        //unregister standard dependency handler components
        extractors.clear();
        editors.clear();
        //adding required components for dependency management
        extractors.add(new SampleDependencyExtractor());
        editors.add(new SampleDependencyEditor());
    }

}
