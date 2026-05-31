import java.time.LocalTime;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import dorkbox.systemTray.Checkbox;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
//import dorkbox.systemTray.Checkbox;
import java.awt.Color;
import java.awt.event.ActionListener;


public class Main {
	
	private final static int RESYNC_INTERVAL = 60;
	private static int resync = 0;
	private static int monitor = -1;
	private static int intervalTime = 0;
	private static int intOffset = 0;
	private static Boolean intervalOffset = false;
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
										"white",
										"orange",
										"blue",
										"red",
										"cyan",
										"green",
										"yellow",
										"magenta"
	};
	private static Color[] Colors = {
										new Color(255, 255, 255, 255),
										new Color(255, 99, 27, 255),
										new Color(37, 100, 157, 255),
										Color.RED,
										Color.CYAN,
										Color.GREEN,
										Color.YELLOW,
										Color.MAGENTA
	};
	
	static MenuItem stopwatchStop = new MenuItem(), stopwatchStart = new MenuItem(), timerStop = new MenuItem();
	
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

		for(int i = 1; i < args.length; i++) {
			if(args[i].toLowerCase().equals("-old")) { file += args[i]; i++; }
			
			if(args[i].toLowerCase().equals("-intervaloffset") || args[i].equals("-intervalloffset")) { intervalOffset = true; i++; }
			
			if(args[i].toLowerCase().equals("-intervaltime") || args[i].equals("-intervalltime")) { intervalTime = Integer.valueOf(args[i+1]); System.out.println(i + ", " + args[i+1]); i++; }
			
			if(args[i].toLowerCase().equals("-clock")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setClockColor(Colors[j]); findColor = true; i++;}
				}
				if(!findColor) { f.setClockColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);redraw(); i++; }
				else i++;
			}
			if(args[i].toLowerCase().equals("-timer")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setTimerColor(Colors[j]); findColor = true; i++;}
				}
				if(!findColor) { f.setTimerColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);redraw(); i++; }
				else i++;
			}
			if(args[i].toLowerCase().equals("-stopwatch")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setStopwatchColor(Colors[j]); findColor = true; i++;}
				}
				if(!findColor) { f.setStopwatchColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);redraw(); i++; }
				else i++;
			}
			if(args[i].toLowerCase().equals("-monitor")) { monitor = Integer.valueOf(args[i + 1]); i++; }
		}
		
		f.setLocation(monitor);

	    
		(new Thread(() -> Tray(f))).start();
		System.out.flush();
		sync();
	    
	    while (true) {
		    f.setText();
	    	
	    	resync++;
	    	if(resync >= RESYNC_INTERVAL) {
	    		resync = 0;
	    	    sync();
	    	} else {
	    		//if(LocalTime.now().getHour() == 23 && LocalTime.now().getMinute() == 0) while(true) play(timerSoundFile);
	    		//if(LocalTime.now().getHour() == 22 && LocalTime.now().getMinute() == 30) { int i = 0; while( i < 5) { play(intervalSoundFile); i++; } }
	    		if(intervalTime != 0 && (((intervalOffset) ? (intOffset % intervalTime) : (0)) == LocalTime.now().getMinute() % intervalTime))
	    			(new Thread(() -> play(intervalSoundFile))).start();
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
        
        Menu customize = new Menu("Customize");
        
        mainMenu.add(customize);
        
        MenuItem mResync = new MenuItem("Resync After Next Update", e->{
        	resync = RESYNC_INTERVAL;
        });
        customize.add(mResync);
        
        MenuItem update = new MenuItem("Switch Position", e->{
        	if(monitor == 0 || monitor == -1) { monitor = 1; f.setLocation(monitor); }
        	else { monitor = 0; f.setLocation(monitor); }
        });
        customize.add(update);

        Menu ColorMenu = new Menu("Adjust Colors");
        
        Menu TimerColor = new Menu("Timer");
        
        Menu ClockColor = new Menu("Clock");
        
        Menu StopwatchColor = new Menu("Stopwatch");
        
        for(int i = 0; i < Colors.length; i++) {
        	Color j = Colors[i];
        	ClockColor.add(new MenuItem(ColorNames[i], e -> { f.setClockColor(j); redraw(); }));
        	TimerColor.add(new MenuItem(ColorNames[i], e -> { f.setTimerColor(j); redraw(); }));
        	StopwatchColor.add(new MenuItem(ColorNames[i], e -> { f.setStopwatchColor(j); redraw(); }));
        }
        
        ColorMenu.add(TimerColor);
        
        ColorMenu.add(ClockColor);
        
        ColorMenu.add(StopwatchColor);
        
        customize.add(ColorMenu);
        
        MenuItem exit = new MenuItem("Exit", e->{
        	System.exit(0);
        });

        
        Menu intervallMenu = new Menu("Intervall");
        
        for(int i : intervalNumbers){
			if(i == 0){
				intervallMenu.add(new MenuItem("off", e -> { intervalTime = i; }));
			} else{
				intervallMenu.add(new MenuItem(i + " min", e -> { intervalTime = i; intOffset = LocalTime.now().getMinute(); }));
			}
		}
        
        Checkbox intervallOffset = new Checkbox("Interval Offset", e -> {
        	intervalOffset = (intervalOffset) ? false : true;
        });
        intervallMenu.add(intervallOffset);
        
        if(intervalOffset) intervallOffset.setChecked(true);
        
        mainMenu.add(intervallMenu);

       
        Menu stopwatchMenu = new Menu("Stopwatch");
        
        
        mainMenu.add(stopwatchMenu);
        
        
        MenuItem stopwatchPause = new MenuItem("Toggle Stopwatch", e -> {
        	if(stopwatch.getState() == 1) timer.pause();
        	else stopwatch.resume();
        });
        
        stopwatchStop = new MenuItem("Stop Stopwatch", e -> {
        	stopwatch.stop();
        	stopwatchPause.setEnabled(false);
        	stopwatchStop.setEnabled(false);
        	stopwatchStart.setEnabled(true);
        	redraw();
        	currentStopwatch = false;
        });
        
        stopwatchStart = new MenuItem("Start Stopwatch", e -> {
        	stopwatchThread = new Thread(() -> {
        		currentStopwatch = true;
            	stopwatchPause.setEnabled(true);
            	stopwatchStop.setEnabled(true);
            	stopwatchStart.setEnabled(false);
        		if(currentTimer) f.setSize(f.getPicWidth(), f.getPicHeight()*3);
        		else f.setSize(f.getPicWidth(), f.getPicHeight()*2);
        		stopwatch.startStopwatch();
        		if(currentTimer) f.setSize(f.getPicWidth(), f.getPicHeight()*2);
        		else f.setSize(f.getPicWidth(), f.getPicHeight());
            	stopwatchPause.setEnabled(false);
            	stopwatchStop.setEnabled(false);
            	stopwatchStart.setEnabled(true);
        		currentStopwatch = false;
        	});
        	stopwatchThread.start();
        });
        
        stopwatchMenu.add(stopwatchPause);
        stopwatchMenu.add(stopwatchStop);
        stopwatchMenu.add(stopwatchStart);

        Menu timerStart = new Menu("SetTimer");
        
        MenuItem timerPause = new MenuItem("Toggle Timer", e -> {
        	if(timer.getState() == 1) timer.pause();
        	else timer.resume();
        });
        timerStop = new MenuItem("Stop Timer", e -> {
        	timer.stop();
        	timerPause.setEnabled(false);
        	timerStop.setEnabled(false);
        	timerStart.setEnabled(true);
        	redraw();
        	currentTimer = false;
        });
		
        Menu timerMenu = new Menu("Timer");
        timerMenu.add(timerPause);
        timerMenu.add(timerStop);
        timerMenu.add(timerStart);
        
        
        for(float i : timerNumbers){
        	String name = (i < 1) ? (int)(60*i) + " sec" : (int)i + " min";
        	timerStart.add(new MenuItem(name, e -> {
				if(timer.getTime() == -1) {
					currentTimer = true;
					timer.addTime((int)(i*60));
					timerThread = new Thread(() -> {
			        	timerPause.setEnabled(true);
			        	timerStop.setEnabled(true);
						if(currentStopwatch) f.setSize(f.getPicWidth(), f.getPicHeight()*3);
		        		else f.setSize(f.getPicWidth(), f.getPicHeight()*2);
						timer.startTimer();
			        	timerPause.setEnabled(false);
			        	timerStop.setEnabled(false);
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
        
        mainMenu.add(timerMenu);

        mainMenu.add(exit);
        
        
        //System.out.println("Tray Done");
        (new Thread(() -> play(intervalSoundFile))).start();
	}
	
	private static void play(String filename) {
		try(AudioInputStream ais = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(filename))){
			Clip clip = AudioSystem.getClip(audio);
	        clip.open(ais);
	        clip.start();
	        Thread.sleep( (10000 - clip.getMicrosecondLength() / 1000) );
	        clip.close();
		} catch (Exception exc) {
	        exc.printStackTrace(System.out);
		}
	}
	
	public static void stopPlayTimer() { playTimer = false; }
	
	public static int getMonitor() { return monitor; }
}
