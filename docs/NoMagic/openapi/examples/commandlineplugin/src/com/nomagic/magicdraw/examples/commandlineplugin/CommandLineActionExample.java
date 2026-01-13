/*
 * Copyright (c) 2015 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.commandlineplugin;

import com.nomagic.magicdraw.commandline.CommandLineAction;

/**
 * @author Martynas Lelevicius
 */
@SuppressWarnings("squid:S106")
class CommandLineActionExample implements CommandLineAction
{
	@Override
	public byte execute(String[] args)
	{
		System.out.println("------------------- This code is executed in running application environment -------------------");
		System.out.println("Output can be found in the log file.");
		printArguments(args);
		return 0;
	}

	private static void printArguments(String[] args)
	{
		if (args != null && args.length > 0)
		{
			System.out.println("Command line arguments:");
			System.out.println(String.join(", ", args));
		}
		else
		{
			System.out.println("Command line without arguments.");
		}
	}
}
