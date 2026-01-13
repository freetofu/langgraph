/*
 * Copyright (c) 2016 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.fileattachments;

import com.nomagic.annotation.Used;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.fileattachments.FileAttachmentsHelper;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.IOException;

/**
 * @author Edgaras Dulskis
 */
@SuppressWarnings({"squid:S106", "squid:S1148"})
public class FileAttachmentsExample
{
	/**
	 * This method shows how to attach a file to a project
	 *
	 * @param owner parent element that will directly own attached file element
	 */
	@Used("example")
	public void attachFile(Element owner)
	{
		Project project = Project.getProject(owner);

		SessionManager.getInstance().createSession(project, "Create Attached File");

		// Attached File element is a DSLed (Stereotyped) UML Comment element
		Comment attachedFileElement = FileAttachmentsHelper.createAttachedFileElement(owner);
		// file to be attached. It should exist.
		File file = new File("C:/yourFile.txt");
		FileAttachmentsHelper.storeFileToAttachedFile(attachedFileElement, file);

		SessionManager.getInstance().closeSession(project);
	}

	/**
	 * Stores attached file to the provided directory. File name and the extension will be the same as the
	 * originally attached file. It can be seen in Attached File's Specification window.
	 *
	 * @param directory directory to store the attached file to. Directory will be created if not yet exists
	 * @see FileAttachmentsHelper#getStoredFileInputStream(Element)
	 */
	@Used("example")
	@CheckForNull
	public File saveAttachedFileToDirectory(File directory, Comment attachedFileElement)
	{
		try
		{
			File file = FileAttachmentsHelper.extractToDirectory(directory, attachedFileElement);
			if (file == null)
			{
				System.out.println("Element did not contain any attachment");
			}
			return file;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Failed to save the output file");
			return null;
		}
	}

	@Used("example")
	public void removeAttachedFile(Comment attachedFileElement)
	{
		Project project = Project.getProject(attachedFileElement);

		SessionManager.getInstance().createSession(project, "Remove Attached File");

		// it is enough to remove Attached File model element just like any other element. The attachment data will be also removed with it.
		attachedFileElement.dispose();

		SessionManager.getInstance().closeSession(project);
	}
}
