package p2_BathroomSTRONGVersion;

import java.lang.reflect.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import p0_BathroomCommon.*;


public class StrongMonitor implements BathroomMonitor {

	Method writeMethod; // do not remove
	
	/* COMPLETE */
	
	public StrongMonitor (Analyser an) {
		try {writeMethod = an.getClass().getMethod("writeString", String.class);}
		catch(Exception ex) {System.exit(1);}
		
		/* COMPLETE */
	}
	
	private void injectTrace (String s) {
		try { writeMethod.invoke(null, s);} catch(Exception ex) {System.exit(1);}
	}
	
	public void enter(Person p) {
		/* COMPLETE */	
		
		injectTrace("--> ENTERING bathroom "+p);
		
		/* COMPLETE if needed*/	
	}

	public void accessAnteroom (Person p) {
		/* COMPLETE if needed*/	
		
		injectTrace("\t--> ACCESSING the anteroom "+p);
		
		/* COMPLETE if needed*/	
	}
	
	public void getFreeStall(Person p) {
		/* COMPLETE if needed*/	
		
		injectTrace("\t\t "+p+" HAS TAKEN Stall ["+stallNum+"]");
		
		/* COMPLETE if needed*/	
	}
	
	public void exit(Person p) {
		/* COMPLETE if needed*/	
		
		injectTrace("<** LEAVING stall ["+p.getAssignedStall()+"] "+p);
		
		/* COMPLETE if needed*/	
	}
	
	

}