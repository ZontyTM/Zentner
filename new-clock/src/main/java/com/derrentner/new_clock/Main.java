package com.derrentner.new_clock;

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
	
	public static ConfigFrame f;
	public static ClockFrame[] cf;

	public static void main(String[] args)
	{
		SystemTray.DEBUG = true;
		System.out.println(Main.class.getResource("font/0.svg"));
		System.out.println(Main.class.getResource("pics/exit.png"));
		(new Thread(() -> tray())).start();
		SwingUtilities.invokeLater(() -> cf = new ClockFrame[] { new ClockFrame(-1, null, ClockType.HourMinute)} );
		SwingUtilities.invokeLater(() -> f = new ConfigFrame(cf));		
	}

	private static void tray()
	{
		SystemTray tray = SystemTray.get();

		System.out.println("tray = " + tray);

		if (tray == null) {
		    System.out.println("SystemTray initialization failed");
		    return;
		}
		System.out.println("Tray started...");

//		SystemTray tray = SystemTray.get();
		if (Main.class.getResource("pics/clock.png") != null)
			tray.setImage(Main.class.getResource("pics/clock.png"));
		else
			System.out.println("Picture not Found");

		Menu mainMenu = tray.getMenu();

		MenuItem openConfig = new MenuItem("Open Config", e -> {
			System.out.println("Opening Config...");
			f.setVisible(true);
		});

		mainMenu.add(openConfig);

		MenuItem exit = new MenuItem("Exit", e -> {
			System.out.println("Exiting...");
			System.exit(0);
		});

		mainMenu.add(exit);

		System.out.println("Tray Done!");
	}
}
