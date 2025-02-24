package p5_QuintupletsAlternative;


import java.util.concurrent.Semaphore;
// import java.util.concurrent.?????;

import p3_QuintupletsCommon.*;

public class QASynchronizer implements QuintupletSynchronizer{
	
	/* COMPLETE */
	
	public void goPlayground (Quintuplet q) {
		QAnalyser.injectTrace(q+" is in the playground ready to play when all together");
		/* COMPLETE from this point. Previous line must be the first one */
	}
	
	public void startPlaying (Quintuplet q) {
		/* COMPLETE */
		
		
		QAnalyser.injectTrace(q+" is having fun with the tickadiddle"); // this line must be the very last
	}
	
	public void endPlaying (Quintuplet q) {
		QAnalyser.injectTrace(q+" is about to leave the tickadiddle");
		/* COMPLETE from this point. Previous line must be the first one */
	}
	
	public void goHomeForASnack (Quintuplet q) {
		/* COMPLETE */
		
		QAnalyser.injectTrace(q+" is having a snack at home"); // this line must be the very last
	}
}
