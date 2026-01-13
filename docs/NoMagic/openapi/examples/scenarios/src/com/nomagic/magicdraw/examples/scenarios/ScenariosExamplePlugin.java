package com.nomagic.magicdraw.examples.scenarios;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.usecasescenarios.scenarios.*;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

import java.awt.event.ActionEvent;

/**
 * Example plugin which demonstrates usage of Scenarios Open API.
 *
 * @author Mindaugas Genutis
 */
public class ScenariosExamplePlugin extends Plugin
{
    @Override
    public void init()
    {
        addBrowserContextMenuAction();
    }

    /**
     * Adds context menu to elements in the browser.
     */
    private static void addBrowserContextMenuAction()
    {
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
        {
            @Override
            public void configure(ActionsManager manager, Tree browser)
            {
                MDActionsCategory category = new MDActionsCategory();

                category.addAction(new CreateScenarioAction());

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
     * Action for creating a scenario.
     */
    static class CreateScenarioAction extends DefaultBrowserAction
    {
        /**
         * Constructs this action.
         */
        public CreateScenarioAction()
        {
            super("CREATE_SCENARIO", "Create scenario from plugin", null, null);
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            BaseElement baseElement = getFirstElement();

            if (baseElement instanceof UseCase useCase)
            {

				Project project = Project.getProject(baseElement);

                // Scenario manipulation should be wrapped with the session management calls.
                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.createSession(project,"Create scenario");

                // Creates a use case scenario.
                Scenario scenario = ScenarioManager.createScenario(useCase);
                // Sets the scenario name.
                scenario.setName("Extract money from ATM.");

                // Adds a basic flow step.
                FlowStep flowStep1 = scenario.addFlowStep();
                // Sets a name for the basic flow step.
                flowStep1.setName("Insert card");

                FlowStep flowStep2 = scenario.addFlowStep();
                flowStep2.setName("Enter pin");

                FlowStep flowStep3 = scenario.addFlowStep();
                flowStep3.setName("Good bye");

                // Adds an alternative condition for the basic flow step.
                AlternativeCondition condition = scenario.addAlternativeCondition(flowStep2);
                // Sets a condition guard.
                condition.setIfCondition("Pin correct");

                // Sets a name for the alternative flow step.
                FlowStep flowStep = condition.getAlternativeFlowSteps().get(0);
                flowStep.setName("Extract money");
                // Adds an exception type to the basic flow step.
                ExceptionType exceptionType = scenario.addExceptionType(flowStep2);

                // Sets a name for the exception type.
                exceptionType.setName("Card expired");
                // Sets a name for the exceptional flow step.
                FlowStep exceptionalFlowStep = exceptionType.getExceptionalFlowSteps().get(0);
                exceptionalFlowStep.setName("Inform customer about expired card");
                sessionManager.closeSession(project);

                // Open and layout the scenario diagram.
                ScenarioManager.displayScenario(scenario, true, true, "Open ATM Scenario");
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