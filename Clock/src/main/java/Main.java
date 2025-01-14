import java.time.LocalTime;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
//import dorkbox.systemTray.Checkbox;
import java.awt.Color;


public class Main {
	
	private final static int RESYNC_INTERVAL = 60;
	private static int resync = 0;
	private static int monitor = -1;
	private static int intervalTime = 0;
	private static Mixer.Info audio = null;
	private static String intervalSoundFile = "pics/interval.wav";
	private static String timerSoundFile = "pics/timer.wav";
	private static int[] intervalNumbers = {0,1,2,5,10,15,30,60};
	private static float[] timerNumbers = {0.5f,1,2,5,10,15,20,30,60};
	public static Timer timer = new Timer();
	public static Stopwatch stopwatch = new Stopwatch();
	private static Thread timerThread;
	public static Boolean playTimer = false;
	public static Boolean currentTimer = false;
	private static Thread stopwatchThread;
	public static Boolean currentStopwatch = false;
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
		
		for(Mixer.Info info : AudioSystem.getMixerInfo()){
	        Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
	        if(lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(SourceDataLine.class)){
	        	audio = info;
                break;
            }
		}

		String file = "clock-bg";
		f = new Frame(file);

		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-old")) file += args[i];
			if(args[i].equals("-clock")) {
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].equals(ColorNames[j])) f.setClockColor(Colors[j]);
					else f.setClockColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);
				}
			}
			if(args[i].equals("-timer")) {
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].equals(ColorNames[j])) f.setTimerColor(Colors[j]);
					else f.setTimerColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);redraw();
				}
			}
			if(args[i].equals("-stopwatch")) {
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].equals(ColorNames[j])) f.setStopwatchColor(Colors[j]);
					else f.setStopwatchColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);
				}
			}
			if(args[i].equals("-monitor")) monitor = Integer.valueOf(args[i + 1]);
		}
		
		f.setLocation(monitor);

	    
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
        
        MenuItem update = new MenuItem("Switch Position", e->{
        	if(monitor == 0 || monitor == -1) { monitor = 1; f.setLocation(monitor); }
        	else { monitor = 0; f.setLocation(monitor); }
        });
        mainMenu.add(update);

        Menu ColorMenu = new Menu("Adjust Colors");
        
        Menu TimerColor = new Menu("Timer");
        
        Menu ClockColor = new Menu("Clock");
        
        for(int i = 0; i < Colors.length; i++) {
        	Color j = Colors[i];
        	ClockColor.add(new MenuItem(ColorNames[i], e -> { f.setClockColor(j); redraw(); }));
        	TimerColor.add(new MenuItem(ColorNames[i], e -> { f.setTimerColor(j); redraw(); }));
        }
        
        ColorMenu.add(TimerColor);
        
        ColorMenu.add(ClockColor);
        
        mainMenu.add(ColorMenu);
        
        MenuItem exit = new MenuItem("Exit", e->{
        	System.exit(0);
        });

        Menu intervallMenu = new Menu("Intervall");

        for(int i : intervalNumbers){
			if(i == 0){
				intervallMenu.add(new MenuItem("off", e -> { intervalTime = i; }));
			} else{
				intervallMenu.add(new MenuItem(i + " min", e -> { intervalTime = i; }));
			}
		}
        
        mainMenu.add(intervallMenu);

       
        
     
        
        
        Menu stopwatchToggles = new Menu("Toggle/Stop Stopwatch");
        Menu stopwatchMenu = new Menu("Stopwatch");

        MenuItem stopwatchPause = new MenuItem("Toggle Stopwatch", e -> {
        	if(stopwatch.getState() == 1) timer.pause();
        	else stopwatch.resume();
        });
        MenuItem stopwatchStop = new MenuItem("Stop Stopwatch", e -> {
        	stopwatch.stop();
        	mainMenu.remove(stopwatchToggles);
        	mainMenu.add(stopwatchMenu);
        	redraw();
        	currentStopwatch = false;
        });
        
        
        Menu timerToggles = new Menu("Toggle/Stop Timer");
        
        MenuItem timerPause = new MenuItem("Toggle Timer", e -> {
        	if(timer.getState() == 1) timer.pause();
        	else timer.resume();
        });
        MenuItem timerStop = new MenuItem("Stop Timer", e -> {
        	timer.stop();
        	mainMenu.remove(timerToggles);
        	redraw();
        	currentTimer = false;
        });
        
        Menu timerMenu = new Menu("Set Timer");
        
        
        
        for(float i : timerNumbers){
        	String name = (i < 1) ? (int)(60*i) + " sec" : (int)i + " min";
        	timerMenu.add(new MenuItem(name, e -> {
				if(timer.getTime() == -1) {
					currentTimer = true;
					timer.addTime((int)(i*60));
					timerThread = new Thread(() -> {
						mainMenu.remove(exit);
						mainMenu.add(timerToggles);
						timerToggles.add(timerPause);
						timerToggles.add(timerStop);
						mainMenu.add(exit);
						if(currentStopwatch) f.setSize(f.getPicWidth(), f.getPicHeight()*3);
		        		else f.setSize(f.getPicWidth(), f.getPicHeight()*2);
						timer.startTimer();
						mainMenu.remove(timerToggles);
						if(timer.getState() == 1) {
							playTimer = true;
							while(playTimer) play(timerSoundFile);
							timer.stop();
						}
						if(currentStopwatch) f.setSize(f.getPicWidth(), f.getPicHeight()*2);
		        		else f.setSize(f.getPicWidth(), f.getPicHeight());
						currentTimer = false;
					});
					timerThread.start();
				} else timer.addTime((int)(i*60));
			}));
		}
        
        
        MenuItem stopwatchStart = new MenuItem("Start Stopwatch", e -> {
        	stopwatchThread = new Thread(() -> {
        		currentStopwatch = true;
        		mainMenu.remove(exit);
        		if(timer.getState() == -1) mainMenu.remove(timerMenu);
        		else mainMenu.remove(timerToggles);
        		mainMenu.remove(stopwatchMenu);
        		mainMenu.add(stopwatchToggles);
        		stopwatchToggles.add(stopwatchPause);
        		stopwatchToggles.add(stopwatchStop);
        		if(timer.getState() == -1) mainMenu.add(timerMenu);
        		else mainMenu.add(timerToggles);
        		mainMenu.add(exit);
        		if(currentTimer) f.setSize(f.getPicWidth(), f.getPicHeight()*3);
        		else f.setSize(f.getPicWidth(), f.getPicHeight()*2);
        		stopwatch.startStopwatch();
        		if(currentTimer) f.setSize(f.getPicWidth(), f.getPicHeight()*2);
        		else f.setSize(f.getPicWidth(), f.getPicHeight());
        		mainMenu.remove(exit);
        		if(timer.getState() == -1) mainMenu.remove(timerMenu);
        		else mainMenu.remove(timerToggles);
        		mainMenu.remove(stopwatchToggles);
        		mainMenu.add(stopwatchMenu);
        		if(timer.getState() == -1) mainMenu.add(timerMenu);
        		else mainMenu.add(timerToggles);
        		mainMenu.add(exit);
        		currentStopwatch = false;
        	});
        	stopwatchThread.start();
        });
        
        mainMenu.add(stopwatchMenu);
        
        stopwatchMenu.add(stopwatchStart);

        mainMenu.add(timerMenu);

        mainMenu.add(exit);
        
        
        System.out.println("Tray Done");
        (new Thread(() -> play(intervalSoundFile))).start();
	}
	
	private static void play(String filename) {
		try(AudioInputStream ais = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(filename))){
			Clip clip = AudioSystem.getClip(audio);
	        clip.open(ais);
	        clip.start();
	        Thread.sleep( (10000 - clip.getMicrosecondLength() / 1000) );
	        clip.close();
			System.out.println("test");
		} catch (Exception exc) {
	        exc.printStackTrace(System.out);
		}
	}
	
	public static void stopPlayTimer() { playTimer = false; }
	
	public static int getMonitor() { return monitor; }
}
