package server;
//References:
//https://systembash.com/a-simple-java-udp-server-and-udp-client/
//https://www.geeksforgeeks.org/multithreading-in-java/
//https://www.geeksforgeeks.org/synchronized-in-java/
//https://objectcomputing.com/resources/publications/sett/january-2002-corba-and-java-by-don-busch-principal-software-engineer
//http://www.ejbtutorial.com/corba/tutorial-for-corba-hello-world-using-java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CORBA.ORB;

import controller.Controller;
import DPSSCorba.DPSSInterfaceHelper;
import DPSSCorba.DPSSInterface;
/**
 *
 * @author ypandya
 */
public class GameServer {
	
	/**
	 * This is the GameServer class
	 */
	
	/**
	 * main method to run all the servers
	 * @param args args for main function
	 */
	public static void main(String args[]) {
		
		buildLogDirectory("./logs");
		
		try {
			
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			Controller europe = new Controller("EU");
			europe.setORB(orb);
			org.omg.CORBA.Object refEU = rootpoa.servant_to_reference(europe);
			DPSSInterface hrefEU = DPSSInterfaceHelper.narrow(refEU);
			NamingContextExt ncRefEU = NamingContextExtHelper.narrow(objRef);		
            NameComponent pathEU[] = ncRefEU.to_name("EU");
            ncRefEU.rebind(pathEU, hrefEU);
            
    		Controller northamerica = new Controller("NA");
    		northamerica.setORB(orb);
			org.omg.CORBA.Object refNA = rootpoa.servant_to_reference(northamerica);
			DPSSInterface hrefNA = DPSSInterfaceHelper.narrow(refNA);
			NamingContextExt ncRefNA = NamingContextExtHelper.narrow(objRef);		
            NameComponent pathNA[] = ncRefNA.to_name("NA");
            ncRefNA.rebind(pathNA, hrefNA);
            
    		Controller asia = new Controller("AS");
    		asia.setORB(orb);
			org.omg.CORBA.Object refAS = rootpoa.servant_to_reference(asia);
			DPSSInterface hrefAS = DPSSInterfaceHelper.narrow(refAS);
			NamingContextExt ncRefAS = NamingContextExtHelper.narrow(objRef);		
            NameComponent pathAS[] = ncRefEU.to_name("AS");
            ncRefAS.rebind(pathAS, hrefAS);
            
            System.out.println("Server(s) are Started");
    		
//    		loadData(europe, northamerica, asia);
    		
//    		System.out.println("Initial data loaded into server");
    		
            // client's invocations
    		for (;;) { orb.run(); }
    		
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		System.out.println("Server(s) are closed");
		
	}
	
	/**
	 * This method is used to load the initial player data
	 * @param europe europe controller object 
	 * @param northamerica northamerica controller object 
	 * @param asia asia controller object 
	 */
	static void loadData(Controller europe, Controller northamerica, Controller asia) {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("src/data.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] listParts = line.split(",");
				String firstName = listParts[0];
				String lastName = listParts[1];
				String age = listParts[2];
				String userName = listParts[3];
				String password = listParts[4];
				String ipAddress = listParts[5];
				
				if (ipAddress.startsWith("132")) {
					northamerica.createPlayerAccount(firstName, lastName, age, userName, password, ipAddress);
				} else if (ipAddress.startsWith("93")) {
					europe.createPlayerAccount(firstName, lastName, age, userName, password, ipAddress);
				} else if (ipAddress.startsWith("182")) {
					asia.createPlayerAccount(firstName, lastName, age, userName, password, ipAddress);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to create logs directory to store the logs
	 * @param path location of the logs folder
	 */
	public static void buildLogDirectory(String path) {
		File outputDir = new File(path);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}
	
}