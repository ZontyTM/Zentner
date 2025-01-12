import java.time.LocalTime;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
//import dorkbox.systemTray.Checkbox;
import java.awt.Color;

public class Main {
	
	private final static int RESYNC_INTERVAL = 60;
	private static int resync = 0;
	private static int intervalTime = 0;
	private static String intervalSoundFile = "pics/interval.wav";
	private static String timerSoundFile = "pics/timer.wav";
	private static int[] intervalNumbers = {0,1,2,5,10,15,30,60};
	private static int[] timerNumbers = {1,2,5,10,15,20,30,60};
	public static Timer timer = new Timer();
	private static Thread timerThread;
	private static String[] ColorNames = {
										"White",
										"Orange",
										"Blue",
										"Red",
										"Cyan",
										"Green",
										"Yellow",
										"Magenta"
	};
	private static Color[] Colors = {
										new Color(255, 255, 255, 255),
										new Color(227, 99, 27, 255),
										new Color(37, 100, 157, 255),
										Color.RED,
										Color.CYAN,
										Color.GREEN,
										Color.YELLOW,
										Color.MAGENTA
	};
	private static Frame f;
	
	public static void main(String[] args) {
		//play(intervalSoundFile);
		//play(timerSoundFile);
		//(new Thread(() -> play(soundFile))).start();
		

		String file = "clock-bg";
		f = new Frame(file);

		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-old")) file += args[i];
			if(args[i].equals("-clock")) {
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].equals(ColorNames[j])) f.setClockColor(Colors[j]);
				}
			}
			if(args[i].equals("-timer")) {
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].equals(ColorNames[j])) f.setTimerColor(Colors[j]);
				}
			}
		}
	    
		(new Thread(() -> Tray(f))).start();
		System.out.flush();
		sync();
	    
	    while (true) {
		    f.setText();
	    	
		    /*if(timerMenuMin != 0) {
		    	timerMenuMin--;
		    	if(timerMenuMin == 0) {
					System.out.println("Timer");
					(new Thread(() -> play(timerMenuSoundFile, timerMenuSec))).start();
					timerMenuSec = 0;
				}
		    }*/
	    	resync++;
	    	if(resync >= RESYNC_INTERVAL) {
	    		resync = 0;
	    	    sync();
	    	} else {
	    		if(intervalTime != 0 && (LocalTime.now().getMinute() % intervalTime == 0))
	    			{ (new Thread(() -> play(intervalSoundFile))).start(); }
		    	try { Thread.sleep(60000);
		    	} catch (InterruptedException e) { e.printStackTrace(); }
	    	}
		}
	    
	}
	
	public static void redraw() { f.setText(); }
	
	private static void sync() {
		System.out.println("Syncing...");
		System.out.flush();
	    while(true) {
	    	//System.out.println(LocalTime.now().getSecond());
	    	
	    	if(LocalTime.now().getSecond() == 0) break;
	    	
	    	try { Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
	    }
	    System.out.println("Synced");
	}
	
	private static void Tray(Frame f) {
		SystemTray systemTray = SystemTray.get("SysTrayExample");
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }
        
        systemTray.setTooltip("Clock");
        systemTray.setImage(Main.class.getResource("pics/clock.png"));
        
        Menu mainMenu = systemTray.getMenu();
        
        MenuItem mResync = new MenuItem("Resync After Next Update", e->{
        	resync = RESYNC_INTERVAL;
        });
        mainMenu.add(mResync);
        
        MenuItem update = new MenuItem("Reload Position", e->{
        	f.setLocation();
        });
        mainMenu.add(update);
        
        MenuItem exit = new MenuItem("Exit", e->{
			System.out.println(timer.getState() + ", " + timer.getTime());
        	System.exit(0);
        });

        Menu intervallMenu = new Menu("Intervall");

        for(int i : intervalNumbers){
			if(i == 0){
				intervallMenu.add(new MenuItem("off", e -> { intervalTime = i; /*System.out.println("Interval off");*/ }));
			} else{
				intervallMenu.add(new MenuItem(i + " min", e -> { intervalTime = i; /*System.out.println("Interval " + j);*/ }));
			}
		}
        
        mainMenu.add(intervallMenu);

        Menu timerToggles = new Menu("Toggle/Stop Timer");
        
        MenuItem timerPause = new MenuItem("Toggle Timer", e -> {
        	if(timer.getState() == 1) timer.pause();
        	else timer.resume();
        });
        MenuItem timerStop = new MenuItem("Stop Timer", e -> {
        	timer.stop();
        	mainMenu.remove(timerToggles);
        	redraw();
        });
        
        Menu timerMenu = new Menu("Set Timer");
        
        
        
        for(int i : timerNumbers){
			timerMenu.add(new MenuItem(i + " min", e -> {
				System.out.println(timer.getState() + ", " + timer.getTime());
				if(timer.getTime() == -1) {
					timer.addTime(i*60);
					timerThread = new Thread(() -> {
						mainMenu.remove(exit);
						mainMenu.add(timerToggles);
						timerToggles.add(timerPause);
						timerToggles.add(timerStop);
						mainMenu.add(exit);
						f.setSize(f.getPicWidth(), f.getPicHeight()*2);
						timer.startTimer();
						f.setSize(f.getPicWidth(), f.getPicHeight());
						mainMenu.remove(timerToggles);
						if(timer.getState() == 1) {
							play(timerSoundFile);
							timer.stop();
						}
					});
					timerThread.start();
					System.out.println("new Timer started");
				} else timer.addTime(i*60);
				System.out.println(timer.getState() + ", " + timer.getTime() + "," + timerThread.isAlive());
			}));
		}
        mainMenu.add(timerMenu);
        
        Menu ColorMenu = new Menu("Adjust Colors");
        
        Menu TimerColor = new Menu("Timer");
        
        Menu ClockColor = new Menu("Clock");
        
        for(int i = 0; i < Colors.length; i++) {
        	Color j = Colors[i];
        	ClockColor.add(new MenuItem(ColorNames[i], e -> f.setClockColor(j)));
        	TimerColor.add(new MenuItem(ColorNames[i], e -> f.setTimerColor(j)));
        }
        
        ColorMenu.add(TimerColor);
        
        ColorMenu.add(ClockColor);
        
        mainMenu.add(ColorMenu);
        
        
        mainMenu.add(exit);
        
	}
	
	private static void play(String filename) {
		try(AudioInputStream ais = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(filename))){
			Clip clip = AudioSystem.getClip();
	        clip.open(ais);
	        clip.start();
	        Thread.sleep( 5 * clip.getMicrosecondLength() / 1000 );
	        clip.close();
		} catch (Exception exc) {
	        exc.printStackTrace(System.out);
		}
	}
}
