package com.derrentner.new_clock;

import java.awt.Image;
import java.awt.Toolkit;

import dorkbox.systemTray.SystemTray;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;


public class Main {

	public String os = "lin";
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            SaveFile();
	        }
	    }, "Shutdown-thread"));
		
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")) os = "win";
		System.out.println(os.toLowerCase());

		Tray();
	}

	private static void Tray() {
		System.out.println("Starting Tray");

		String os = System.getProperty("os.name").toLowerCase();

		SystemTray systemTray = SystemTray.get("SysTrayExample");
		if (systemTray == null) {
		    throw new RuntimeException("Unable to load SystemTray!");
		}

		Image image = Toolkit.getDefaultToolkit().getImage(
		    Main.class.getResource("/pics/clock.png")
		);

		systemTray.setImage(image);

		Menu mainMenu = systemTray.getMenu();

		
		MenuItem openItem = new MenuItem("Open Config", e -> {});
		mainMenu.add(openItem);

		MenuItem exit = new MenuItem("Exit", e -> { System.exit(0); });
		mainMenu.add(exit);

		System.out.println("Tray Done!");
	}
	
	private static void SaveFile() {
		System.out.println("Saving...");
		
		System.out.println("Saved!");
	}
}
