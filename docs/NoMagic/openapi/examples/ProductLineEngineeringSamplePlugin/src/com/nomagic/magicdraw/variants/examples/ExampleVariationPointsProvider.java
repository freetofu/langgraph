/*
  @(#) ExampleVariationPointsProvider.java  2015/11/23
 * Copyright (c) 2015 No Magic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.variants.examples;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml2.Classifiers;
import com.nomagic.magicdraw.variants.PropertyNames;
import com.nomagic.magicdraw.variants.evaluation.BooleanEvaluator;
import com.nomagic.magicdraw.variants.evaluation.Evaluator;
import com.nomagic.magicdraw.variants.variationpoints.VariationPoint;
import com.nomagic.magicdraw.variants.variationpoints.VariationPointsFactory;
import com.nomagic.magicdraw.variants.variationpoints.VariationPointsProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.annotation.CheckForNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * An example variation points provider implementation.
 * It returns all elements in a given scope as variable elements.
 * variation points are created only for specific named elements for sake of demonstration.
 *
 * @author Tomas Lukosius
 */
public class ExampleVariationPointsProvider implements VariationPointsProvider
{
	private final VariationPointsFactory factory = new VariationPointsFactory();

	@Override
	public String getName()
	{
		return "Example Variation Points Provider";
	}

	@Override
	public void setUp(Project project, @CheckForNull PropertyManager propertyManager)
	{
	 /*
		 called just before the start of the transformation. This method is called automatically, when transforming from MagicDraw Model Transformation dialog.
		 if transformation is triggered not from MagicDraw Model Transformation dialog, this method should be called before transformation.
		 Initialize resources that are required for getVariabilityElements() and getVariationPoints() methods
	 */
	}

	@Override
	public void tearDown(Collection<VariationPoint> failed)
	{
		// automatically called after transformation is complete. Resources should be released here
	}

	/**
	 * Collect elements in scope, that will be transformed
	 *
	 * @param scope where to collect variable elements
	 * @return collection of variable elements
	 */
	@Override
	public Collection<Element> getVariabilityElements(Collection<Element> scope)
	{
		// define, what elements will be transformed in a given scope

		// it's recommended to return only those elements, that have to be transformed.
		// What is more, single element should be included once.
		// Then transformation will execute faster.
		Collection<Element> variabilityElements = new HashSet<>();
		for (Element scopeElement : scope)
		{
			Collection<? extends Element> elements = Finder.byTypeRecursively().find(scopeElement, null);
			variabilityElements.addAll(elements);
		}
		return variabilityElements;
	}

	/**
	 * Collect and return all variation points for element
	 *
	 * @param element element, for which variation points are collected
	 * @return collection of variation points
	 */
	@Override
	public Collection<VariationPoint> getVariationPoints(Element element)
	{
		// here we create variation points for given element
		if (element instanceof NamedElement)
		{
			final String name = ((NamedElement) element).getName();
			// com.nomagic.magicdraw.variants.PropertyNames contains constants of properties, supported in transformations
			switch (name)
			{
				case "existence":
					return Collections.singletonList(
							factory.createExistenceVariationPoint(element, createExistenceChangeEvaluator()));
				case "name":
					return Collections.singletonList(
							factory.createPropertyVariationPoint(element, PropertyNames.NAME, createNameChangeEvaluator()));
				case "default value":
					return Collections.singletonList(
							factory.createDefaultValueVariationPoint(element, createDefaultValueChangeEvaluator()));
				case "multiplicity":
					return Collections.singletonList(
							factory.createMultiplicityVariationPoint(element, createMultiplicityChangeEvaluator()));
				case "changeType":
					return Collections.singletonList(
							factory.createTypeVariationPoint(element, createTypeChangeEvaluator()));
				case "removeFlow":
					return Collections.singletonList(
							factory.createActivityBranchExistenceVariationPoint(element, createExistenceChangeEvaluator()));
				case "changeToSubtype":
					return Collections.singletonList(
							factory.createTypeVariationPoint(element, createChangeTypeToFirstSubtypeEvaluator()));
				case "stereotypedClass":
					return Arrays.asList(
							factory.createTagVariationPoint(element, getAppliedStereotype(element), "someElementValue",
															createElementValueChangeEvaluator()),
							factory.createTagVariationPoint(element, getAppliedStereotype(element), "someCount",
															createIntegerIncrementEvaluator())
					);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Return first stereotype applied to given element. Element may have multiple stereotypes, but we applied only one to keep example
	 * as simple as possible.
	 *
	 * @param element given element with one stereotype applied
	 * @return first applied stereotype
	 */
	private static Stereotype getAppliedStereotype(Element element)
	{
		return element.getAppliedStereotype().get(0);
	}

	/*
		below are some examples of an evaluator creation.
		An evaluator returns value, which will be applied to given element during transformation.
		An evaluator receives a current value parameter, which can be used to calculate a value, that is applied during transformation.
		com.nomagic.magicdraw.variants.variationpoints.VariationPointsFactory has methods to create all kinds of currently supported variation points.
	 */
	private static BooleanEvaluator createExistenceChangeEvaluator()
	{
		return new BooleanEvaluator()
		{
			@Override
			public Boolean evaluate(Element element)
			{
				return false;
			}
		};
	}

	private static Evaluator<Object> createNameChangeEvaluator()
	{
		return (element, currentValue) -> currentValue == null ? "" : currentValue.toString() + " changed";
	}

	private static Evaluator<Object> createDefaultValueChangeEvaluator()
	{
		return (element, currentValue) -> 10;
	}

	private static Evaluator<String> createMultiplicityChangeEvaluator()
	{
		return (element, currentValue) -> "2";
	}

	private static Evaluator<Type> createTypeChangeEvaluator()
	{
		return (element, currentValue) -> (Type) Finder.byNameRecursively().find(Project.getProject(element).getPrimaryModel(), Class.class, "TypeB", true);
	}

	private static Evaluator<Type> createChangeTypeToFirstSubtypeEvaluator()
	{
		return (element, currentValue) -> {
			if (currentValue instanceof Classifier)
			{
				Collection<Classifier> derivedClassifiers = Classifiers.getDirectDerivedClassifiers((Classifier) currentValue);
				if (!derivedClassifiers.isEmpty())
				{
					return derivedClassifiers.iterator().next();
				}
			}
			return (Type) currentValue;
		};
	}

	private static Evaluator<Object> createElementValueChangeEvaluator()
	{
		return (element, currentValue) -> Finder.byNameRecursively().find(Project.getProject(element).getPrimaryModel(), Class.class, "TypeB", true);
	}

	private static Evaluator<Object> createIntegerIncrementEvaluator()
	{
		return (element, currentValue) -> {
			if (currentValue instanceof Integer)
			{
				return (Integer) currentValue + 1;
			}
			return 0;
		};
	}
}
