package com.derrentner.new_clock;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

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
    private static ConfigFrame configFrame;
    private static List<ClockFrame> clockFrames = new ArrayList<>();
    private static Map<Integer,BufferedImage[]> defaultFonts = new HashMap<Integer, BufferedImage[]>();
    public static Main main;

	public static void main(String[] args)
	{
	    main = new Main();
		main.run();
	}

	private void run()
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
	        clockFrames.add(clock);
	        clock.setVisible(true);
	    }

	    configFrame = new ConfigFrame(settings, clockFrames);

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
		else if(DEBUG)
			System.out.println("Picture not Found");

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
	
	public BufferedImage[] getDefaultFont(int textSize)
	{
		if (defaultFonts == null || !defaultFonts.containsKey(textSize))
			return loadSvg(textSize);
		else
			return defaultFonts.get(textSize);
	}
	
	private BufferedImage[] loadSvg(int textSize)
	{
		BufferedImage[] images = new BufferedImage[11];
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        int imgWidth = 52;
        int imgHeight = 74;
        int digitHeight = textSize;
        int digitWidth = imgWidth * digitHeight / imgHeight;
        
        for(int i = 0; i < 11; i++)
        {        	
        	SVGDocument doc;
			try
			{
				if(i == 10) doc = factory.createSVGDocument(Main.class.getResource("font/colon.svg").toString());
				else doc = factory.createSVGDocument(Main.class.getResource("font/" + i + ".svg").toString());
				
				UserAgent userAgent = new UserAgentAdapter();
				DocumentLoader loader = new DocumentLoader(userAgent);
				BridgeContext ctx = new BridgeContext(userAgent, loader);
				ctx.setDynamicState(BridgeContext.DYNAMIC);
				
				GraphicsNode root = new GVTBuilder().build(ctx, doc);
				
				BufferedImage image = new BufferedImage(digitWidth, textSize, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D g2d = image.createGraphics();
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				
				AffineTransform at = new AffineTransform();
				g2d.setTransform(at);
				
				Rectangle2D bounds = root.getPrimitiveBounds();
				double scaleX = digitWidth  / bounds.getWidth();
				double scaleY = digitHeight / bounds.getHeight();
				double scale = Math.min(scaleX, scaleY);
				
				if (i == 10) scale *= 0.75;
				
				g2d.scale(scale, scale);
				root.paint(g2d);
				g2d.dispose();
				
				images[i] = image;
			}
			catch (IOException e) { e.printStackTrace(); }
        	
        }
        defaultFonts.put(textSize, images);
        return images;
	}
}
