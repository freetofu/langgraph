/*
 * Copyright (c) 2002 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.simulation.examples;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.simulation.dashboard.api.ExternalValueProviders;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;

/**
 * Plugin illustrating how external tools that are executing the model can display those custom values in diagrams on specific parts or ports.
 * Note that no model execution is happening on the Cameo Simulation Toolkit with the given values - they are purely for display purposes.
 * <br>
 * Using This plugin:
 * 1) Open sample project /simulation/Transmission.mdzip
 * 2) Open ibd PowerTrain
 * 3) See ECU.carSpeed and ECU.wheelDiameter values in the diagram
 *
 * @author Edgaras Dulskis
 */
public class ExternalValueInDiagramsPlugin extends Plugin
{
	/**
	 * Initializing the plugin during application startup.
	 * <br>
	 * Illustrates how to register a custom value provider. In this case, registering happens on project load, but providers
	 * can be registered and unregistered at any time while the project is open.
	 */
	@Override
	public void init()
	{
		Application.getInstance().addProjectEventListener(new ProjectEventListenerAdapter()
		{
			@Override
			public void projectOpened(Project project)
			{
				// The root context of the external execution. Values will only be visible in the Diagrams that have this element set as context
				Classifier context = Finder.byQualifiedName().find(project, "Transmission::Structure::PowerTrain", Class.class);
				if (context != null)
				{
					// Engine Control Unit refers to ECU block in the Transmission sample project
					EngineControlUnitValueProvider valueProvider = new EngineControlUnitValueProvider(context);
					ExternalValueProviders.getInstance(project).register(valueProvider);

					// if later the provider needs to be unregistered, it should be done by calling:
					// ExternalValueProviders.getInstance(project).unregister(valueProvider);
					// Since providers are registered for specific open project, there is no obligation to unregister manually on project close.
				}
			}
		});

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
