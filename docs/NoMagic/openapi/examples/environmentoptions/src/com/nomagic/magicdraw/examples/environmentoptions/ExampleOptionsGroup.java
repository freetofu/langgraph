package com.nomagic.magicdraw.examples.environmentoptions;

import com.nomagic.annotation.Used;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.examples.environmentoptions.resources.EnvironmentOptionsResources;
import com.nomagic.magicdraw.icons.IconsFactory;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.ChoiceProperty;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Options group for the example.
 *
 * @author Mindaugas Genutis
 */
public class ExampleOptionsGroup extends AbstractPropertyOptionsGroup
{
	/**
	 * ID of the example options group.
	 */
	public static final String ID = "options.example";

	/**
	 * ID of property group 1.
	 */
	public static final String GROUP1 = "GROUP1";

	/**
	 * ID of property group 2.
	 */
	public static final String GROUP2 = "GROUP2";

	/**
	 * ID of "My Choice" property.
	 */
	public static final String MY_CHOICE_PROPERTY_ID = "MY_CHOICE_PROPERTY_ID";

    private static final Icon ICON = IconsFactory.getIcon("nonenode.png");

    /**
	 * Choice 1 value.
	 */
	private static final String CHOICE_1 = "Choice 1";

	/**
	 * Choice 2 value.
	 */
	private static final String CHOICE_2 = "Choice 2";

	/**
	 * Choice 3 value.
	 */
	private static final String CHOICE_3 = "Choice 3";

	/**
	 * Possible choice property values.
	 */
	public static final List MY_CHOICE_VALUES = Arrays.asList(CHOICE_1, CHOICE_2, CHOICE_3);

	/**
	 * ID of "My Boolean" property.
	 */
	public static final String MY_BOOLEAN_PROPERTY_ID = "MY_BOOLEAN_PROPERTY_ID";

	/**
	 * Resource name of example options.
	 */
	private static final String EXAMPLE_OPTIONS_NAME = "EXAMPLE_OPTIONS_NAME";

	/**
	 * Provides resources to the properties.
	 */
	@SuppressWarnings("ConstantConditions")
	public static final PropertyResourceProvider PROPERTY_RESOURCE_PROVIDER = (key, property) -> EnvironmentOptionsResources.getString(key);

	/**
	 * Constructs this options group.
	 */
	public ExampleOptionsGroup()
	{
		super(ID);
	}

	@Override
	public void setDefaultValues()
	{
		setMyChoiceProperty(CHOICE_2);
		setMyBoolean(true);
	}

	/**
	 * Sets my choice property value.
	 *
	 * @param value value to set.
	 */
	public void setMyChoiceProperty(String value)
	{
		ChoiceProperty property = new ChoiceProperty(MY_CHOICE_PROPERTY_ID, value, MY_CHOICE_VALUES);
		property.setValuesTranslatable(false);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
		property.setGroup(GROUP1);

		addProperty(property, true);
	}

	/**
	 * Gets my choice property value.
	 *
	 * @return my choice property value.
	 */
	@Used
	public String getMyChoicePropertyValue()
	{
		Property p = getProperty(MY_CHOICE_PROPERTY_ID);
		return p != null ? (String) p.getValue() : CHOICE_2;
	}

	/**
	 * Gets my boolean property value.
	 *
	 * @return my boolean property value.
	 */
	@Used
	public boolean isMyBoolean()
    {
        Property p = getProperty(MY_BOOLEAN_PROPERTY_ID);
        return p != null && (Boolean) p.getValue();
    }

	/**
	 * Sets my boolean property value.
	 *
	 * @param value my boolean property value.
	 */
    public void setMyBoolean(boolean value)
    {
        BooleanProperty property = new BooleanProperty(MY_BOOLEAN_PROPERTY_ID, value);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(GROUP2);

        addProperty(property);
    }

	@Override
	public String getName()
	{
		return EnvironmentOptionsResources.getString(EXAMPLE_OPTIONS_NAME);
	}

	@Override
	public javax.swing.Icon getGroupIcon()
	{
		return ICON;
	}
}