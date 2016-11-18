package dpm.launcher;

import dpm.repository.Repository;
import dpm.util.DPMConstants;
import dpm.util.Printer;
import lejos.hardware.Button;
//import lejos.hardware.Sound;

import java.util.HashMap;

/**
 * Class that executes the tasks required in the beta demo
 * <br>Gets Wifi data and calculates coordinates of green zone
 * <br>Starts the preliminary block search algorithm
 * <br>Once a block is found, travels to green zone and drops it
 * @author Emile Traor�
 *
 */
public class BetaDemo implements Launcher, DPMConstants{
	private int[] greenZone, redZone;
	private int startingCorner, role;
	
	public static void main (String[] args){
		(new Thread() {
			public void run() {
				while (Button.waitForAnyPress() != Button.ID_ESCAPE){}
			System.exit(0);
			}
		}).start();
		
		Launcher launcher = new BetaDemo();
		
		while(launcher.getRole() == NO_ROLE){
			launcher = new BetaDemo();
		}
		
		Printer.getInstance().display("Ready");
		
		Repository.launch(launcher);
		System.out.println(""+(Repository.getGreenZone()[0] + Repository.getGreenZone()[2])/2 + "\n" +
				(Repository.getGreenZone()[1] + Repository.getGreenZone()[3])/2);
		Repository.localize();
		Repository.search();
		Repository.doFinalization();
		
		(new Thread() {
			public void run() {
				while (!Repository.clawIsEmpty()){
					try{Thread.sleep(1000l);}catch (Exception e){}
				}
				Repository.doFinalization();
			}
		}).start();
	}
	
	private BetaDemo() {
		
		HashMap<String, Integer> wifiData = new WifiConnect().ConnectWifi();
		
		try{
			greenZone = new int[] {wifiData.get("LGZx"), wifiData.get("UGZy"), wifiData.get("UGZx"), wifiData.get("LGZy")};
			redZone = new int[] {wifiData.get("LRZx"), wifiData.get("URZy"), wifiData.get("URZx"), wifiData.get("LRZy")};
			startingCorner = wifiData.get("BSC");
			
			if(wifiData.get("BTN") == TEAM_NUMBER)
				role = BUILDER;
			else if(wifiData.get("CTN") == TEAM_NUMBER)
				role = BUILDER;
			else
				role = NO_ROLE;
		}
		catch (Exception e){
			System.exit(0);
		}
	}
	
	
	@Override
	public int[] getGreenZone() {
		return greenZone;
	}

	@Override
	public int[] getRedZone() {
		return redZone;
	}

	@Override
	public int getStartZone() {
		return startingCorner;
	}

	@Override
	public int getRole() {
		return role;
	}
}