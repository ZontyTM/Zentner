import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Frame extends JWindow {
	
	private JPanel clockBG;
	private JPanel timerBG;
	private JPanel stopwatchBG;
    private BufferedImage pic = null;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
    private Font f = new Font("Segoe UI Black", Font.PLAIN, 10);
    private Color ClockColor = new Color(255, 255, 255, 220);
    private Color TimerColor = new Color(255, 255, 255, 220);
    private Color StopwatchColor = new Color(255, 255, 255, 220);
    private int clockY;
    //	private Kernel32.SYSTEM_POWER_STATUS battery;
	
	public Frame(String file) {
	    //super("Clock");
	    setType(Type.POPUP);
		addMouseListener(new MouseHandler());
	    setAlwaysOnTop(true);
	    //setUndecorated(true);
	    setFocusable(false);
	    setFocusableWindowState(false);
	    setBackground(new Color(0,0,0,0));
	    
	    /*battery = new Kernel32.SYSTEM_POWER_STATUS();
		if(!battery.hasBattery()) battery = null;
		else file += "1";*/
	    
	    try { pic = ImageIO.read(ClassLoader.getSystemResourceAsStream("pics/" + file + ".png"));
		} catch (IOException e1) {e1.printStackTrace();}
    	setSize(pic.getWidth(), pic.getHeight());						//FRAME SIZE
    	clockY = (pic.getHeight() + 6)/2+1;
    	
    	timerBG = new JPanel() {
			Font f = new Font("Segoe UI Black", Font.PLAIN, 10);
	    	int y = (pic.getHeight() + 6)/2+1 + pic.getHeight();
	    	
	        @Override
	        protected void paintComponent(Graphics g) {
	        	paintClock(g);
	        	paintStopwatch(g);
	        	//g.clearRect(0, 0, getWidth(), getHeight());			//??
	        	g.setColor(TimerColor);
	        	if(Main.timer.getState() == -1) return;
	        	int height = pic.getHeight();
                g.drawImage(pic, 0, height, null);
                
                g.setFont(f);
                
                String show = Main.timer.getFormattedTime();
                
                g.drawString(show, (getWidth()-g.getFontMetrics().stringWidth(show))/2+1, y);
	        }
	    };
		
	    clockBG = new JPanel() {
	    	
	        @Override
	        protected void paintComponent(Graphics g) {
	        	paintClock(g);
	        	paintStopwatch(g);
	        }
	    };
	    
    	/*if(battery != null) {
                	battery.update();
                	show = battery.getBatteryLifePercent() + " " + show;
                }*/
		
	    setVisible(true);
	    setText();
	}
	
	protected void paintClock(Graphics g) {
		g.setColor(ClockColor);
		
    	//g.clearRect(0, 0, getWidth(), getHeight());

        g.drawImage(pic, 0, 0, null);
        
        g.setFont(f);
        
        String show = LocalTime.now().format(format);
        
        g.drawString(show, (getWidth()-g.getFontMetrics().stringWidth(show))/2+1, clockY);
	}
	
	public void paintStopwatch(Graphics g) {
		g.setColor(StopwatchColor);
		
    	//g.clearRect(0, 0, getWidth(), getHeight());

    	int height = pic.getHeight();
    	if(Main.currentTimer) height *= 2;
        g.drawImage(pic, 0, height, null);
        
        g.setFont(f);
        
        String show = Main.stopwatch.getFormattedTime();

    	int y = (pic.getHeight() + 6)/2+1 + pic.getHeight();
    	if(Main.currentTimer) y += pic.getHeight();
        
        g.drawString(show, (getWidth()-g.getFontMetrics().stringWidth(show))/2+1, y);
	}
	
	public void setClockColor(int r, int g, int b, int a) { ClockColor = new Color(r, g, b, a); }
	public void setClockColor(Color color) { ClockColor = color; }
	
	public void setTimerColor(int r, int g, int b, int a) { TimerColor = new Color(r, g, b, a); }
	public void setTimerColor(Color color) { TimerColor = color; }
	
	public void setStopwatchColor(int r, int g, int b, int a) { StopwatchColor = new Color(r, g, b, a); }
	public void setStopwatchColor(Color color) { StopwatchColor = color; }
	
	public int getPicWidth() {return pic.getWidth();}
	public int getPicHeight() {return pic.getHeight();}
	
	public void setLocation(int position) {
		Rectangle monitor;
		if(position == -1 || position > GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length) monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations()[0].getBounds();
		else monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[position].getConfigurations()[0].getBounds();
    	setLocation(monitor.x + monitor.width - getWidth(), 0);
	}
	
	public void setText() {
		boolean visible = isVisible();
	    if(visible) setVisible(false);
    	setBackground(new Color(0, 0, 0, 255));
        setBackground(new Color(0, 0, 0, 0));
		//setContentPane(clockBG);
		setContentPane(timerBG);
		if(visible) setVisible(true);
	}
	
	private class MouseHandler implements MouseListener {
		public void mouseClicked(MouseEvent m) {
			/*try {
				ProcessBuilder builder = new ProcessBuilder("PowerShell.exe", 
						"Add-Type -AssemblyName System.Windows.Forms\n",
						"[System.Windows.Forms.Application]::SetSuspendState(0,0,0)");
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line;
		    	while((line = r.readLine()) != null) System.err.println(line);
			} catch (Exception e) {e.printStackTrace();}*/
			/*try { Sleep();
			} catch (Exception e) {e.printStackTrace();}*/
		}
		public void mouseEntered(MouseEvent m) {
		    new Thread(() -> {
				int x = getX(), y = getY(), w = getWidth(), h = getHeight();

			    setVisible(false);
			    
			    
		    	while(true) {
					Point mouse = MouseInfo.getPointerInfo().getLocation();
					
					if(!isIn(mouse.x, mouse.y, x, y, w, h)) break;
					
					Main.stopPlayTimer();
					try { Thread.sleep(100);
					} catch (InterruptedException e) { e.printStackTrace();}
				}
			    
		    	setBackground(new Color(0, 0, 0, 255));
                setBackground(new Color(0, 0, 0, 0));
                
				setVisible(true);
				Main.redraw();
		    }).start();
		}
		public void mouseExited(MouseEvent m) {
		    //bg.setVisible(true);
		}
		public void mousePressed(MouseEvent m) {}
		public void mouseReleased(MouseEvent m) {}
		
		private boolean isIn(int x, int y, int bx, int by, int w, int h){
			return (x >= bx && x <= (bx + w) && y >= by && y <= (by + h));
		}
	}
	
	public void Sleep() throws AWTException, InterruptedException {
	    Robot robot = new Robot();
        robot.setAutoDelay(0);
        
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_X);
        robot.keyRelease(KeyEvent.VK_X);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        
        Thread.sleep(100);
        
        robot.keyPress(KeyEvent.VK_R);
        robot.keyRelease(KeyEvent.VK_R);
        
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
	}
}