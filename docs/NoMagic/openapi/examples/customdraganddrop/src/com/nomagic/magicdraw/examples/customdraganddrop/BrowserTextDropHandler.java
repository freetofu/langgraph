/*
 * Copyright (c) 2021 NoMagic, Inc. All Rights Reserved
 */
package com.nomagic.magicdraw.examples.customdraganddrop;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.dnd.BrowserTabTreeDragAndDropHandler;
import com.nomagic.magicdraw.ui.dnd.DroppedData;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Class handles drag and drop of text which is dragged to browser tree node from another application.
 * <p>
 * If plain text comes through, it creates a comment with dragged text for node.
 *
 * @author Domas Petrulis
 */
public class BrowserTextDropHandler implements BrowserTabTreeDragAndDropHandler
{
	@Override
	public boolean canImportToNode(Node node, TransferHandler.TransferSupport support)
	{
		if (support.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor()))
		{
			Element element = getElement(node);
			return element != null && element.canAddChild() && element.isEditable();
		}
		return false;
	}

	@CheckForNull
	@Override
	public Object getTransferredData(TransferHandler.TransferSupport support)
	{
		Transferable transferable = support.getTransferable();
		if (transferable != null)
		{
			return DiagramTextDropHandler.getPlainText(transferable);
		}
		return null;
	}

	@Override
	public void importDataToNode(Node node, DroppedData droppedData, Tree tree)
	{
		Object data = droppedData.getTransferredData();
		if (data instanceof String)
		{
			Element element = getElement(node);
			if (element != null)
			{
				Project project = Project.getProject(element);
				SessionManager.getInstance().executeInsideSession(project, "Creating comment after drop", () -> {
					Comment comment = project.getElementsFactory().createCommentInstance();
					comment.setOwner(element);
					comment.setBody((String) data);
					comment.getAnnotatedElement().add(element);
				});
			}
		}
	}

	@Override
	public int getPriority()
	{
		return HIGH_PRIORITY;
	}

	@CheckForNull
	private static Element getElement(Node node)
	{
		Object object = node.getUserObject();
		if (object instanceof Element)
		{
			return (Element) object;
		}
		return null;
	}
}
