package com.derrentner.new_clock;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.derrentner.new_clock.ClockFrame.ClockType;

import dorkbox.systemTray.*;




public class Main
{
	public enum DisplayPosition
	{
		TopLeft,
		TopRight
	}
	
	public static Boolean DEBUG = false;
    private static Settings settings;
    static ConfigFrame configFrame;
    static List<ClockFrame> clockFrames = new ArrayList<>();

	public static void main(String[] args)
	{
		if (DEBUG == true)
		{
			System.out.println("Headless = " + GraphicsEnvironment.isHeadless());
			System.out.println(System.getProperty("java.home"));
			System.out.println(System.getProperty("java.version"));
			System.out.println(System.getProperty("java.vendor"));
			
			SystemTray.DEBUG = true;
			System.out.println(Main.class.getResource("font/0.svg"));
			System.out.println(Main.class.getResource("pics/exit.png"));			
		}
		
		settings = SettingsManager.load();

	    for (ClockConfig config : settings.getClocks())
	    {
	        ClockFrame clock = new ClockFrame(config);
	        clockFrames.add(clock); // ✅ IMPORTANT FIX
	        clock.setVisible(true);
	    }

	    configFrame = new ConfigFrame(settings, clockFrames);
	    configFrame.setVisible(true);

	    new Thread(Main::tray).start();		
	}

	private static void tray()
	{
		SystemTray tray = SystemTray.get();

		if(DEBUG) System.out.println("tray = " + tray);

		if (tray == null) {
			if(DEBUG) System.out.println("SystemTray initialization failed");
		    return;
		}
		if(DEBUG) System.out.println("Tray started...");

//		SystemTray tray = SystemTray.get();
		if (Main.class.getResource("pics/clock.png") != null)
			tray.setImage(Main.class.getResource("pics/clock.png"));
		else
			if(DEBUG) System.out.println("Picture not Found");

		Menu mainMenu = tray.getMenu();

		MenuItem openConfig = new MenuItem("Open Config", e -> {
			if(DEBUG) System.out.println("Opening Config...");
			configFrame.setVisible(true);
		});

		mainMenu.add(openConfig);

		MenuItem exit = new MenuItem("Exit", e -> {
			if(DEBUG) System.out.println("Exiting...");
			System.exit(0);
		});

		mainMenu.add(exit);

		if(DEBUG) System.out.println("Tray Done!");
	}
}
