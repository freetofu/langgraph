package com.nomagic.magicdraw.examples.customdraganddrop;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dnd.*;

/**
 * Drag and Drop plugin, to show custom Drag and Drop capabilities.
 * Plugin registers custom handlers for Drag and Drop actions.
 *
 * @author Rolandas Kasinskas
 * @author Domas Petrulis
 */
public class CustomDragAndDropPlugin extends Plugin
{
	@Override
	public void init()
	{
		//Create an object which has CustomDragAndDropHandler implementation for the factory
		CustomDragAndDropHandlerFactory handlerFactory = CustomDragAndDropImpl::new;
		//Registers custom Drag and Drop handler only to diagram.
		CustomDropDiagramHandlerFactory.register(handlerFactory);
		//Registers custom Drag and Drop handler only for diagram shapes.
		CustomShapeMoveHandlerFactory.register(handlerFactory);

		//Registers DnD handler for browser
		BrowserTabTreeDragAndDropHandlerRegistry.register(BrowserTextDropHandler::new);
		//Registers DnD handler for diagram
		DiagramTransferableDragAndDropHandlerRegistry.register(DiagramTextDropHandler::new);
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
