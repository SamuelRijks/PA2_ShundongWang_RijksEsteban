package p0_BathroomCommon;

public class Person extends Thread {

	private int pid;  // Person id.
	private Gender gender;
	private int stallNum;
	private int ticket;
	
	private BathroomMonitor monitor;
	
	public Person (int id, Gender gender, BathroomMonitor monitor) {
		this.gender = gender;
		this.pid = id;
		this.monitor = monitor;
	}
	
	public int getPid ()  {return this.pid;}
	public Gender getGender () {return this.gender;}
	
	public void assignStall (int stallNum) {
		this.stallNum = stallNum;
		// System.out.println("+++ stall "+stallNum+" assigned to "+this);
	}
	
	public int getAssignedStall () {
		return this.stallNum;
	}
	
	public void giveTicket (int ticket) {
		this.ticket = ticket;
	}
	
	public int getTicket () {
		return this.ticket;
	}
	
	
	public void run () {
		while (true) {
			try {Thread.sleep(125+(int)(5000*Math.random()));} catch(InterruptedException ie) {}
			monitor.enter(this);
			monitor.accessAnteroom(this);
			monitor.getFreeStall(this);
			try {Thread.sleep(125+(int)(250*Math.random()));} catch(InterruptedException ie) {}
			System.out.println("\t\t *** NOW!!! *** ("+this+" in Stall "+stallNum+")");
			try {Thread.sleep(125+(int)(125*Math.random()));} catch(InterruptedException ie) {}
			monitor.exit(this);
		}
	}
	
	public String toString () {
		return (gender==Gender.MAN ? "MAN" : "WOMAN")+"("+pid+")";
	}
}


