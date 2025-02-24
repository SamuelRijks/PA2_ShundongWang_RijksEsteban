package p1_BathroomMILDVersion;

import java.util.Scanner;

import p0_BathroomCommon.Gender;
import p0_BathroomCommon.Person;

public class LauncherMILD {
	
	public static void main (String [] args) {
		Scanner scanner = new Scanner(System.in);
		
		final int MEN = 20;
		final int WOMEN = 20;
		
		System.out.println("\nOpening the bathroom. MILD version");
		System.out.print("Enter to proceed, and enter again to terminate ");
		scanner.nextLine();
		System.out.println();
		
		MildMonitor monitor = new MildMonitor(new MildAnalyser());
		
		Person men [] = new Person [MEN];
		Person women [] = new Person [WOMEN];
		
		for (int i=0; i<men.length; i++) {
			men[i] = new Person(i, Gender.MAN, monitor);
		}
		for (int i=0; i<women.length; i++) {
			women[i] = new Person(i, Gender.WOMAN, monitor);
		}
		
		for (int i=0; i<men.length; i++) {
			men[i].start();
		}
		for (int 
			i=0; i<women.length; i++) {
			women[i].start();
		}
		
		scanner.nextLine();
		scanner.close();
		System.exit(0);
		
 	}

}