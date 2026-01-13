/*
 * Copyright (c) 2010 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.events;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.impl.PropertyNames;
import com.nomagic.utils.Utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Property change listener which shows message, if "Is Derived" property is changed into true.
 *
 * @author Justinas Bisikirskas
 */
public class DerivedValuePropertyChangeListener implements PropertyChangeListener
{
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (PropertyNames.IS_DERIVED.equals(evt.getPropertyName()) &&
			Utilities.isEqual(evt.getNewValue(), Boolean.TRUE))
		{
			Application.getInstance().getGUILog().showMessage("Attribute is derived");
		}
	}
}
