/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.imagegenerator;

import com.nomagic.magicdraw.commandline.ProjectCommandLine;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.export.image.ImageExporter;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.utils.MDLog;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Command line utility for diagram images exporting.
 * See the Developer guide for startup command sample.
 */
@SuppressWarnings({"squid:S106", "squid:S1148"})
public class ExportDiagramImages extends ProjectCommandLine
{
	private static final String DESTINATION = "destination_dir";
	private static File mDestinationDir;

	public static void main(String[] args) throws InstantiationException
	{
		new ExportDiagramImages().launch(args);
	}

	@Override
	protected byte execute(Properties props, Project project)
	{
		if (project == null)
		{
			printToCommandLineConsole("Project was not loaded.");
			return -1;
		}
		for (DiagramPresentationElement diagram : project.getDiagrams())
		{
			final File diagramFile = new File(mDestinationDir, diagram.getHumanName() + diagram.getID() + ".svg");
			try
			{
				ImageExporter.export(diagram, ImageExporter.SVG, diagramFile);
			}
			catch (IOException e)
			{
				printToCommandLineConsole(e.getMessage());
				MDLog.getGeneralLog().error(e.getMessage(), e);
				return -1;
			}
		}
		return 0;
	}

	@Override
	protected void parseArguments(String[] args)
	{
		String destinationDirName = Arrays.stream(args).filter(argument -> argument.startsWith(DESTINATION)).findFirst()
				.map(argument -> argument.substring(DESTINATION.length() + 1)).orElse("");

		/*
		 * Check destination dir name.
		 */
		if (destinationDirName.isEmpty())
		{
			System.out.println("Destination directory not defined!");
			throw new IllegalArgumentException();
		}

		/*
		 * Check destination dir.
		 */
		mDestinationDir = new File(destinationDirName);
		if (!mDestinationDir.exists() && !mDestinationDir.mkdirs())
		{
			System.out.println("Cannot create destination directory!");
			throw new IllegalArgumentException();
		}

		System.out.println("Exporting images...");
	}
}
