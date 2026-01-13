package com.nomagic.magicdraw.variants.examples;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.variants.transformation.VariantRealizationTransformation;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import java.awt.event.ActionEvent;
import java.util.Collections;

/**
 * An example browser action implementation to demonstrate element transformations
 *
 * @author Tomas Lukosius
 */
public class ExampleAction extends DefaultBrowserAction
{
    public ExampleAction()
    {
        super("ExampleBrowserAction", "Example browser action", null, null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e)
    {
        Tree tree = getTree();
        if (tree != null)
        {
            Node selectedNode = tree.getSelectedNode();
            if (selectedNode != null)
            {
                Element scopeElement = (Element) selectedNode.getUserObject();
                ExampleVariationPointsProvider provider = new ExampleVariationPointsProvider();

                // you can provide custom properties to the setUp method to initialize the provider properly. Null can also be provided
                PropertyManager manager = new PropertyManager();
                manager.addProperty(new BooleanProperty("CalledFromExample", true));
                Project project = Project.getProject(scopeElement);
                if(project != null)
                {
                    provider.setUp(project, manager);

                    // here we perform transformation in given scope with our created variation points provider
                    VariantRealizationTransformation.transform(provider, Collections.singletonList(scopeElement));
                }
            }
        }
    }
}
