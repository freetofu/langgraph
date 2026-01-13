/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.dependencymatrix.datamodel.ElementNode;
import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.AbstractMatrixCell;
import com.nomagic.magicdraw.dependencymatrix.datamodel.cell.DependencyEntry;
import com.nomagic.magicdraw.dependencymatrix.datamodel.editing.DependencyEditor;
import com.nomagic.magicdraw.dependencymatrix.persistence.PersistenceManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.task.ProgressStatus;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * This sample shows how to add actions to create menu category. For all actions there are
 * additional information about the dependency matrix cell.
 * <p/>
 * NOTE: if <code>canCreate</code> returns <code>false</code>, no menu items will be created
 * using <code>createAddActions</code>. The same applies to <code>canEdit</code> and <code>createDelete</code> methods
 *
 * @author Vytautas Dagilis
 */
class SampleDependencyEditor implements DependencyEditor
{
    @Override
    public void init(PersistenceManager settings, @CheckForNull ProgressStatus status)
    {

    }

    @Override
    public void createAddActions(PersistenceManager settings, ElementNode row, ElementNode column,
                                 AbstractMatrixCell cell,
                                 ActionsCategory mainCategory, ActionsCategory rowToColumn,
                                 ActionsCategory columnToRow)
    {
        if (canCreate(settings, row, column, cell))
        {
            rowToColumn.addAction(new SampleAction("Add Dependency Action"));
        }
    }

    @Override
    public void createEditActions(PersistenceManager settings, ElementNode row, ElementNode column,
                                  AbstractMatrixCell cell,
                                  ActionsCategory main, ActionsCategory deleteActions)
    {
        if (canEdit(settings, row, column, cell))
        {
            ActionsCategory category = new ActionsCategory("edit", "Edit");
            category.setNested(false);
            category.setDisplayHeader(true);
            for (DependencyEntry dependencyEntry : cell.getDependencies())
            {
                for (Element element : dependencyEntry.getCause())
                {
                    if (element instanceof NamedElement namedElement)
                    {
                        category.addAction(new SetNameAction(namedElement));
                    }
                }
            }
            if (!category.isEmpty())
            {
                main.addAction(category, 0);
            }
        }
    }

    @Override
    public boolean canCreate(PersistenceManager settings, ElementNode row, ElementNode column,
                             @CheckForNull AbstractMatrixCell cell)
    {
        return row.getElement() != column.getElement(); //disable delete action only where row is equal to the column
    }

    @Override
    public boolean canEdit(PersistenceManager persistenceManager, ElementNode row, ElementNode column,
                           AbstractMatrixCell cell)
    {
        return !row.equals(column); //disable edit action only where row is equal to the column
    }

    @Override
    public void clear()
    {
    }

    @Override
    public void elementUpdated(Collection<Element> element)
    {
    }

    /**
     * Action used in sample to edit Dependency element name. Shows input dialog and sets name, if it was changed
     */
    private static class SetNameAction extends NMAction
    {
        private final NamedElement mNamedElement;

        public SetNameAction(NamedElement namedElement)
        {
            super(namedElement.getID(), "Edit Dependency Name", null, null);
            mNamedElement = namedElement;
        }

        @Override
        public void actionPerformed(@CheckForNull ActionEvent e)
        {
            String oldName = mNamedElement.getName();
            String newName = JOptionPane.showInputDialog("Enter new name:", oldName);
            if (newName != null && !oldName.contentEquals(newName))
            {
                Project project = Project.getProject(mNamedElement);

                SessionManager.getInstance().createSession(project,"Set Name");
                mNamedElement.setName(newName);
                SessionManager.getInstance().closeSession(project);
            }
        }
    }
}
