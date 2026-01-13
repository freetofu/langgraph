package com.nomagic.magicdraw.variants.examples;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.properties.StringProperty;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.variants.ui.modeltransformation.VariantsModelTransformationWizardConfigurator;
import com.nomagic.magicdraw.variants.ui.modeltransformation.VariantsModelTransformationWizardRegistry;
import com.nomagic.magicdraw.variants.variationpoints.VariationPointsProvider;

/**
 * An example of plugin, to transform model with defined rules.
 *
 * @author Tomas Lukosius
 */
public class ExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
		BrowserContextAMConfigurator configurator = new BrowserContextAMConfigurator()
		{
			@Override
			public void configure(ActionsManager actionsManager, Tree tree)
			{
				MDActionsCategory category = new MDActionsCategory();
				category.addAction(new ExampleAction());
				actionsManager.addCategory(0, category);
			}

			@Override
			public int getPriority()
			{
				return AMConfigurator.MEDIUM_PRIORITY;
			}
		};
		ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(configurator);

		final VariationPointsProvider variationPointsProvider = new ExampleVariationPointsProvider();
		VariantsModelTransformationWizardRegistry.getInstance().register(new VariantsModelTransformationWizardConfigurator()
		{
			@Override
			public VariationPointsProvider getVariationPointsProvider()
			{
				return variationPointsProvider;
			}

			@Override
			public PropertyManager getVisibleProperties(Project project)
			{
				StringProperty stringProperty = new StringProperty("sample visible property", "");
				stringProperty.setGroup(getVariationPointsProvider().getName() + " properties");
				PropertyManager visibleProperties = new PropertyManager();
				visibleProperties.addProperty(stringProperty);
				return visibleProperties;
			}

			@Override
			public PropertyManager getInvisibleProperties(Project project)
			{
				StringProperty stringProperty = new StringProperty("sample invisible property", "");
				PropertyManager invisibleProperties = new PropertyManager();
				invisibleProperties.addProperty(stringProperty);
				return invisibleProperties;
			}

			@Override
			public boolean isVariationPointsProviderVisible(Project project)
			{
				return true;
			}
		});
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}
