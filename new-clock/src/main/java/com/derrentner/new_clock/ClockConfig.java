package com.derrentner.new_clock;

import java.awt.Color;

public class ClockConfig
{
    private int timeShift;
    private int monitor;
    private int textSize;
    private int bgTransparency;
    private Main.DisplayPosition monitorPosition;
    private Color color;
    private boolean showSeconds;

    public ClockConfig()
    {
        timeShift = 0;
        monitor = 0;
        monitorPosition = Main.DisplayPosition.TopRight;
        textSize = 10;
        color = Color.WHITE;
        showSeconds = false;
        bgTransparency = 0;
    }
    
    public ClockConfig(ClockConfig other)
    {
        this.timeShift = other.timeShift;
        this.monitor = other.monitor;
        this.monitorPosition = other.monitorPosition;
        this.textSize = other.textSize;
        this.color = other.color;
        this.showSeconds = other.showSeconds;
        this.bgTransparency = other.bgTransparency;
    }

    public int getTimeShift()
    {
        return timeShift;
    }

    public void setTimeShift(int timeShift)
    {
        this.timeShift = timeShift;
    }

    public int getMonitor()
    {
        return monitor;
    }

    public void setMonitor(int monitor)
    {
        this.monitor = monitor;
    }

    public Main.DisplayPosition getMonitorPosition()
    {
        return monitorPosition;
    }

    public void setMonitorPosition(Main.DisplayPosition monitorPosition)
    {
        this.monitorPosition = monitorPosition;
    }

    public int getTextSize()
    {
        return textSize;
    }

    public void setTextSize(int textSize)
    {
        this.textSize = textSize;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public boolean isShowSeconds()
    {
        return showSeconds;
    }

    public void setShowSeconds(boolean showSeconds)
    {
        this.showSeconds = showSeconds;
    }
    
    public int getBGTransparency()
    {
    	return bgTransparency;
    }
    
    public void setBGTransparency(int bgT)
    {
    	bgTransparency = bgT;
    }
}