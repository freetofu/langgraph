package com.nomagic.magicdraw;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This is example of maven MagicDraw integration.
 *
 * When executed new commandline java process is started. Classpath is constructed from
 * magicdraw.properties and additional classpath provided by maven parameter {@link #classpath}.
 * Provided application arguments by {@link #app_args} are also combined with ones from
 * magicdraw.properties. First goes MagicDraw default arguments then follow custom arguments from
 * {@link #app_args} property.
 *
 * Note: all paths starts from MagicDraw home defined in {@link #md_home}.
 *
 * @author Mantas Balnys
 *
 * @goal run
 */
public class StartApp extends AbstractMojo
{
	/**
	 * MagicDraw home folder. Required to set in pom.xml file.
	 *
	 * @parameter
	 */
	private final String md_home = null;

	/**
	 * Java home. By default it is loaded from "bin/magicdraw.properties" file.
	 *
	 * @parameter
	 */
	private String java_home = null;

	/**
	 * Java executable file. It is not recommended to set this parameter in "pom.xml" file.
	 *
	 * @parameter
	 */
	private String java_executable = null;

	/**
	 * Main class to start. By default MagicDraw main is used. Set it if you want to start your own
	 * class.
	 *
	 * @parameter
	 */
	private String main_class = null;

	/**
	 * Additional classpath. This classpath is appended to default MagicDraw classpath. Define it if
	 * your plugin is not in default MagicDraw classpath.
	 *
	 * @parameter
	 */
	private final String classpath = null;

	/**
	 * Application arguments. These arguments are appended to default MagicDraw arguments.
	 *
	 * @parameter
	 */
	private final String app_args = null;

	@Override
	@SuppressWarnings("squid:S106")
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		// Find MagicDraw home.
		if (md_home == null)
		{
			throw new MojoExecutionException("MagicDraw home is mandatory attribute. 'md_home' value must be defined.");
		}
		File mdHome = new File(md_home);

		// Read MagicDraw properties file.
		File propertiesFile = new File(mdHome, "bin/magicdraw.properties");
		if (!propertiesFile.isFile())
		{
			throw new MojoExecutionException("'bin/magicdraw.properties' file not found. Check if MagicDraw home is defined correctly.");
		}
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(propertiesFile));
		}
		catch (IOException ex)
		{
			throw new MojoExecutionException("Failed while loading 'bin/magicdraw.properties' file.", ex);
		}

		// Find java executable.
		if (java_executable == null)
		{
			if (java_home == null)
			{
				java_home = properties.getProperty("JAVA_HOME");
				if (java_home == null)
				{
					java_home = System.getProperty("java.home");
				}
			}
			java_executable = java_home + File.separator + "bin" + File.separator + "java";
		}
		File javaExecutable = new File(java_executable);
		if (!javaExecutable.isFile())
		{
			throw new MojoExecutionException("Java executable not found in " + javaExecutable.getAbsolutePath());
		}
		String javaArgs = properties.getProperty("JAVA_ARGS");

		// Construct application classpath.
		String commandClassPath = properties.getProperty("CLASSPATH").replace(':', File.pathSeparatorChar);
		if (this.classpath != null)
		{
			commandClassPath += File.pathSeparator + this.classpath;
		}

		// Find main class.
		if (main_class == null)
		{
			main_class = properties.getProperty("MAIN_CLASS");
		}

		// Construct application arguments.
		String appArgs = properties.getProperty("APP_ARGS");
		if (app_args != null)
		{
			if (appArgs == null)
			{
				appArgs = app_args;
			}
			else
			{
				appArgs = appArgs + " " + app_args;
			}
		}

		// Create commandline.
		List<String> cmd = new ArrayList<>();
		cmd.add(javaExecutable.getAbsolutePath());
		if (javaArgs != null)
		{
			String[] args = javaArgs.trim().split("\\s+");
			cmd.addAll(Arrays.asList(args));
		}
		cmd.add("-cp");
		cmd.add(commandClassPath);
		cmd.add(main_class);
		if (appArgs != null)
		{
			String[] app = appArgs.trim().split("\\s+");
			cmd.addAll(Arrays.asList(app));
		}

		getLog().info("Starting MagicDraw from commandline:\n" + cmd);
		try
		{
			// Start application.
			Process p = Runtime.getRuntime().exec(cmd.toArray(new String[0]), null, mdHome);
			Read s1 = new Read(p.getInputStream(), System.out);
			Read s2 = new Read(p.getErrorStream(), System.err);
			s1.start();
			s2.start();
			int ret = p.waitFor();
			s1.join();
			s2.join();
			getLog().info("Program Finished.");
			if (ret != 0)
			{
				throw new MojoExecutionException("Failed in program execution. Program returned " + ret);
			}
		}
		catch (Exception ex)
		{
			throw new MojoExecutionException("Failed in program execution.", ex);
		}
	}

	/**
	 * Print output of {@link Process}.
	 *
	 * @author Mantas Balnys
	 */
	public static class Read extends Thread
	{
		private final InputStream is;

		private final PrintStream out;

		public Read(InputStream is, PrintStream out)
		{
			this.is = is;
			this.out = out;
		}

		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					int s = is.read();
					if (s < 0)
					{
						break;
					}
					out.write(s);
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace(out);
			}
		}
	}
}
