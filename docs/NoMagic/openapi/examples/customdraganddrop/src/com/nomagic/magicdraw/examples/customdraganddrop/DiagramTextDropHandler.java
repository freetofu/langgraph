/*
 * Copyright (c) 2021 NoMagic, Inc. All Rights Reserved
 */
package com.nomagic.magicdraw.examples.customdraganddrop;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.dnd.DiagramTransferableDragAndDropHandler;
import com.nomagic.magicdraw.ui.dnd.DropTarget;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import org.apache.logging.log4j.LogManager;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Class handles drag and drop of text which is dragged to diagram panel from another application
 * <p>
 * If plain text comes through, it creates a note with the dragged text in the diagram panel
 *
 * @author Domas Petrulis
 */
public class DiagramTextDropHandler extends DiagramTransferableDragAndDropHandler
{
	@Override
	public DropTarget getDropTarget(Point location, DiagramPresentationElement diagramPresentationElement, Transferable transferable)
	{
		if (transferable.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor()))
		{
			if (diagramPresentationElement.isEditable())
			{
				return new DropTarget(diagramPresentationElement, true);
			}
		}
		return new DropTarget(null, false);
	}

	@Override
	public void drop(Point location, DiagramPresentationElement diagramPresentationElement, PresentationElement elementOver, Transferable transferable)
	{
		String text = getPlainText(transferable);
		if (!text.isEmpty())
		{
			SessionManager.getInstance().executeInsideSession(Project.getProject(diagramPresentationElement), "Creating note after drop", () -> {
				try
				{
					PresentationElementsManager presentationElementsManager = PresentationElementsManager.getInstance();
					presentationElementsManager.setText(presentationElementsManager.createNote(diagramPresentationElement, location), text);
				}
				catch (ReadOnlyElementException e)
				{
					error("Failed to create note from text drop", e);
				}
			});
		}
	}

	public static String getPlainText(Transferable transferable)
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			String line;
			BufferedReader reader = new BufferedReader(DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(transferable));
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		}
		catch (UnsupportedFlavorException | IOException e)
		{
			error("Failed to get plain text.", e);
		}
		return builder.toString();
	}

	@CheckForNull
	@Override
	public String getDescription()
	{
		return "Create note";
	}

	private static void error(String message, Exception e)
	{
		LogManager.getLogger(DiagramTextDropHandler.class).error(message, e);
	}
}
