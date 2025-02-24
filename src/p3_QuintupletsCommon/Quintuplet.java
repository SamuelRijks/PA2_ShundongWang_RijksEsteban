package p3_QuintupletsCommon;

public class Quintuplet extends Thread {
	
	private static int count = 0;
	
	private String name;
	private QuintupletSynchronizer sync;
	private int id;
	
	public Quintuplet (String name, QuintupletSynchronizer sync) {
		this.name = name;
		this.sync = sync;
		this.id = count++;
	}
	
	public void run () {
		while (true) {
			sync.goPlayground(this);
			sync.startPlaying(this);
			// now playing with the tickadiddle
			try {Thread.sleep(1+(int)(5*Math.random()));} catch(InterruptedException ie) {}
			sync.endPlaying(this);
			sync.goHomeForASnack(this);
		}
	}
	
	public String toString () {
		return this.name+"["+id+"]";
	}
}
