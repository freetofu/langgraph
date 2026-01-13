/*
 *
 *  * Copyright (c) 2012 NoMagic, Inc. All Rights Reserved.
 *
 */

package com.nomagic.magicdraw.examples.generictablemanager;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.generictable.GenericTableManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Example plugin that demonstrates OpenAPI for GenericTableManager
 * Add and remove works just after generic table was created
 *
 * @author Rolandas Kasinskas
 */
public class GenericTableManagerExamplePlugin extends Plugin
{
    //Variable for differing names when creating multiple example table at once
    private int i = 1;

    //Created Generic Table diagram
    private Diagram createdDiagram;

    @Override
    public void init()
    {
        addBrowserContextMenuAction();
    }


    /**
     * Adds context menu to elements in the browser.
     */
    private void addBrowserContextMenuAction()
    {
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
        {
            @Override
            public void configure(ActionsManager manager, Tree browser)
            {
                MDActionsCategory category = new MDActionsCategory();
                category.addAction(new GenericTableManagerAction());

                //If generic table was created - show add, remove actions
                if (createdDiagram != null)
                {
                    category.addAction(new GenericTableManagerAddAction());
                    category.addAction(new GenericTableManagerRemoveAction());
                }
                manager.addCategory(category);
            }

            @Override
            public int getPriority()
            {
                return AMConfigurator.MEDIUM_PRIORITY;
            }
        });
    }

    /**
     * Remove selected element from last created generic table
     * If table does not have element or diagram was deleted, nothing happens
     */
    private class GenericTableManagerRemoveAction extends DefaultBrowserAction
    {
        /**
         * Constructs this action.
         */
        public GenericTableManagerRemoveAction()
        {
            super("GENERIC_TABLE_MANAGER_REMOVE", "Remove element from last created Generic Table", null, null);
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {

			//Get first selected element
            BaseElement baseElement = getFirstElement();
            if (baseElement instanceof Element element)
            {
                //Checks if generic table has wanted to remove element
				List<Element> rowElements = GenericTableManager.getRowElements(createdDiagram);
				boolean hasElement = false;
				for (Element rowElement1 : rowElements)
				{
					if (rowElement1.equals(element))
					{
						hasElement = true;
					}
				}
                if (createdDiagram != null && hasElement)
                {
                    Project project = Project.getProject(baseElement);

                    SessionManager sessionManager = SessionManager.getInstance();
                    sessionManager.createSession(project,"Remove element from last created Generic Table");

                    //Remove element from generic table
                    GenericTableManager.removeRowElement(createdDiagram, element);

                    //Table element types list

					//Get all row elements
                    rowElements = GenericTableManager.getRowElements(createdDiagram);
                    List<Object> tableElementTypes = rowElements.stream()
                            .map(BaseElement::getClassType)
                            .distinct()
                            .collect(Collectors.toList());

                    //Set table element types for shown elements
                    GenericTableManager.setTableElementTypes(createdDiagram, tableElementTypes);

                    sessionManager.closeSession(project);
                }
            }
        }
    }

    /**
     * Adds selected element to last created generic table
     */
    private class GenericTableManagerAddAction extends DefaultBrowserAction
    {
        /**
         * Constructs this action.
         */
        public GenericTableManagerAddAction()
        {
            super("GENERIC_TABLE_MANAGER_ADD", "Add element to last created Generic Table", null, null);
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            if (createdDiagram != null)
            {
                Project project = Project.getProject(createdDiagram);

                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.createSession(project,"Add element to last created Generic Table");

                //Get first selected element
                BaseElement baseElement = getFirstElement();
                if (baseElement instanceof Element element)
                {

					//Table element types list

					//Get all row elements
                    List<Element> rowElements = GenericTableManager.getRowElements(createdDiagram);

                    //Add table element types to set, to remove duplicates
                    Set<Class> set = rowElements.stream()
                            .map(BaseElement::getClassType)
                            .collect(Collectors.toSet());
                    //Add selected element type to element types
                    set.add(element.getClassType());
					List<Object> tableElementTypes = new ArrayList<>(set);

                    //Set table element types for shown elements
                    GenericTableManager.setTableElementTypes(createdDiagram, tableElementTypes);

                    //Add element to table
                    GenericTableManager.addRowElement(createdDiagram, element);

                }
                sessionManager.closeSession(project);
            }
        }
    }

    /**
     * Action for generic table manager
     * Create Generic table diagram
     * Set element types
     * Add selected element to table
     * Show some columns of added element
     */
    private class GenericTableManagerAction extends DefaultBrowserAction
    {
        /**
         * Constructs this action.
         */
        public GenericTableManagerAction()
        {
            super("GENERIC_TABLE_MANAGER", "Create Generic Table", null, null);
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            //Get first selected element
            BaseElement baseElement = getFirstElement();
            if (baseElement instanceof Element element)
            {
                Project project = Project.getProject(baseElement);

                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.createSession(project,"Create Generic Table");

				//Create Generic table diagram
                try
                {
                    createdDiagram = GenericTableManager.createGenericTable(project, "Generic table name" + i);
                    i++;

                    //Create table element types
                    //Can add multiple table element types
                    List<Object> tableElementTypes = new ArrayList<>();
                    tableElementTypes.add(element.getClassType());

                    //Set table element types for shown elements
                    GenericTableManager.setTableElementTypes(createdDiagram, tableElementTypes);

                    //Add element to table
                    GenericTableManager.addRowElement(createdDiagram, element);

                    //Get possible columns to show for element
                    List<Element> elements = new ArrayList<>();
                    List<Object> types = GenericTableManager.getTableElementTypes(createdDiagram);

                    //Handle just first element type for example
                    //Can add all element types to elements list
                    if (!types.isEmpty())
                    {
                        Object obj = types.get(0);
                        if (obj instanceof Element)
                        {
                            elements.add((Element) obj);
                        }
                        else if (obj instanceof Class clazz)
                        {
							if (StereotypesHelper.getUML2MetaClassByName(project, clazz.getSimpleName()) != null)
                            {
                                elements.add(StereotypesHelper.getUML2MetaClassByName(project, clazz.getSimpleName()));
                            }
                        }
                    }

                    if (!elements.isEmpty())
                    {
                        //Columns will be shown for first found element type
                        //Can add columns from all elements if wanted
                        List<String> columnList = GenericTableManager.getPossibleColumnIDs(elements.get(0));

                        //Show all available columns for element
                        //noinspection unused
                        int columnCount = columnList.size();
						List<String> fewColumns = new ArrayList<>(columnList);

                        //Add columns to table
                        GenericTableManager.addColumnsById(createdDiagram, fewColumns);
                    }
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }

                sessionManager.closeSession(project);
            }
        }
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
}
