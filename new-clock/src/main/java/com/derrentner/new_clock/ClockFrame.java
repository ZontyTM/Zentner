package com.derrentner.new_clock;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

public class ClockFrame extends JFrame
{
	public ClockFrame(int Display)
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screen.width;
		int screenHeight = screen.height;
//		Rectangle monitor;
//		if(Display == -1 || Display > GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length) monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations()[0].getBounds();
//		else monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[Display].getConfigurations()[0].getBounds();
//    	//setLocation(monitor.x + monitor.width - getWidth(), 0/*verticalOffset*/);
    	
		// Window settings
		setUndecorated(true);
		setSize(32, 14);
		setShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}
