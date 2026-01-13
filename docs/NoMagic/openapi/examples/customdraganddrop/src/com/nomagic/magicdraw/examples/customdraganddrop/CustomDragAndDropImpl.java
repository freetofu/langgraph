package com.nomagic.magicdraw.examples.customdraganddrop;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.dnd.CustomDragAndDropHandler;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.util.List;

/**
 * Custom drag and drop implementation for our custom drag and drop handler.
 * Implement methods with wanted functionality of the drag and drop.
 * <p/>
 * In this implementation we make a custom drag and drop handler to allow
 * to drop dragged named element on target named element and set name target name from dragged element.
 * And only 1 element is allowed to be dragged and dropped.
 * This implementation will be used for Browser and Diagram drag and drop.
 *
 * @author Rolandas Kasinskas
 */
public class CustomDragAndDropImpl extends CustomDragAndDropHandler
{
	@Override
	public String getDescription()
	{
		return "My custom Drag and Drop action";
	}

	@Override
	public boolean willAcceptDrop(Point location, @CheckForNull PresentationElement elementOver,
								  @CheckForNull List<Element> draggedElements,
								  DiagramPresentationElement diagram)
	{
		if (elementOver != null && draggedElements != null && draggedElements.size() == 1)
		{
			Element element = elementOver.getElement();
			Element draggedElement = draggedElements.get(0);
			return element instanceof NamedElement && draggedElement instanceof NamedElement;
		}

		return false;
	}

	@Override
	public boolean drop(Point location, @CheckForNull PresentationElement elementOver,
						@CheckForNull List<Element> draggedElements,
						DiagramPresentationElement diagram)
	{
		if (elementOver != null && draggedElements != null && draggedElements.size() == 1)
		{
			Project project = Project.getProject(elementOver);

			NamedElement element = (NamedElement) elementOver.getElement();
			NamedElement draggedElement = (NamedElement) draggedElements.get(0);
			SessionManager.getInstance().executeInsideSession(project, "Change name of element", () -> {
				//noinspection ConstantConditions
				element.setName(draggedElement.getName());
			});
			return true;
		}

		return false;
	}
}
