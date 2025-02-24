package p1_BathroomMILDVersion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import p0_BathroomCommon.*;

import java.lang.reflect.*;

public class MildMonitor implements BathroomMonitor {

	private Method writeMethod; // do not remove
	
	/* COMPLETE */
	
	public MildMonitor (Analyser an) {
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
	
	public void getFreeStall (Person p) {
		// BEWARE: invoke p.assignStall when a free stall for p has been found
		
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