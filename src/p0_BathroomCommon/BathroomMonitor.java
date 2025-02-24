package p0_BathroomCommon;

public interface BathroomMonitor {
	
	public void enter (Person p);
	public void accessAnteroom (Person p);
	public void getFreeStall(Person p);
	public void exit (Person p);
}