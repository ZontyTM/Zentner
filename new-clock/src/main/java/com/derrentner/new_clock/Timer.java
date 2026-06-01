package com.derrentner.new_clock;

import java.awt.Color;

public class Timer
{
	private int hour;
	private int minute;
	private Color color;
	private int position;
	private int hourSystem;

	public Timer()
	{
		hour = 0;
		minute = 0;
		color = Color.WHITE;
		position = 0;
		hourSystem = 24;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public void setHourSystem(int hourSystem)
	{
		this.hourSystem = hourSystem;
	}

	public void addTime(int hour, int minute)
	{
		this.hour += hour;
		this.minute += minute;
	}

	public void progressTime()
	{
		this.minute++;
		if (this.minute == 60)
		{
			this.minute = 0;
			this.hour++;
			if (this.hour == this.hourSystem)
			{
				this.hour = 0;
			}
		}
		checkStuff();
	}

	public void checkStuff()
	{
		checkTimer();
		checkAlarm();
	}

	public void checkTimer()
	{
	} // Needs to be implemented

	public void checkAlarm()
	{
	} // Needs to be implemented
}
