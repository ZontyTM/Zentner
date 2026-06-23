package com.derrentner.new_clock;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsManager
{
    public static Settings load()
    {
        Settings settings = new Settings();
        Path file = getSettingsFile();

        if (!Files.exists(file))
        {
            settings.getClocks().add(new ClockConfig());
            return settings;
        }

        Properties p = new Properties();

        try (InputStream in = Files.newInputStream(file))
        {
            p.load(in);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load settings", e);
        }

        int count = Integer.parseInt(p.getProperty("clock.count", "1"));

        for (int i = 0; i < count; i++)
        {
            ClockConfig c = new ClockConfig();

            c.setTextSize(Integer.parseInt(p.getProperty("clock." + i + ".textSize", "10")));
            c.setShowSeconds(Boolean.parseBoolean(p.getProperty("clock." + i + ".seconds", "false")));
            c.setMonitor(Integer.parseInt(p.getProperty("clock." + i + ".monitor", "0")));
            c.setTimeShift(Integer.parseInt(p.getProperty("clock." + i + ".timeshift", "0")));

            String[] rgb = p.getProperty("clock." + i + ".color", "255,255,255").split(",");
            c.setColor(new Color( Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]) ));
            c.setBGTransparency(Integer.parseInt(p.getProperty("clock." + i + ".bgtransparency", "0")));
            c.setMonitorPosition(Main.DisplayPosition.valueOf(p.getProperty("clock." + i + ".position", "TopRight")));
            settings.getClocks().add(c);
        }

        return settings;
    }

    public static void save(Settings settings)
    {
        Properties p = new Properties();

        var clocks = settings.getClocks();
        p.setProperty("clock.count", String.valueOf(clocks.size()));

        for (int i = 0; i < clocks.size(); i++)
        {
            ClockConfig c = clocks.get(i);
            Color col = c.getColor();

            p.setProperty("clock." + i + ".textSize", String.valueOf(c.getTextSize()));
            p.setProperty("clock." + i + ".seconds", String.valueOf(c.isShowSeconds()));
            p.setProperty("clock." + i + ".monitor", String.valueOf(c.getMonitor()));
            p.setProperty("clock." + i + ".position", c.getMonitorPosition().name());
            p.setProperty("clock." + i + ".timeshift", String.valueOf(c.getTimeShift()));
            p.setProperty("clock." + i + ".color", col.getRed() + "," + col.getGreen() + "," + col.getBlue());
            p.setProperty("clock." + i + ".bgtransparency", String.valueOf(c.getBGTransparency()));
        }

        try (OutputStream out = Files.newOutputStream(getSettingsFile()))
        {
            p.store(out, "NewClock Settings");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to save settings", e);
        }
    }

    private static Path getSettingsFile()
    {
        try
        {
            Path jarPath = Paths.get( Main.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
            return jarPath.getParent().resolve("settings.properties");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}