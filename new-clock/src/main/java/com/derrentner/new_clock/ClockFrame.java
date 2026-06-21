package com.derrentner.new_clock;

import javax.swing.*;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import com.derrentner.new_clock.Main.DisplayPosition;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
//import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@SuppressWarnings("serial")
public class ClockFrame extends JFrame
{
	private final ClockConfig config;

    
	
	
	public enum ClockType
	{
		HourMinute,
		HourMinuteSecond
	}
    private BufferedImage[] digits;
    private BufferedImage colon;
    private int timeShiftMinutes = 0;
    private volatile LocalTime time = LocalTime.now().plusMinutes(timeShiftMinutes);
    private int monitorIndex = 0;
    private int imgWidth = 52;
    private int imgHeight = 74;
    private int digitHeight = 10;
    private int digitWidth = imgWidth * digitHeight / imgHeight;
    private int textPadding = 1;
    private int padding = 2;
    private int recWitdth = (int)(4.5f * digitWidth) + Math.max((int)(digitWidth * 0.25), 2 * padding) + 4 * textPadding;
    private int recHeight = digitHeight + Math.max((int)(digitHeight * 0.25),  2 * padding);
    private ClockType clockType = ClockType.HourMinute;
    private Main.DisplayPosition displayPosition = Main.DisplayPosition.TopRight;
    private Color color = new Color(255, 140, 50, 255);
    private ScheduledExecutorService executor;
    private volatile ScheduledFuture<?> scheduleHandler;

    public ClockFrame(ClockConfig config)
    {		
        String sessionType = System.getenv("XDG_SESSION_TYPE");
        if(Main.DEBUG) System.out.println("sessionType: " + sessionType);
        
        if(!sessionType.equals("wayland")) {
            MouseAdapter hoverAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered(e);
                }
            };
            addMouseListener(hoverAdapter);
        }
//    	if (display != -1) monitorIndex = display;
//		if (type != null) clockType = type;
//		if (position != null) displayPosition = position;
		
        
        this.config = config;

        if (config.isShowSeconds())
        {
            setType(ClockType.HourMinuteSecond);
        }
        else
        {
            setType(ClockType.HourMinute);
        }
        loadImages();

        applyConfig();
        
	    setType(Type.POPUP);
        setUndecorated(true);
        setAlwaysOnTop(true);
		setFocusable(false);
	    setFocusableWindowState(false);
	    updatePosition(displayPosition);
        getContentPane().setBackground(Color.GRAY);
        setVisible(true);
        
        startScheduler();
        
        clockPanel.setOpaque(true);
        clockPanel.setBackground(Color.GRAY);

        setContentPane(clockPanel);
    }
    
    private void startScheduler() {

        if (executor != null) {
            executor.shutdownNow();
        }

        executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> SwingUtilities.invokeLater(() -> {
            updateTime();
            clockPanel.repaint();
        });

        if (clockType == ClockType.HourMinute) {

            int initialDelay = 60 - LocalTime.now().getSecond();

            scheduleHandler = executor.scheduleAtFixedRate(task, initialDelay, 60, TimeUnit.SECONDS);

        } else {

            long initialDelay = 1000 - (System.currentTimeMillis() % 1000);

            scheduleHandler = executor.scheduleAtFixedRate(task, initialDelay, 1000, TimeUnit.MILLISECONDS);
        }
    }
    
    private final JPanel clockPanel = new JPanel()
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            if (digits == null) return;

            Graphics2D g2 = (Graphics2D) g;

            int x = Math.max((int)(digitWidth * 0.125), padding);
            int y = Math.max((int)(digitHeight * 0.125), padding);

            char[] time = (clockType == ClockType.HourMinute)
                    ? getTime().format(DateTimeFormatter.ofPattern("HH:mm")).toCharArray()
                    : getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")).toCharArray();

            for (char c : time)
            {
                BufferedImage img = (c == ':') ? colon : digits[c - '0'];

                if (c == ':')
                {
                    g2.drawImage(img,
                            x - (int)(digitWidth * 0.25 + textPadding / 2.0f),
                            y,
                            digitWidth,
                            digitHeight,
                            null);

                    x += textPadding + digitWidth / 2;
                }
                else
                {
                    g2.drawImage(img, x, y, digitWidth, digitHeight, null);
                    x += textPadding + digitWidth;
                }
            }
        }
    };
    
    public void hovered(MouseEvent m)
	{

	    new Thread(() -> {
		    Point mouse = MouseInfo.getPointerInfo().getLocation();
		    Rectangle r = getBounds();
		    
	    	setVisible(false);
	    	while(r.contains(mouse)) {
				try { Thread.sleep(50); }
				catch (InterruptedException e) { e.printStackTrace(); }
				mouse = MouseInfo.getPointerInfo().getLocation();
			}
		    
			setVisible(true);
	    }).start();
	}
    
    private void loadImages()
    {
        digits = new BufferedImage[10];

        for (int i = 0; i < 10; i++)
        {
        	try
            {
                BufferedImage img = loadSvg(getClass().getResource("font/" + i + ".svg"), digitWidth, digitHeight, false);
                tint(img, color);
                digits[i] = img;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            colon = loadSvg(getClass().getResource("font/colon.svg"),
                    digitWidth, digitHeight, true);
            tint(colon, color);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void tint(BufferedImage img, Color color)
    {
    	for (int y = 0; y < img.getHeight(); y++)
    	{
    		for (int x = 0; x < img.getWidth(); x++)
    		{
    			int argb = img.getRGB(x, y);
    			
    			int alpha = (argb >> 24) & 0xff;
    			if (alpha == 0) continue;
    			
    			int rgb =
    					(color.getRed() << 16) |
    					(color.getGreen() << 8) |
    					color.getBlue();
    			
    			img.setRGB(x, y, (alpha << 24) | rgb);
    		}
    	}
    }
    
    private BufferedImage loadSvg(URL url, int width, int height, Boolean colon) throws Exception {

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        SVGDocument doc = factory.createSVGDocument(url.toString());

        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(userAgent);
        BridgeContext ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);

        GraphicsNode root = new GVTBuilder().build(ctx, doc);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        AffineTransform at = new AffineTransform();
        g2d.setTransform(at);

        Rectangle2D bounds = root.getPrimitiveBounds();
        double scaleX = width  / bounds.getWidth();
        double scaleY = height / bounds.getHeight();
        double scale = Math.min(scaleX, scaleY);
        
        if (colon) scale *= 0.75;
        
        g2d.scale(scale, scale);
        root.paint(g2d);
        g2d.dispose();

        return image;
    }

    public Color getColor() { return color; }
    
    public ClockType getClockType() { return clockType; }
    
    public DisplayPosition getDisplayPosition() { return displayPosition; }
    
    public synchronized LocalTime getTime() { return time; }
    
    public int getDisplay() { return monitorIndex; }
    
    public ClockConfig getConfig() { return config; }

    public void setType(ClockType type)
    {
    	clockType = type;
    	updatePosition(displayPosition);
    	startScheduler();
    	updateTime();
    	repaint();
    }
    
    public void updateDisplay(int display) {
    	monitorIndex = display;
    	updatePosition(null);
    }
    
    public void updatePosition(Main.DisplayPosition monitorPosition)
	{
	    GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	    Rectangle bounds = screens[monitorIndex].getDefaultConfiguration().getBounds();
	    int x, y;
	    recWitdth = (int)(4.5f * digitWidth) + Math.max((int)(digitWidth * 0.25), 2 * padding) + 4 * textPadding;
	    recHeight = digitHeight + Math.max((int)(digitHeight * 0.25),  2 * padding);
	    if(clockType == ClockType.HourMinuteSecond) recWitdth += (int)(2.5f * digitWidth + 3 * textPadding);
	    setSize(recWitdth, recHeight);

	    if(monitorPosition == null) monitorPosition = displayPosition;
	    else displayPosition = monitorPosition;
	    
	    switch (monitorPosition) {
	        case TopLeft -> {
	            x = bounds.x;
	            y = bounds.y;
	        }

	        case TopRight -> {
	            x = bounds.x + bounds.width - getWidth();
	            y = bounds.y;
	        }
	        
	        default -> throw new IllegalStateException();
	    }
	    setLocation(x, y);
	}
    
    public void updateColor(Color color)
    {
        this.color = color;

        if (digits == null) return;

        for (BufferedImage img : digits)
        {
            tint(img, color);
        }

        if (colon != null)
            tint(colon, color);

        clockPanel.repaint();
    }
    
    public synchronized void updateTime()
    {
    	time = LocalTime.now().plusMinutes(timeShiftMinutes);
    }

    public void changeTextSize(int newSize)
    {
        digitHeight = newSize;
        digitWidth = imgWidth * digitHeight / imgHeight;

        loadImages();
        updatePosition(null);
        repaint();
    }
    
    private void applyConfig()
    {
    	setType((config.isShowSeconds()) ? ClockType.HourMinuteSecond : ClockType.HourMinute);
        changeTextSize(config.getTextSize());
        updateColor(config.getColor());
        updateDisplay(config.getMonitor());
        updatePosition(config.getMonitorPosition());
    }
}
