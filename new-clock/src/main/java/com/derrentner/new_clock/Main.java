package com.derrentner.new_clock;

import javax.swing.SwingUtilities;

import dorkbox.systemTray.*;

public class Main
{
	public static ConfigFrame f;

	public static void main(String[] args)
	{
		(new Thread(() -> tray())).start();
		SwingUtilities.invokeLater(() -> f = new ConfigFrame());
	}

	private static void tray()
	{
		System.out.println("Tray started...");

		SystemTray tray = SystemTray.get();
		if (Main.class.getResource("/pics/clock.png") != null)
			tray.setImage(Main.class.getResource("/pics/clock.png"));
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
			;
			System.exit(0);
		});

		mainMenu.add(exit);

		System.out.println("Tray Done!");
	}
}
