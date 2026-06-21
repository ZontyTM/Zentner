package com.derrentner.new_clock;

import java.util.ArrayList;
import java.util.List;

public class Settings
{
    private List<ClockConfig> clocks = new ArrayList<>();

    public List<ClockConfig> getClocks()
    {
        return clocks;
    }

    public void setClocks(List<ClockConfig> clocks)
    {
        this.clocks = clocks;
    }
}