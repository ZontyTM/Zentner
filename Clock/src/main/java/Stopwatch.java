
public class Stopwatch {
	private int seconds = -1;
	private int toggled = -1;
	
	public int getTime() { return seconds; }
	public void restart() {seconds = 0;}
	public void addTime(int seconds) { this.seconds += seconds; }
	
	public String getFormattedTime() {
		if(seconds >= 3600) return (String.format("%02d:%02d", seconds / 3600, (int)((seconds % 3600) / 60)));
		else return (String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60)));
	}
	
	public int getState() { return toggled; }

	public void resume() { toggled = 1; }
	public void pause() { toggled = 0; }
	public void stop() { toggled = -1; }
	
	public void startStopwatch() {
		toggled = 1;
		
		while(toggled > -1) {
			while(toggled == 0) {
				try { Thread.sleep(1000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			Main.redraw();
			if(toggled == -1) break;
			try { Thread.sleep(1000); seconds++; }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		seconds = -1;
	}
}
