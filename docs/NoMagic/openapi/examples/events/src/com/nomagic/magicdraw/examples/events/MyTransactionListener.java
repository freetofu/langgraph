/*
 * Copyright (c) 2010 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.events;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.transaction.TransactionCommitListener;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

/**
 * Transaction listener, which listens if attribute is created in transaction, and if attribute owner is Classifier,
 * updates it's name with the current number of attributes. Additionally Any property change listener gets registered
 * for every created property.
 *
 * @author Justinas Bisikirskas
 */
public class MyTransactionListener implements TransactionCommitListener
{
	@Override
	public Runnable transactionCommited(final Collection<PropertyChangeEvent> events)
	{
		return () -> {
			for (PropertyChangeEvent event : events)
			{
				if (UML2MetamodelConstants.INSTANCE_CREATED.equals(event.getPropertyName()))
				{
					Object source = event.getSource();
					if (source instanceof Property property)
					{
						Element owner = property.getOwner();
						if (owner instanceof Classifier propertyOwner)
						{
							propertyOwner.setName("Contains (" + propertyOwner.getAttribute().size() + ") attributes");
						}

						// additionally for this Property we register listener to listen for any property changes in this Element properties.
						property.addPropertyChangeListener(new DerivedValuePropertyChangeListener());
					}
				}
			}
		};
	}
}