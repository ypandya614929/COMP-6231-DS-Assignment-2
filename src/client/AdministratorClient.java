package client;
//References:
//https://systembash.com/a-simple-java-udp-server-and-udp-client/
//https://www.tutorialspoint.com/java_rmi/java_rmi_introduction.htm
//https://www.javatpoint.com/RMI
//https://www.geeksforgeeks.org/multithreading-in-java/
//https://www.geeksforgeeks.org/synchronized-in-java/
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import interfaces.AdminInterface;

/**
 *
 * @author ypandya
 */
public class AdministratorClient {
	
	/**
	 * This is the admin user class containing admin user operations
	 */
	static AdminInterface adminObj;
	static Registry registry1;
	static Registry registry2;
	static Registry registry3;
	static BufferedReader br;
	private static Logger logger;
	
	/**
	 * main method to run the admin user operations
	 * @param args
	 * @throws NotBoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws NotBoundException, IOException, InterruptedException {

		registry1 = LocateRegistry.getRegistry(9990);
		registry2 = LocateRegistry.getRegistry(9991);
		registry3 = LocateRegistry.getRegistry(9992);
		
		br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			adminObj = null;
			System.out.println("\nDistributed Player Status System");
			System.out.println("================================");
			System.out.println("Admin Options : \n");
			System.out.println("1 : Get Player status");
			System.out.println("2 : Suspend Player Account");
			System.out.println("3 : Exit\n");
			System.out.print("Select : ");
			String choice = br.readLine().trim();
			if (choice.equals("1")){
				getPlayerStatus();
			}
			else if (choice.equals("2")){
				suspendAccount();
			}
			else if (choice.equals("3")){
				break;
			}
			else {
				System.out.println("===== Please select valid option =====");
				continue;
			}
		}
	}
		
	/**
	 * This method is used to check the string is empty/null or not
	 * @param str
	 * @return boolean true if string is null, false otherwise
	 */
	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }
	
	/**
	 * This method is used to check user wants to re-enter missing input or wants to quit
	 * @return boolean true if user wants to exit, false otherwise
	 * @throws IOException
	 */
	public static boolean exitCheck() throws IOException {
		boolean is_return = false;
		while (true) {
			System.out.println("Please select below options : ");
			System.out.println("1 : Do you want to re-enter");
			System.out.println("2 : exit");
			System.out.print("Select : ");
			String str = br.readLine().trim();
			if (str.equals("1")) {
				is_return = false;
				break;
			}
			else if (str.equals("exit") || str.equals("2")) {
				is_return = true;
				break;
			}
			else {
				System.out.println("===== Please select valid option =====");
				is_return = false;
				continue;
			}
		}
		return is_return;
	}
	
	/**
	 * This method is used to check the ip is valid or not
	 * @param ip
	 * @return boolean true if ip is valid, false otherwise
	 */
	public static boolean ipCheck(String ip) {
		if (ip.startsWith("132") || ip.startsWith("182")) {
			if (ip.length()==15) {
				if ((ip.substring(3,4).equals(".")) && (ip.substring(7,8).equals(".")) && (ip.substring(11, 12).equals("."))) {
					return true;
				}
			}
		}
		else if (ip.startsWith("93")) {
			if (ip.length()==14) {
				if ((ip.substring(2,3).equals(".")) && (ip.substring(6,7).equals(".")) && (ip.substring(10, 11).equals("."))) { 
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method is used to get input to get player status
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NotBoundException
	 */
	public static void getPlayerStatus() throws IOException, InterruptedException, NotBoundException{
		boolean is_info_collected = false;
		String username = "";
		String password = "";
		String ip = "";
		boolean returnMenu = false;
		while (!returnMenu) {
			if (!returnMenu) {
				while (isNullOrEmpty(username)) {
					System.out.print("Enter username : ");
					username = br.readLine().trim();
					if (isNullOrEmpty(username)) {
						System.out.println("===== The username can't be empty. =====");
						if(exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!returnMenu) {
				while (isNullOrEmpty(password)) {
					System.out.print("Enter password : ");
					password = br.readLine().trim();
					if (isNullOrEmpty(password)) {
						System.out.println("===== The password can't be empty. =====");
						if(exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!returnMenu) {
				while (!ipCheck(ip) && !returnMenu) {
					System.out.print("Enter ip-address in following format 132.XXX.XXX.XXX or 93.XXX.XXX.XXX or 182.XXX.XXX.XXX : ");
					ip = br.readLine().trim();
					if (ipCheck(ip)) {
						returnMenu = true;
						break;
					}
					else {
						System.out.println("===== The ip must be in following format 132.XXX.XXX.XXX or 93.XXX.XXX.XXX or 182.XXX.XXX.XXX. =====");
						if (exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!isNullOrEmpty(username) && !isNullOrEmpty(password) && ipCheck(ip)) {
				is_info_collected = true;
			}
		}
		if (is_info_collected && ip!=null) {
			addLog("logs/" + username + ".txt", username);
			createAdminObject(ip);
			logger.info("IP : " + ip + ", username : " + username + ", start getPlayerStatus() operation.");
			String response = adminObj.getPlayerStatus(username, password, ip);
			logger.info("IP : " + ip + ", username : " + username + ", Result getPlayerStatus() : " + response);
			System.out.println(response);
		}
	}
	
	/**
	 * This method is used to get input to suspend player account
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NotBoundException
	 */
	public static void suspendAccount() throws IOException, InterruptedException, NotBoundException{
		boolean is_info_collected = false;
		String username = "";
		String password = "";
		String ip = "";
		String playerUsername = "";
		boolean returnMenu = false;
		while (!returnMenu) {
			if (!returnMenu) {
				while (isNullOrEmpty(username)) {
					System.out.print("Enter username : ");
					username = br.readLine().trim();
					if (isNullOrEmpty(username)) {
						System.out.println("===== The username can't be empty. =====");
						if(exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!returnMenu) {
				while (isNullOrEmpty(password)) {
					System.out.print("Enter password : ");
					password = br.readLine().trim();
					if (isNullOrEmpty(password)) {
						System.out.println("===== The password can't be empty. =====");
						if(exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!returnMenu) {
				while (!ipCheck(ip) && !returnMenu) {
					System.out.print("Enter ip-address in following format 132.XXX.XXX.XXX or 93.XXX.XXX.XXX or 182.XXX.XXX.XXX : ");
					ip = br.readLine().trim();
					if (ipCheck(ip)) {
						break;
					}
					else {
						System.out.println("===== The ip must be in following format 132.XXX.XXX.XXX or 93.XXX.XXX.XXX or 182.XXX.XXX.XXX. =====");
						if (exitCheck()) {
							returnMenu = true;
							break;
						}
					}
				}
			}
			if (!returnMenu) {
				while (isNullOrEmpty(playerUsername)) {
					System.out.print("Enter Player username to suspend account : ");
					playerUsername = br.readLine().trim();
					if (isNullOrEmpty(playerUsername)) {
						System.out.println("===== The player username can't be empty. =====");
						if(exitCheck()) {
							returnMenu = true;
							break;
						}
					} else {
						returnMenu = true;
						break;
					}
				}
			}
			if (!isNullOrEmpty(username) && !isNullOrEmpty(password) && ipCheck(ip) && !isNullOrEmpty(playerUsername)) {
				is_info_collected = true;
			}
		}
		if (is_info_collected && ip!=null) {
			addLog("logs/" + username + ".txt", username);
			createAdminObject(ip);
			logger.info("IP : " + ip + ", username : " + username + ", start suspendAccount() operation.");
			String response = adminObj.suspendAccount(username, password, ip, playerUsername);
			logger.info("IP : " + ip + ", username : " + username + ", Result suspendAccount() : " + response);
			System.out.println(response);
		}
	}
	
	/**
	 * This method is used to set/update logger
	 * @param path
	 * @param key
	 */
	static void addLog(String path, String key) {
		try {
			File f = new File(path);
			String data = "";
			logger = Logger.getLogger(key);
			if(f.exists() && !f.isDirectory()) { 
				data = new String(Files.readAllBytes(Paths.get(path)));
			}
			if (logger.getHandlers().length < 1)
			{	
				try {
					f.delete();
				} catch (Exception e) {}
				logger = Logger.getLogger(key);
				FileHandler fh = new FileHandler(path, true);
				SimpleFormatter ft = new SimpleFormatter();
				fh.setFormatter(ft);
				logger.addHandler(fh);
				logger.setUseParentHandlers(false);
				logger.info(data);
				logger.setUseParentHandlers(true);
				
			}
		} catch (Exception err) {
			logger.info("Unable to create file, please check file permission.");
		}
	}

	
	/**
	 * This method is used to set the server object based on the ip
	 * @param ip
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static void createAdminObject(String ip) throws AccessException, RemoteException, NotBoundException {
		if (ip.startsWith("132")) {
			adminObj = (AdminInterface) registry1.lookup("North America");
		} 
		else if (ip.startsWith("93")) {
			adminObj = (AdminInterface) registry2.lookup("Europe");
		} 
		else if (ip.startsWith("182")) {
			adminObj = (AdminInterface) registry3.lookup("Asia");
		}
	}
}
	
