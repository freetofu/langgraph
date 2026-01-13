package com.nomagic.magicdraw.simulation.examples;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.simulation.dashboard.api.ExternalValueProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.finder.FinderByQualifiedName;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Illustrates how to display custom value text for selected elements in the given context (PowerTrain in this case)
 *
 * @author Edgaras Dulskis
 */
public class EngineControlUnitValueProvider extends ExternalValueProvider
{
	private final Element carSpeedElement;
	private final Element wheelDiameterElement;
	/**
	 * Mimics some model execution process that happens outside this tool and produces values to display
	 */
	private final MockExternalExecution externalExecution;

	/**
	 * {@inheritDoc}
	 */
	public EngineControlUnitValueProvider(Element context)
	{
		super(context);
		Project project = Project.getProject(context);
		carSpeedElement = FinderByQualifiedName.getInstance().find(project, "Transmission::Structure::ECU::carSpeed", Property.class);
		wheelDiameterElement = FinderByQualifiedName.getInstance().find(project, "Transmission::Structure::ECU::wheelDiameter", Property.class);
		externalExecution = new MockExternalExecution(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public String getValueText(Element element, List<? extends Element> pathToContext)
	{
		// values should be pre-collected for diagram related performance reasons. Avoid time demanding implementations (e.g. remote calls) of this method
		if (element == carSpeedElement)
		{
			return externalExecution.getCarSpeed() + " mi/h";
		}
		else if (element == wheelDiameterElement)
		{
			return externalExecution.getWheelDiameter() + "\""; // double quote to represent inches
		}
		return null; // do not provide custom value for other elements in this context
	}

	/**
	 * To be called every time speed changes to call {@link #getValueText(Element, List)} for that element again
	 * @see #invalidate(Element)
	 * @see #invalidateAll()
	 */
	public void invalidateCarSpeed()
	{
		invalidate(carSpeedElement);
	}
}
