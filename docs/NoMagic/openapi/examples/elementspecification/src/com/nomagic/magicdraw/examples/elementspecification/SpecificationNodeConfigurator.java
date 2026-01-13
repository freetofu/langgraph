/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.elementspecification;

import com.nomagic.magicdraw.ui.dialogs.specifications.ISpecificationComponent;
import com.nomagic.magicdraw.ui.dialogs.specifications.configurator.ISpecificationNodeConfigurator;
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.ConfigurableNodeFactory;
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.IConfigurableNode;
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.ISpecificationNode;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Martynas Lelevicius
 */
class SpecificationNodeConfigurator implements ISpecificationNodeConfigurator
{
	@Override
	public void configure(IConfigurableNode node, Element element)
	{
		final IConfigurableNode myNode = ConfigurableNodeFactory.createConfigurableNode(new MyNode());
		node.insertNode(IConfigurableNode.DOCUMENTATION_HYPERLINKS, IConfigurableNode.Position.BEFORE, myNode);
		myNode.addNode(ConfigurableNodeFactory.createConfigurableNode(new MyInnerSpecificationNode()));
	}

	private static class MyNode implements ISpecificationNode
	{
		@Override
		public String getID()
		{
			return "MY_NODE";
		}

		@CheckForNull
		@Override
		public Icon getIcon()
		{
			return null;
		}

		@Override
		public String getText()
		{
			return "My Node";
		}

		@Override
		public void dispose()
		{
		}

		@Override
		public ISpecificationComponent createSpecificationComponent(Element element)
		{
			return new MySpecificationComponent();
		}

		@Override
		public void propertyChanged(Element element, PropertyChangeEvent event)
		{
		}

		@Override
		public boolean updateNode()
		{
			return false;
		}
	}

	private static class MySpecificationComponent implements ISpecificationComponent
	{
		@Override
		public JComponent getComponent()
		{
			return new JLabel("My Specification Component");
		}

		@Override
		public void propertyChanged(Element element, PropertyChangeEvent event)
		{
		}

		@Override
		public void updateComponent()
		{
		}

		@Override
		public void dispose()
		{
		}
	}

	private static class MyInnerSpecificationNode implements ISpecificationNode
	{
		@Override
		public String getID()
		{
			return "MY_INNER_NODE";
		}

		@CheckForNull
		@Override
		public Icon getIcon()
		{
			return null;
		}

		@Override
		public String getText()
		{
			return "My Inner Node";
		}

		@Override
		public void dispose()
		{
		}

		@Override
		public ISpecificationComponent createSpecificationComponent(Element element)
		{
			return new MyInnerSpecificationComponent();
		}

		@Override
		public void propertyChanged(Element element, PropertyChangeEvent event)
		{
		}

		@Override
		public boolean updateNode()
		{
			return false;
		}
	}

	private static class MyInnerSpecificationComponent implements ISpecificationComponent
	{
		@Override
		public JComponent getComponent()
		{
			return new JLabel("My Inner Specification Component");
		}

		@Override
		public void propertyChanged(Element element, PropertyChangeEvent event)
		{
		}

		@Override
		public void updateComponent()
		{
		}

		@Override
		public void dispose()
		{
		}
	}

}
