package p3_QuintupletsCommon;

import java.util.Random;

public class QAnalyser {
	
	private static Random alea = new Random();
	
	private static final int READY = 0;
	private static final int ALL_PARK = 1;
	private static final int HAVING_FUN = 2;
	private static final int LEAVING = 3;
	private static final int ALL_PLAYED = 4;
	private static final int SNACK= 5;
	private static final int BAD_TYPE = -4578;
	
	private static boolean [] inPark = {false, false, false, false, false};
	private static volatile int inParkCount = 0;
	private static boolean [] havingFun = {false, false, false, false, false};
	private static volatile int hadFun = 0; 
	private static volatile int inTickCount = 0;
	
	private static volatile boolean firstSnack = true;
	
	public static synchronized void injectTrace (String s) {
		int n;
		StringBuffer sbuffer = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			//System.out.print(s.charAt(i));
			n = alea.nextInt(100);
			if (n>=95) try {Thread.sleep(1);} catch(Exception ex) {}
			else if (n>=75) Thread.yield();
			sbuffer.append(s.charAt(i));
		}
		
		s = sbuffer.toString();
		System.out.println(s);

		int id = getId(s);
		int type = determineType(s);
		switch (type) {
			case READY: 
				if (inPark[id]) {
					System.err.println("duplicate arrival in playground ["+id+"]");
					for (int i=0; i<5; i++) {System.err.println(inPark[i]);};
					System.exit(1);
				}
				inPark[id] = true;
				inParkCount ++;
				break;
			case ALL_PARK:
				if (inParkCount!=5) {
					System.err.println("Claiming all in playground when not all in playground");
					System.exit(1);
				}
				break;
			case HAVING_FUN:
				if (inParkCount!=5) {
					System.err.println("Cannot use tickadiddle when not all in playground");
					System.exit(1);
				}
				if (havingFun[id]) {
					System.err.println("duplicate use of tickaddidle ["+id+"]");
					System.exit(1);
				}
				havingFun[id] = true;
				hadFun++;
				inTickCount++;
				if (inTickCount>3) {
					System.err.println("Tickadiddle full ["+id+"] can't use it now");
					System.exit(1);
				}
				firstSnack = true;
				break;
			case LEAVING:
				if (!havingFun[id]) {
					System.err.println("["+id+"] can't leave the tickadiddle if not in it");
					System.exit(1);
				}
				havingFun[id]= false;
				inTickCount--;
				break;
			case ALL_PLAYED: 
				if (hadFun<5) {
					System.err.println("Not all quintuplets have played with the tickadiddle...");
					System.exit(1);
				}
				break;
			case SNACK: 
				if (firstSnack) {
					if (hadFun<5) {
						System.err.println("Not all quintuplets have played with the tickadiddle...");
						System.exit(1);
					}
					firstSnack = false;
				}
				// reset everything
				inPark[id]=false;
				inParkCount--;
				havingFun[id]=false;
				hadFun--;
				break;
			default: 
				System.err.println("Bad Trace "+s);
				break;
		}
	}
	
	private static int getId(String s) {
		int start = s.indexOf('[');
		int end = s.indexOf(']');
		return Integer.parseInt(s.substring(start+1, end));
	}
	
	private static int determineType (String s) {
		if (s.contains("ready")) return READY;
		if (s.contains("all")) return ALL_PARK;
		if (s.contains("fun")) return HAVING_FUN;
		if (s.contains("leave")) return LEAVING;
		if (s.contains("ALL")) return ALL_PLAYED;
		if (s.contains("snack")) return SNACK;
		return BAD_TYPE;
	}
	
} 