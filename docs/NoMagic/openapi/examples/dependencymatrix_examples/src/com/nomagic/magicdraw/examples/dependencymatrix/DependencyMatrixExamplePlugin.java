/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixAMConfigurator;
import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixActionRegistry;
import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixConfigurator;
import com.nomagic.magicdraw.dependencymatrix.configuration.MatrixDataHelper;
import com.nomagic.magicdraw.dependencymatrix.datamodel.ElementNode;
import com.nomagic.magicdraw.dependencymatrix.datamodel.MatrixData;
import com.nomagic.magicdraw.dependencymatrix.persistence.PersistenceManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;

/**
 * This sample shows main features of Dependency Matrix which may be extended/customized.
 * <p/>
 * These features include: Custom matrix edit actions, additional Dependency Matrix actions, and Dependency Matrix Cell renderers.
 * <p/>
 * Custom matrix editors allow to edit model data using actions, added into the matrix cell menu. Additionally editors provide information about editable/not editable cells.
 * <p/>
 * Additional actions may be added into dependency matrix row/column, header and cell menu, without any additional logic attached.
 * <p/>
 * Renderers are used to display dependencies between row and column elements. Instead of arrows, it is possible to use other icons or any Java component to display required dependency information
 *
 * @author Vytautas Dagilis
 */
public class DependencyMatrixExamplePlugin extends Plugin
{
    public final String DEPENDENCY_MATRIX_SAMPLE = "Sample Extended Matrix";

    @Override
    public void init()
    {
        //registration of new matrix type.
        //Do not forget that diagramType value should exactly match new custom diagram type and should be unique diagram type in the application.
        DependencyMatrixConfigurator
                .registerConfiguration(new DependencyMatrixSampleConfigurator(DEPENDENCY_MATRIX_SAMPLE));

        //Below are examples how to add additional actions to the dependency matrix. No custom configurator needs to be registered.
        addRowOrColumnAction();
        ActionsConfiguratorsManager.getInstance().addDiagramCommandBarConfigurator(DEPENDENCY_MATRIX_SAMPLE,
                                                                                   new AddSampleActionMenuConfigurator(
                                                                                           "Export to MS Word"));
        ActionsConfiguratorsManager.getInstance().addDiagramShortcutsConfigurator(DEPENDENCY_MATRIX_SAMPLE,
                                                                                  new AddSampleActionMenuConfigurator(
                                                                                          "Sample Short cut Action"));

        DependencyMatrixActionRegistry.getInstance().addCellContextAMConfigurator(new SampleDependencyMatrixCellAMConfigurator());
    }

    /**
     * Adds new action to the row/column context menu for All matrices
     */
    private static void addRowOrColumnAction()
    {
        DependencyMatrixActionRegistry.getInstance().addContextConfigurator(new DependencyMatrixAMConfigurator()
        {
            @Override
            public void configure(ActionsManager manager, Collection<ElementNode> elementNodes, boolean forRow, PersistenceManager settings)
            {
                ActionsCategory category = new ActionsCategory();
                manager.addCategory(category);
                int selectedSize = elementNodes.size();
                if (selectedSize > 0)
                {
                    if (forRow)
                    {
                        category.addAction(new SampleAction("Row Action for " + selectedSize + " nodes."));
                    }
                    else
                    {
                        category.addAction(new SampleAction("Column Action for " + selectedSize + " nodes."));
                    }
                }
                else
                {
                    category.addAction(new ExportAllElementsAction(settings));
                }
            }

            @Override
            public int getPriority()
            {
                return HIGH_PRIORITY;
            }
        });
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

    /**
     * Standard configurator which adds Sample action with provided text.
     */
    private static final class AddSampleActionMenuConfigurator implements AMConfigurator
    {

        private final String mText;

        private AddSampleActionMenuConfigurator(String text)
        {
            mText = text;
        }

        @Override
        public void configure(ActionsManager mngr)
        {
            ActionsCategory category = new ActionsCategory();
            mngr.addCategory(category);
            category.addAction(new SampleAction(mText));
        }

        @Override
        public int getPriority()
        {
            return HIGH_PRIORITY;
        }
    }

    /**
     * Action which displays all row and column element in the dialog
     */
    private static class ExportAllElementsAction extends NMAction
    {
        /**
         * Dependency matrix settings
         */
        private final PersistenceManager mSettings;

        public ExportAllElementsAction(PersistenceManager settings)
        {
            super("id", "Export to MS Word", null, null);
            mSettings = settings;
        }

        @Override
        public void actionPerformed(@CheckForNull ActionEvent e)
        {
            Element matrixElement = mSettings.getMatrixSettings().getMatrixElement();
            if (matrixElement instanceof Diagram matrix)
            {
				MatrixData matrixData = MatrixDataHelper.buildMatrix(matrix);
                //if wrong element was provided - data may be null
                if (matrixData != null)
                {
                    Collection<Element> allElements = new HashSet<>();
                    allElements.addAll(matrixData.getRowElements());
                    allElements.addAll(matrixData.getColumnElements());
                    StringBuilder wholeElementList = new StringBuilder();
                    for (Element allElement : allElements)
                    {
                        if (allElement instanceof NamedElement namedElement)
                        {
							wholeElementList.append(namedElement.getName()).append("\n");
                        }
                    }

                    JOptionPane.showMessageDialog(Application.getInstance().getMainFrame(), wholeElementList.toString(),
                                                  "All row and column elements", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
