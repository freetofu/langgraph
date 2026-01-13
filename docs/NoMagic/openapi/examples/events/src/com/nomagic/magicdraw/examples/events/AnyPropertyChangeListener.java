/*
 * Copyright (c) 2010 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.events;

import com.nomagic.magicdraw.core.Application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Listener which shows message every time it gets change event.
 *
 * @author Justinas Bisikirskas
 */
public class AnyPropertyChangeListener implements PropertyChangeListener
{

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		Application.getInstance().getGUILog()
				.showMessage(evt.getPropertyName() + " is Changed");
	}
}
