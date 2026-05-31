import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

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

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Timestamp;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class Main {
	
	private final static int RESYNC_INTERVAL = 60;
	private static int resync = 0;
	private static int monitor = -1;
	public static int alarmHour = -1;
	public static int alarmMinute = -1;
	private static int[] alarmHourNumbers = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
	private static int[] alarmMinuteNumbers = {1,2,5,10,15,20,25,30,35,40,45,50,55};
	public static Boolean playAlarm = false;
	private static int intervalTime = 0;
	private static int intOffset = 0;
	private static Boolean intervalOffset = false;
	private static Mixer.Info audio = null;
	private static String intervalSoundFile = "pics/interval.wav";
	private static String timerSoundFile = "pics/timer.wav";
	private static int[] intervalNumbers = {0,1,2,5,10,15,30,60};
	private static float[] timerNumbers = {0.5f,1,2,5,10,15,20,25,30,35,40,45,50,55,60,180};
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
										new Color(255, 255, 255, 220),
										new Color(227, 99, 29, 220),
										new Color(37, 100, 157, 220),
										Color.RED,
										Color.CYAN,
										Color.GREEN,
										Color.YELLOW,
										Color.MAGENTA
	};
	
	
	static MenuItem stopwatchStop = new MenuItem(), stopwatchStart = new MenuItem(), timerStop = new MenuItem(), timerPause = new MenuItem(), stopwatchPause = new MenuItem();
	static Menu timerStart = new Menu("SetTimer");
	
	private static Frame f;
	
	public static void main(String[] args) {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {		//For SaveFile creation on being shutdown without pressing exit
	        public void run() {
	            saveFile();
	        }
	    }, "Shutdown-thread"));
		
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
		
		for(int i = 0; i < args.length; i++) {System.out.println(i + ", '" + args[i] + "'");}
		
		for(int i = 0; i < args.length; i++) {
			
			if(args[i].toLowerCase().equals("-old")) { file += args[i]; i++; }
			
			if(args[i].toLowerCase().equals("-intervaloffset") || args[i].equals("-intervalloffset")) { intervalOffset = true; i++; }
			
			if(args[i].toLowerCase().equals("-intervaltime") || args[i].equals("-intervalltime")) { intervalTime = Integer.valueOf(args[i+1]); i++; }
			
			if(args[i].toLowerCase().equals("-clock")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setClockColor(Colors[j]); findColor = true;}
				}
				if(!findColor) { f.setClockColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255);redraw(); }
				i++;
			}
			if(args[i].toLowerCase().equals("-timer")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setTimerColor(Colors[j]); findColor = true; }
				}
				if(!findColor) { f.setTimerColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255); redraw(); }
				i++;
			}
			if(args[i].toLowerCase().equals("-stopwatch")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setStopwatchColor(Colors[j]); findColor = true;}
				}
				if(!findColor) { f.setStopwatchColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255); redraw(); }
				i++;
			}
			if(args[i].toLowerCase().equals("-alarm")) {
				Boolean findColor = false;
				for(int j = 0; j < ColorNames.length; j++) {
					if(args[i+1].toLowerCase().equals(ColorNames[j])) { f.setAlarmColor(Colors[j]); findColor = true;}
				}
				if(!findColor) { f.setAlarmColor(Integer.valueOf(args[i+1]), Integer.valueOf(args[i+2]), Integer.valueOf(args[i+3]), 255); redraw(); }
				i++;
			}
			if(args[i].toLowerCase().equals("-monitor")) { monitor = Integer.valueOf(args[i + 1]); i++; }
			
			if(args[i].toLowerCase().equals("-verticaloffset")) { f.verticalOffset = Integer.valueOf(args[i+1]); i++; }
			
			if(args[i].toLowerCase().equals("-font")) { System.out.println(args[i + 1]); f.fontName = (args[i + 1]).replace('_', ' '); i++; }
			
			if(args[i].toLowerCase().equals("-fontsize")) { System.out.println(args[i + 1]); f.fontSize = Integer.valueOf(args[i + 1]); i++; }
			
		}		
		
		f.setLocation(monitor);

		(new Thread(() -> Tray(f))).start();
		
		System.out.println(readFile());
		
		sync();
	    

		while (true) {
	    	redraw();
	    	
	    	resync++;
	    	if(resync >= RESYNC_INTERVAL) {
	    		resync = 0;
	    	    sync();
	    	} else {
	    		//if(LocalTime.now().getHour() == 23 && LocalTime.now().getMinute() % 2 == 0) { play(intervalSoundFile); play(intervalSoundFile); }
	    		//if(LocalTime.now().getHour() == 22 && LocalTime.now().getMinute() % 2 == 0) { play(intervalSoundFile); play(intervalSoundFile); }
	    		if(LocalTime.now().getHour() == alarmHour && LocalTime.now().getMinute() == alarmMinute) { playAlarm = true; (new Thread(() -> { while(playAlarm) { play(timerSoundFile); } alarmHour = -1; alarmMinute = -1; redraw(); } )).start(); };
	    		if(intervalTime != 0 && (((intervalOffset) ? (intOffset % intervalTime) : (0)) == LocalTime.now().getMinute() % intervalTime))
	    			(new Thread(() -> play(intervalSoundFile))).start();
		    	try { Thread.sleep(60000);
		    	} catch (InterruptedException e) { e.printStackTrace(); }
	    	}
		}
	    
	}
	
	public static void redraw() { f.setFrameSize(); f.setText(); }
	
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
        
        Menu AlarmColor = new Menu("Alarm");
        
        Menu StopwatchColor = new Menu("Stopwatch");
        
        for(int i = 0; i < Colors.length; i++) {
        	Color j = Colors[i];
        	ClockColor.add(new MenuItem(ColorNames[i], e -> { f.setClockColor(j); redraw(); }));
        	TimerColor.add(new MenuItem(ColorNames[i], e -> { f.setTimerColor(j); redraw(); }));
        	StopwatchColor.add(new MenuItem(ColorNames[i], e -> { f.setStopwatchColor(j); redraw(); }));
        	AlarmColor.add(new MenuItem(ColorNames[i], e -> { f.setAlarmColor(j); redraw(); }));
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

        
        Menu alarmMenu = new Menu("Alarm");
        
        Menu alarmHourMenu = new Menu("Hour");
        Menu alarmMinuteMenu = new Menu("Minute");
        MenuItem stopAlarm = new MenuItem("Stop Alarm", e -> {
        	if(playAlarm) playAlarm = false;
        	alarmHour = -1;
        	alarmMinute = -1;
        	redraw();
        });
        
        for(float i = 0; i < alarmHourNumbers.length; i++) {
        	int j = (int) i;
        	alarmHourMenu.add(new MenuItem(alarmHourNumbers[j] + " h", e -> { 
        		if(alarmHour == -1) { alarmHour = alarmHourNumbers[j]; alarmMinute = 0;}
        		else alarmHour += alarmHourNumbers[j];
        		
        		if(alarmMinute == -1) alarmMinute = 0;
        		if(alarmHour >= 24) { alarmHour -= 24; }
        		redraw();
        	} ));
        }
        
        for(float i = 0; i < alarmMinuteNumbers.length; i++) {
        	int j = (int) i;
        	alarmMinuteMenu.add(new MenuItem(alarmMinuteNumbers[j] + " min", e -> { 
        		if(alarmMinute == -1) { alarmMinute = alarmMinuteNumbers[j]; alarmHour = 0; }
        		else alarmMinute += alarmMinuteNumbers[j];
        		
        		if(alarmHour == -1) alarmHour = 0;
        		if(alarmMinute >= 60) { alarmMinute -= 60; alarmHour++; }
        		redraw();
        	} ));
        }
        
        

        alarmMenu.add(alarmHourMenu);
        alarmMenu.add(alarmMinuteMenu);
        alarmMenu.add(stopAlarm);
        
        mainMenu.add(alarmMenu);
        
        
        
        Menu stopwatchMenu = new Menu("Stopwatch");
        
        
        mainMenu.add(stopwatchMenu);
        
        
        stopwatchPause = new MenuItem("Toggle Stopwatch", e -> {
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
            	redraw();
        		stopwatch.startStopwatch(0);
            	redraw();
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

        
        timerPause = new MenuItem("Toggle Timer", e -> {
        	if(timer.getState() == 1) timer.pause();
        	else timer.resume();
        });
        timerStop = new MenuItem("Stop Timer", e -> {
        	timer.stop();
        	timerPause.setEnabled(false);
        	timerStop.setEnabled(false);
        	timerStart.setEnabled(true);
        	currentTimer = false;
        	redraw();
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
			        	redraw();
						timer.startTimer();
			        	timerPause.setEnabled(false);
			        	timerStop.setEnabled(false);
						if(timer.getState() == 1) {
							playTimer = true;
							while(playTimer) play(timerSoundFile);
							timer.stop();
						}
						currentTimer = false;
						redraw();
					});
					timerThread.start();
				} else timer.addTime((int)(i*60));
			}));
		}
        
        mainMenu.add(timerMenu);

        mainMenu.add(exit);
        
        
        System.out.println("Tray Done");
        //(new Thread(() -> play(intervalSoundFile))).start();
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
	
	public static Boolean saveFile() {
		try {
		      FileWriter myWriter = new FileWriter("SaveFile.txt");
		      myWriter.write(
		    		  		LocalTime.now().toSecondOfDay() + "\n" + "\n" +
		    		  		((Main.currentTimer) ? 1 : 0) + "\n" + Main.timer.getTime() + "\n" + "\n" +
		    		  		((Main.currentStopwatch) ? 1 : 0) + "\n" + Main.stopwatch.getTime() + "\n" + "\n" +
		    		  		((Main.alarmHour != -1) ? 1 : 0) + "\n" + Main.alarmHour + "\n" + Main.alarmMinute
		    		  		);
		      
		      System.out.println(((Main.alarmHour != -1) ? 1 : 0) + "\n" + Main.alarmHour + "\n" + Main.alarmMinute);
		      
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		      return true;
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		      return false;
		    }
	}
	
	public static Boolean readFile() {
		try {
			File myObj = new File("SaveFile.txt");
		    Scanner myReader = new Scanner(myObj);
		    
	        int[] time = getTime(myReader.nextLine(), LocalTime.now().toSecondOfDay());
	        
	        
		    int[] lines = new int[7];
		    myReader.nextLine();
		    for(int i = 0; i < lines.length; i++) {
		    	if(myReader.hasNextInt()) {
		    		lines[i] = myReader.nextInt();
		    	} else lines[i] = 0;
		    }
		    
		    if(lines[0] == 1) {
		    	int secs = 0;
		    	if(lines[1] > time[3]) secs = lines[1] - time[3];
		    	else secs = 1;
		    	currentTimer = true;
				timer.addTime(secs);
				timerThread = new Thread(() -> {
		        	timerPause.setEnabled(true);
		        	timerStop.setEnabled(true);
		        	redraw();
					timer.startTimer();
		        	timerPause.setEnabled(false);
		        	timerStop.setEnabled(false);
					if(timer.getState() == 1) {
						playTimer = true;
						while(playTimer) play(timerSoundFile);
						timer.stop();
					}
					currentTimer = false;
					redraw();
				});
				timerThread.start();
		    }
		    if(lines[2] == 1) {
		    	stopwatchThread = new Thread(() -> {
	        		currentStopwatch = true;
	            	stopwatchPause.setEnabled(true);
	            	stopwatchStop.setEnabled(true);
	            	stopwatchStart.setEnabled(false);
	            	redraw();
	        		stopwatch.startStopwatch(time[3] + lines[3]);
	            	redraw();
	            	stopwatchPause.setEnabled(false);
	            	stopwatchStop.setEnabled(false);
	            	stopwatchStart.setEnabled(true);
	        		currentStopwatch = false;
	        	});
	        	stopwatchThread.start();
		    }
		    if(lines[4] == 1) {
		    	alarmHour = lines[5];
		    	alarmMinute = lines[6];
		    	f.paintAlarm(f.getGraphics());
		    }

		    
		    myReader.close();
		    return true;
	    } catch (FileNotFoundException e) {
	    	System.out.println("An error occurred.");
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public static int[] getTime(String dateString, int date2) {
		int[] time = {0,0,0,0};
		int date1 = Integer.valueOf(dateString);
		
		
		time[0] = ((date2 - date1) / 3600) % 24;
		time[1] = ((date2 - date1) / 60) % 60 ;
		time[2] = (date2 - date1) % 60;	
		time[3] = (date2 - date1 > 0) ? (date2 - date1) : (date2 + 86400 - date1);
		
		if(date2 < date1) {
			if(time[2] < 0) { time[2] = 60 + time[2]; time[1]--; }
			if(time[1] < 0) { time[1] = 60 + time[1]; time[0]--; }
			if(time[0] < 0) time[0] = 24 + time[0];
		}

		
		return time;
	}
	
	
	
	public static void stopPlayTimer() { playTimer = false; }
	
	public static void stopPlayAlarm() { playAlarm = false; }
	
	public static int getMonitor() { return monitor; }
}
