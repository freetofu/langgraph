/*
 * Copyright (c) 2002 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.simulation.examples;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.simulation.dashboard.api.FlowAnimation;
import com.nomagic.magicdraw.simulation.dashboard.api.FlowingInformationDescriptor;
import com.nomagic.magicdraw.sysml.util.SysMLUtility;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.ConnectorView;
import com.nomagic.magicdraw.uml.symbols.shapes.ConnectorEndView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;

import javax.annotation.CheckForNull;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Plugin illustrating how external tools that are executing the model can display custom information flowing via connector.
 * Note that no model execution is happening on the Cameo Simulation Toolkit with the given values - animation is purely visual feature.
 * <br>
 * 1) Open any sample (e.g. CoffeeMachine.mdzip)
 * 2) Open any IBD diagram with connectors (e.g. Thermal View in CoffeeMachine sample)
 * 3) Right-click on any Connector in the diagram and select "Animate Flow (Example)" action from the context menu.
 *
 * @author Edgaras Dulskis
 */
public class FlowAnimationExamplePlugin extends Plugin
{
	/**
	 * Initializing the plugin during application startup. <br>
	 */
	@Override
	public void init()
	{
		ActionsConfiguratorsManager.getInstance()
				.addDiagramContextConfigurator(DiagramTypeConstants.UML_COMPOSITE_STRUCTURE_DIAGRAM, new AnimateDiagramContextAMConfigurator());
	}

	/**
	 * Adds context action "Animate Flow (Example)" in diagrams for any Connector
	 */
	private static class AnimateDiagramContextAMConfigurator implements DiagramContextAMConfigurator
	{
		@Override
		public void configure(ActionsManager manager, DiagramPresentationElement diagram,
							  PresentationElement[] selected, PresentationElement requestor)
		{
			if (requestor instanceof ConnectorEndView connectorEndView)
			{
				requestor = connectorEndView.getParent();
			}
			if (requestor instanceof ConnectorView connectorView)
			{
				Connector connector = connectorView.getElement();
				if (connector != null)
				{
					ActionsCategory category = new ActionsCategory();
					manager.addCategory(category);
					category.addAction(new AnimateConnectorFlow(connector));
				}
			}
		}

		@Override
		public int getPriority()
		{
			return AMConfigurator.LOW_PRIORITY;
		}
	}

	/**
	 * Action illustrating how to use {@link FlowAnimation} API for animating some information flowing via connectors
	 */
	private static class AnimateConnectorFlow extends DefaultDiagramAction
	{
		private static final String ACTION_NAME = "Animate Flow (Example)";
		private static final String FINISHED_NOTIFICATION_ID = "FlowAnimationFinished";

		private final Connector connector;

		public AnimateConnectorFlow(Connector connector)
		{
			super(ACTION_NAME, ACTION_NAME, null, ActionsGroups.DIAGRAM_OPENED_RELATED);
			this.connector = connector;
		}

		@Override
		public void actionPerformed(@CheckForNull ActionEvent e)
		{
			ConnectorEnd sourceEnd = selectSourceEnd(connector); // animation will start from this end
			String flowingInformation = "myFlowingInformation"; // hardcoded for example, should be some actual data compatible with end/connector/flow types
			FlowingInformationDescriptor descriptor = new FlowingInformationDescriptor(flowingInformation, connector, sourceEnd);

			long durationMillis = 4000; // 4 seconds to reach the other end
			FlowAnimation.getInstance(Project.getProject(connector)).animate(descriptor, durationMillis, AnimateConnectorFlow::showFinishedNotification);
		}

		/**
		 * FlowPort with direction 'out' will be preferred.
		 * If neither end is such port, then the first connector end (as modeled) will be the source end
		 */
		private static ConnectorEnd selectSourceEnd(Connector connector)
		{
			List<ConnectorEnd> ends = connector.getEnd();
			ConnectorEnd secondEnd = ends.get(1);
			Element element = secondEnd.getRole();
			if (element instanceof Port && "out".equals(SysMLUtility.getDirectionForPort(element)))
			{
				return secondEnd;
			}
			return ends.get(0); // otherwise, first end;
		}

		private static void showFinishedNotification(FlowingInformationDescriptor descriptor)
		{
			NotificationManager.getInstance().showNotification(new Notification(FINISHED_NOTIFICATION_ID, "Flow animation finished."));
		}
	}

	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}
}
