package com.nomagic.magicdraw.examples.symbolmove;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.PackageView;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Example plugin which demonstrates how to move symbols on the diagram through Open API.
 *
 * @author Mindaugas Genutis
 */
public class SymbolMoveExamplePlugin extends Plugin
{
    @Override
    public void init()
    {
        addSymbolContextMenu();
    }

    /**
     * Adds symbol context menu on the diagram.
     */
    private static void addSymbolContextMenu()
    {
        ActionsConfiguratorsManager.getInstance().addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM,
                new DiagramContextAMConfigurator()
                {
                    @Override
                    public void configure(ActionsManager manager, DiagramPresentationElement diagram,
										  PresentationElement[] selected, PresentationElement requestor)
                    {
                        ActionsCategory category = new ActionsCategory();
                        manager.addCategory(category);

                        if (selected != null && selected.length > 0)
                        {
                            category.addAction(new MoveSymbolsToInitialPositionAction(Arrays.asList(selected)));
                            category.addAction(new MoveSymbolsToPackageAction(Arrays.asList(selected)));
                        }
                    }

                    @Override
                    public int getPriority()
                    {
                        return AMConfigurator.MEDIUM_PRIORITY;
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
     * Action for moving symbols to initial position.
     */
    static class MoveSymbolsToInitialPositionAction extends MDAction
    {
        /**
         * Symbols to move.
         */
        private final List<PresentationElement> mViews;

        /**
         * Constructs this action.
         *
         * @param views symbols to move.
         */
        public MoveSymbolsToInitialPositionAction(List<PresentationElement> views)
        {
            super("MOVE_SYMBOL_TO_INITIAL_POSITION", "Move Symbols to Initial Position (50, 50)", null,
                    ActionsGroups.UML_PROJECT_OPENED_RELATED);

            mViews = views;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            super.actionPerformed(evt);

            if (mViews != null && !mViews.isEmpty())
            {
                Project project = Project.getProject(mViews.get(0));

                SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project,"Move symbols");

                PresentationElementsManager manager = PresentationElementsManager.getInstance();

                try
                {
                    manager.movePresentationElements(mViews, new Point(50, 50));
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }

                sessionManager.closeSession(project);
            }
        }
    }

    /**
     * Action for moving symbols to a first found package.
     */
    static class MoveSymbolsToPackageAction extends MDAction
    {
        /**
         * Symbols to move.
         */
        private final List<PresentationElement> mViews;

        /**
         * Constructs this action.
         *
         * @param views symbols to move.
         */
        public MoveSymbolsToPackageAction(List<PresentationElement> views)
        {
            super("MOVE_SYMBOLS_TO_FIRST_FOUND_PACKAGE", "Move Symbols to 1st Found Package", null,
                    ActionsGroups.UML_PROJECT_OPENED_RELATED);

            mViews = views;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            super.actionPerformed(evt);

            PackageView firstPackage = findFirstPackage();

            if (firstPackage != null)
            {
                Project project = Project.getProject(firstPackage);

                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.createSession(project,"Move symbols");

                PresentationElementsManager manager = PresentationElementsManager.getInstance();

                try
                {
                    manager.movePresentationElements(mViews, firstPackage, firstPackage.getMiddlePoint());
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }

                sessionManager.closeSession(project);
            }
        }

        /**
         * Finds first package symbol in the diagram on which symbols were selected.
         *
         * @return first found package symbol, null if it wasn't found.
         */
        @CheckForNull
        private PackageView findFirstPackage()
        {
			DiagramPresentationElement diagramView = DiagramPresentationElement.get(mViews.get(0));

            List<PresentationElement> views = diagramView.getPresentationElements();

            return (PackageView) views.stream()
                    .filter(view -> view instanceof PackageView)
                    .findFirst()
                    .orElse(null);

        }
    }
}