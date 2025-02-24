package p5_QuintupletsAlternative;

import java.util.Scanner;
import p3_QuintupletsCommon.*;

public class QALauncher {
	public static void main (String [] args) {
		Scanner scanner = new Scanner(System.in);
		QuintupletSynchronizer sync = new QASynchronizer();
		
		Quintuplet mia = new Quintuplet("MIA", sync);
		Quintuplet mason = new Quintuplet("MASON", sync);
		Quintuplet mila = new Quintuplet("MILA", sync);
		Quintuplet max = new Quintuplet("MAX", sync);
		Quintuplet maya = new Quintuplet("MAYA", sync);
		
		System.out.println("\nLaunching the Quintuplets. ALTERNATIVE version");
		System.out.print("Enter to proceed, and enter again to terminate ");
		scanner.nextLine();
		System.out.println();
		
		mia.start(); 
		mason.start();
		mila.start();
		max.start();
		maya.start();
		
		scanner.nextLine();
		scanner.close();
		System.exit(0);
	}
}
