package com.nomagic.magicdraw.examples.extractrefactor;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.magicdraw.uml.refactor.extract.ExtractManager;
import com.nomagic.magicdraw.uml.refactor.extract.ExtractReference;
import com.nomagic.magicdraw.uml.refactor.extract.ExtractSource;
import com.nomagic.magicdraw.uml.refactor.extract.ExtractTarget;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Example plugin which demonstrates usage of Extract Refactor Open API.
 *
 * @author Mindaugas Genutis
 */
public class ExtractRefactorExamplePlugin extends Plugin
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
                        category.addAction(new ExtractRefactorSymbolsAction(selected));
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
     * Extracts symbols by refactoring.
     *
     * @param symbols symbols to extract.
     */
    private static void extractSymbols(PresentationElement[] symbols)
    {
        // Creates extract refactor manager.
        ExtractManager extractManager = Refactoring.Extracting.createExtractManager(symbols);

        if (extractManager != null)
        {
            Project project = Project.getProject(symbols[0]);

            // A session has to be started before refactoring.
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.createSession(project,"Extract Refactor Symbols");

            // We may control the extract refactor result by modifying extract target.
            ExtractTarget extractTarget = extractManager.getExtractTarget();

            // Create a namespace to which we are going to refactor.
            Package package1 = project.getElementsFactory().createPackageInstance();
            package1.setOwner(project.getPrimaryModel());

            // Set the namespace to which the extract result will go.
            extractTarget.setTargetNamespace(package1);

            // Choose target diagram type from allowed diagram types if the default type does not suite.
            List<String> allowedTargetDiagramTypes = extractTarget.getAllowedTargetDiagramTypes();
            extractTarget.setTargetDiagramType(allowedTargetDiagramTypes.get(0));

            // Modify reference names which link the extract refactor source to the target.
            List<? extends ExtractReference> references = extractTarget.getReferences();

            for (int i = 0; i < references.size(); i++)
            {
                ExtractReference reference = references.get(i);
                reference.setName(Integer.toString(i));
            }

            // We may control the extract refactor source by modifying the extract source.
            ExtractSource extractSource = extractManager.getExtractSource();
            extractSource.setElementName("sourceElementName");

            // Perform actual refactoring.
            extractManager.extract();

            sessionManager.closeSession(project);

            // The element which was created in the source during refactoring.
            //noinspection unused
            Element sourceElement = extractSource.getElement();

            // The element which was created in the target during refactoring.
            //noinspection unused
            Element targetElement = extractTarget.getElement();

            // The diagram which was created in the target during refactoring.
            //noinspection unused
            DiagramPresentationElement targetDiagram = extractTarget.getDiagram();
        }
    }

    /**
     * Action which performs extract refactoring on selected symbols.
     */
    static class ExtractRefactorSymbolsAction extends DefaultDiagramAction
    {
        /**
         * Symbols to extract.
         */
        private final PresentationElement[] mSelected;

        /**
         * Constructs this action.
         *
         * @param selectedViews symbols to extract.
         */
        public ExtractRefactorSymbolsAction(PresentationElement[] selectedViews)
        {
            super("EXTRACT_REFACTOR_SYMBOLS", "Extract Refactor Symbols", null, null);

            mSelected = selectedViews;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            if (mSelected != null && mSelected.length > 0)
            {
                extractSymbols(mSelected);
            }
        }
    }
}