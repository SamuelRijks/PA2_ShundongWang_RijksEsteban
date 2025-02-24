package p2_BathroomSTRONGVersion;

import java.util.*;

import p0_BathroomCommon.Analyser;

public class StrongAnalyser implements Analyser {
	
	private static Random alea = new Random();
	
	private static final int ENTERING_BATH = 1;
	private static final int ACCESSING_ANTE = 2;
	private static final int RELIEVING = 3;
	private static final int LEAVING = 4;
	private static final int BAD_TYPE = -1000;
	
	private static final int WOMAN = 20;
	private static final int MAN = 10;
	private static final int NO_GENDER = -900;
	
	private volatile static int inAnteroom = 0;
	private volatile static int freeStalls = 4;
	private static boolean [] free = {true, true, true, true};
	private volatile static int currentGender = NO_GENDER;
	
	private static Queue<Integer> womenQueueForAnteroom = new LinkedList<Integer>();
	private static Queue<Integer> menQueueForAnteroom = new LinkedList<Integer>();
	private static Queue<Integer> peopleQueueForStall = new LinkedList<Integer>();
	
	public static void writeString (String string ) {
		int n;
		String s;
		StringBuffer sbuffer = new StringBuffer();
		for (int i=0; i<string.length(); i++) {
			//System.out.print(string.charAt(i));
			n = alea.nextInt(100);
			if (n>=95) try {Thread.sleep(1);} catch(Exception ex) {}
			else if (n>=75) Thread.yield();
			sbuffer.append(string.charAt(i));
		}
		s = sbuffer.toString();
		System.out.println("S: "+s);
		analyze(s);
	}
	
	private static void analyze (String s) {
		int type = determineType(s);
		int gender = determineGender(s);
		int id = getId(s);
		int stall;
		switch (type) {
			case StrongAnalyser.ENTERING_BATH: 
				if (gender==MAN) menQueueForAnteroom.add(id);
				else womenQueueForAnteroom.add(id);
				break;
			case StrongAnalyser.ACCESSING_ANTE: 
				if (currentGender==NO_GENDER) currentGender=gender;
				else if (gender!=currentGender) {
					System.err.println("Gender collision in anteroom");
					System.exit(1);
				}
				inAnteroom++;
				//----
				if (gender==MAN) {
					if (id!=menQueueForAnteroom.peek()) {
						System.err.println("MEN ORDER violation when accessing anteroom. Giving access to "+id+" expecting "+menQueueForAnteroom.peek());
						System.exit(1);
					}
					peopleQueueForStall.add(menQueueForAnteroom.remove());					
				}
				else {
					if (id!=womenQueueForAnteroom.peek()) {
						System.err.println("WOMEN ORDER violation when accessing anteroom. Giving access to "+id+" expecting "+womenQueueForAnteroom.peek());
						System.exit(1);
					}
					peopleQueueForStall.add(womenQueueForAnteroom.remove());	
				}
				break;
			case StrongAnalyser.RELIEVING:
				stall = getStall(s);
				if (freeStalls==0) {
					System.err.println("Stall assigned when all stalls busy");
					System.exit(1);
				}
				else if (!free[stall]) {
					System.err.println("Busy stall assigned "+stall);
					System.exit(1);
				}
				
				if (id!=peopleQueueForStall.peek()) {
					System.err.println("ORDER VIOLATION when getting a STALL. Giving access to "+id+" expecting "+peopleQueueForStall.peek());
					System.exit(1);
				}
				peopleQueueForStall.remove();
				
				freeStalls--;
				free[stall] = false;
				inAnteroom--;
				if (inAnteroom==0) currentGender=NO_GENDER;
				break;
			case StrongAnalyser.LEAVING: 
				stall = getStall(s);
				if (freeStalls==4 || free[stall]) {
					System.err.println("leaving a non-assigned (free) "+stall);
					System.exit(1);
				}
				freeStalls++;
				free[stall]=true;
				break;
		default: System.err.println("BAD or MISPLACED TRACE: "+s); System.exit(2); 
		}
	}
	
	private static int determineType (String s) {
		if (s.contains("bathroom")) return ENTERING_BATH;
		if (s.contains("anteroom")) return ACCESSING_ANTE;
		if (s.contains("TAKEN")) return RELIEVING;
		if (s.contains("LEAVING")) return LEAVING;
		return StrongAnalyser.BAD_TYPE;
	}
	
	private static String typeToString (int type) {
		switch (type) {
			case ENTERING_BATH: return "ENTERING BATHROOM";
			case ACCESSING_ANTE: return "ACCESSING ANTEROOM";
			case RELIEVING: return "RELIEVING BLADDER";
			case LEAVING: return "LEAVING STALL";
			default: return "bad type";
		}
	}
	
	private static int determineGender (String s) {
		if (s.contains("WOMAN")) return StrongAnalyser.WOMAN;
		if (s.contains("MAN")) return StrongAnalyser.MAN;
		return StrongAnalyser.NO_GENDER;
	}
	
	private static String genderToString (int gender) {
		switch (gender) {
			case WOMAN: return "WOMAN";
			case MAN: return "MAN";
			default: return "UNEXPECTED";
		}
	}
	
	private static int getId(String s) {
		int start = s.indexOf('(');
		int end = s.indexOf(')');
		return Integer.parseInt(s.substring(start+1, end));
	}
	
	private static int getStall (String s) {
		int start = s.indexOf('[');
		int end = s.indexOf(']');
		return Integer.parseInt(s.substring(start+1, end));
	}
} 