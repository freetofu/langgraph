package com.nomagic.magicdraw.simulation.examples;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mimics some model execution process that happens outside this tool and produces values to display in diagrams.
 * <br>
 * This primitive implementation returns static wheel diameter, but the speed is increasing by 5 every 4 seconds up to maximum 200;
 * @author Edgaras Dulskis
 */
public class MockExternalExecution
{
	private static final double MAX_SPEED = 200;
	private double currentSpeed;

	public MockExternalExecution(EngineControlUnitValueProvider valuesProvider)
	{
		Runnable increaseSpeed = () -> {
			if (currentSpeed < MAX_SPEED)
			{
				synchronized (this)
				{
					currentSpeed += 5;
				}
				valuesProvider.invalidateCarSpeed(); // needed because values are cached after first get
			}
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(increaseSpeed, 0, 4, TimeUnit.SECONDS);
	}

	public synchronized double getCarSpeed()
	{
		return currentSpeed; // this value changes over time
	}

	public double getWheelDiameter()
	{
		return 19; // return static value
	}
}
