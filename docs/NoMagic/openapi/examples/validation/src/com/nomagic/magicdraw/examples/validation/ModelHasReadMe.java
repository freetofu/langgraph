/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.validation;

import com.nomagic.annotation.Used;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;

/**
 * Validation rule that checks whether UML model object has a content diagram with the name READ ME.
 *
 * @author Rimvydas Vaidelis
 * @version 1.0
 */
public class ModelHasReadMe
{
	public static final String CONTENT_DIAGRAM = "Content Diagram";
	public static final String READ_ME = "READ ME";

	/**
	 * Validates a model.
	 *
	 * @param model a model object.
	 * @return true if the model object is valid.
	 */
	@Used
	public static boolean isValid(Model model)
	{
		//noinspection ConstantConditions
		return model.getOwnedDiagram().stream()
				.anyMatch(diagram -> CONTENT_DIAGRAM.equals(diagram.get_representation().getType()) &&
									 READ_ME.equals(diagram.getName()));
	}
}
