package com.nomagic.magicdraw.examples.legenditem;

import com.nomagic.annotation.Used;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.SymbolElementMap;
import com.nomagic.magicdraw.validation.OpenedDiagramValidator;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfigProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.Component;

import javax.annotation.CheckForNull;
import java.util.*;

/**
 * This is example implementation which displays how specific presentation elements (views) of the same
 * element can be adorned in one diagram, but not adorned in another depending on some condition, even though the same Legend is active
 * in both of mentioned diagrams.
 *
 * To see the effect of this implementation please open the project "Legend Item Implementation Sample.mdzip"
 * The full name of this class is used as "Implementation" property of the legend item "Components".
 *
 * This specific implementation annotates view of a Component in the diagram only if that Component is included in the diagram's tagged value
 * "elementsOfInterest"
 * @author Edgaras Dulskis
 */
@Used
public class AdornComponentsOfInterestInDiagramValidator extends OpenedDiagramValidator implements SmartListenerConfigProvider
{
	/**
	 *  The stereotype exists inside the sample project "Legend Item Implementation Sample.mdzip"
	 */
	private static final String STEREOTYPE_NAME = "customDiagram";
	private static final String TAG_NAME = "elementsOfInterest";

	@Override
	public Set<Annotation> getAnnotations(Project project, Constraint constraint)
	{
		Set<Annotation> viewAnnotations = new HashSet<>();
		for (DiagramPresentationElement diagram : getDiagrams()) // iterate diagrams in scope only
		{
			SymbolElementMap symbolElementMap = project.getSymbolElementMap();
			for (Component component : getComponentsOfInterest(diagram.getDiagram())) // check which components should be adorned in this diagram
			{
				for (PresentationElement view : symbolElementMap.getAllPresentationElements(component, diagram))
				{
					viewAnnotations.add(new Annotation(view, constraint)); // create annotation object which will cause the component view to be adorned
				}
			}
		}
		return viewAnnotations;
	}

	/**
	 * Retrieves components from a tagged value. The stereotype is applied on Diagram element
	 */
	private static Collection<Component> getComponentsOfInterest(Diagram diagram)
	{
		Collection<Component> elementsOfInterest = new ArrayList<>();
		for (Object value : StereotypesHelper.getStereotypePropertyValue(diagram, STEREOTYPE_NAME, TAG_NAME))
		{
			if (value instanceof Component)
			{
				elementsOfInterest.add((Component)value);
			}
		}
		return elementsOfInterest;
	}

	@CheckForNull
	@Override
	public Map<Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
	{
		// returned configuration should be different for each implementation, based on which model changes actually affects the results of this validator.
		// In this case component may have be removed or added as element of interest (tag value), so need to recalculate annotations when tag value of a diagram changes
		// returning null means to re-calculate this rule on every model change in the project. This might have negative impact for the tool performance
		return Collections.singletonMap(Diagram.class, Collections.singleton(SmartListenerConfig.TAGGED_VALUE_SHALLOW_CONFIG));
	}
}
