package com.nomagic.magicdraw.examples.testframework;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.CompositeLayouterOptionsGroup;
import com.nomagic.magicdraw.core.options.OrientationOptions;
import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.nomagic.magicdraw.tests.common.TestEnvironment;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.AbstractDiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.composite.CompositeStructureDiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.layout.Layouting;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Test example using JUnit3 test framework
 *
 * @author Mindaugas Genutis
 * @goal Tests Composite Structure diagram layout on part symbols in the diagram.
 */
public class DiagramLayoutTest extends MagicDrawTestCase
{
	/**
	 * File name of the test file
	 */
	private static final String TEST_FILE = "diagram_layout_test/LayoutTest.mdzip";

	/**
	 * Project on which test is performed.
	 */
	private Project project;

	/**
	 * Constructs this test.
	 *
	 * @param name name of the test.
	 */
	public DiagramLayoutTest(String name)
	{
		super(name);
	}

	/**
	 * @goal Demonstrates a possibility to layout the whole diagram, with different options
	 */
	public void testCompositeStructureLayout()
	{
		AbstractDiagramPresentationElement diagramView = getDiagram("B");
		assertNotNull(diagramView);
		diagramView.open();

		CompositeLayouterOptionsGroup optionsGroup = (CompositeLayouterOptionsGroup) Application.getInstance().getEnvironmentOptions()
				.getGroup(CompositeLayouterOptionsGroup.ID);
		String orientationBefore = optionsGroup.getOrientation();
		//set new orientation
		optionsGroup.setOrientation(OrientationOptions.TOP_TO_BOTTOM);

		Layouting.layout(diagramView, Layouting.COMPOSITE_DIAGRAM_LAYOUTER, optionsGroup);
		//set orientation back
		optionsGroup.setOrientation(orientationBefore);

		Rectangle partDBounds = getPartSymbol(diagramView, "d").getBounds();
		Rectangle partEBounds = getPartSymbol(diagramView, "e").getBounds();
		Rectangle partFBounds = getPartSymbol(diagramView, "f").getBounds();

		assertTrue("Part 'd' should be on top",
				   partDBounds.y < partEBounds.y && partDBounds.y < partFBounds.y);
	}

	/**
	 * @goal Demonstrates a possibility to layout selected symbols
	 */
	public void testCompositeStructureSelectedSymbolLayout()
	{
		AbstractDiagramPresentationElement diagramView = getDiagram("A");
		assertNotNull(diagramView);
		diagramView.open();

		PartView part1 = getPartSymbol(diagramView, "c1");
		PartView part2 = getPartSymbol(diagramView, "c2");
		PartView part3 = getPartSymbol(diagramView, "c3");
		layout(diagramView, Arrays.asList(part1, part2, part3));

		Rectangle part1Bounds = part1.getBounds();
		Rectangle part2Bounds = part2.getBounds();
		Rectangle part3Bounds = part3.getBounds();
		assertTrue("Parts should be in the same vertical position after layout",
				   part1Bounds.y == part2Bounds.y && part1Bounds.y == part3Bounds.y);
		assertTrue("Part c1 should be to the left of part c3", part1Bounds.x < part3Bounds.x);
		assertTrue("Part c3 should be to the left of part c2", part3Bounds.x < part2Bounds.x);
	}

	/**
	 * Layouts parts on the diagram.
	 *
	 * @param diagramView   diagram to layout.
	 * @param partsToLayout parts symbols to layout.
	 */
	private static void layout(AbstractDiagramPresentationElement diagramView, List<PresentationElement> partsToLayout)
	{
		diagramView.setSelected(partsToLayout);
		Layouting.layout(diagramView, Layouting.COMPOSITE_DIAGRAM_LAYOUTER);
	}

	/**
	 * Given a diagram, retrieves part symbol in that diagram by part name.
	 *
	 * @param diagramView  diagram to find symbol in
	 * @param propertyName name of part to extract.
	 * @return part symbol if found, null otherwise.
	 */
	private PartView getPartSymbol(AbstractDiagramPresentationElement diagramView, String propertyName)
	{
		Element property = Finder.byNameRecursively().find(project, Property.class, propertyName);
		assertTrue("Property with name " + propertyName + " was not found", property instanceof Property);

		List<PresentationElement> symbols = project.getSymbolElementMap().getAllPresentationElements(property, diagramView);
		assertEquals("There is more than one symbol of " + propertyName + " in diagram" + diagramView.getName(), 1, symbols.size());
		PresentationElement symbol = symbols.iterator().next();
		assertTrue(symbol instanceof PartView);
		return (PartView) symbol;
	}

	/**
	 * Finds a diagram by name in a given project.
	 *
	 * @param name name of the diagram to find.
	 * @return diagram if found, null otherwise.
	 */
	@CheckForNull
	private AbstractDiagramPresentationElement getDiagram(String name)
	{
		return project.getDiagrams().stream()
				.filter(diagramView -> name.equals(diagramView.getName()))
				.findFirst()
				.orElse(null);
	}

	@Override
	protected void setUpTest() throws Exception
	{
		super.setUpTest();

		File file = new File(TestEnvironment.getResourceDir(), TEST_FILE);
		project = openProject(file.getAbsolutePath());
	}

	@Override
	protected void tearDownTest() throws Exception
	{
		closeProject(project);
		// clear reference to the project so that memory can be released (important when running many test classes)
		//noinspection ConstantConditions
		project = null;

		super.tearDownTest();
	}
}