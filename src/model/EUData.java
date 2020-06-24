package model;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.concurrent.ConcurrentHashMap; 


/**
 * @author ypandya
 *
 */
public class EUData {
	/**
	 * This is model class for Europe server
	 */
	ConcurrentHashMap<String, ConcurrentHashMap<String, Administrator>> adminserverData;
	ConcurrentHashMap<String, ConcurrentHashMap<String, Player>> playerserverData;
	String europIp;
	private static Logger logger;

	/**
	 * This method is used to retrieve admin data from the server
	 * @return ConcurrentHashMap containing admin server data
	 */
	public ConcurrentHashMap<String, ConcurrentHashMap<String, Administrator>> getAdminserverData() {
		return adminserverData;
	}

	/**
	 * This method is used to store player data on the server
	 * @param serverData
	 */
	public void setPlayerserverData(ConcurrentHashMap<String, ConcurrentHashMap<String, Player>> serverData) {
		this.playerserverData = serverData;
	}
	
	/**
	 * This method is used to retrieve player data from the server
	 * @return ConcurrentHashMap containing player server data
	 */
	public ConcurrentHashMap<String, ConcurrentHashMap<String, Player>> getPlayerserverData() {
		return playerserverData;
	}

	/**
	 * This method is used to store admin data on the server
	 * @param serverData
	 */
	public void setAdminserverData(ConcurrentHashMap<String, ConcurrentHashMap<String, Administrator>> serverData) {
		this.adminserverData = serverData;
	}

	/**
	 * This method is used to get ip of the server
	 * @return String ip of the server
	 */
	public String geteuropIp() {
		return europIp;
	}

	/**
	 * This method is used to set ip of the server
	 * @param europIp ip of the server
	 */
	public void seteuropIp(String europIp) {
		this.europIp = europIp;
	}

	/**
	 * Constructor
	 */
	public EUData() {
		adminserverData = new ConcurrentHashMap<>();
		playerserverData = new ConcurrentHashMap<>();
		addLog("logs/EU.txt", "EU");
	}

	/**
	 * This synchronized method is used to get the status of all the players
	 * @param userName username of the admin
	 * @param password password of the admin
	 * @param ipAddress ip of the admin
	 * @return String containing number of online and offline players
	 */
	synchronized public String getPlayerStatus(String username, String password, String ip) {
		logger.info("IP : " + ip + ", username : " + username + ", start getPlayerStatus() operation.");
		Administrator adminObj;
		String key = Character.toString(username.charAt(0));
		int onlineCount = 0;
		int offlineCount = 0;
		if (adminserverData.containsKey(key)) {
			ConcurrentHashMap<String,Administrator> temp = adminserverData.get(key);
			if (temp.containsKey(username)) {
				adminObj = temp.get(username);
			} else {
				adminObj = new Administrator(username, password, ip);
			}
		} else {
			adminObj = new Administrator(username, password, ip);
			ConcurrentHashMap<String,Administrator> temp1 = new ConcurrentHashMap<>();
			temp1.put(username, adminObj);
			adminserverData.put(key, temp1);
		}
		adminObj.setPassword(password);
		if (adminObj.userName.equals("Admin") && adminObj.password.equals("Admin")) {
			for (Entry <String, ConcurrentHashMap<String, Player>> outerHashmap : playerserverData.entrySet()) {
				for (Entry <String, Player> innerHashmap : outerHashmap.getValue().entrySet()) {
					if (innerHashmap.getValue() != null) {
						Player playerObj = innerHashmap.getValue();
						if (playerObj.isStatus()) {
							onlineCount++;
						} else {
							offlineCount++;
						}
					}
				}
			}
			logger.info("IP : " + ip + ", username : " + username + ", Result getPlayerStatus() : " + "Europe : "+ onlineCount + " online , " + offlineCount + " offline. ");
			return "Europe : "+ onlineCount + " online , " + offlineCount + " offline. ";
		}
		logger.info("IP : " + ip + ", username : " + username + ", Result getPlayerStatus() : invalid username or password");
		return "Invalid username or password";
	}
	
	/**
	 * This synchronized method is used to suspended player account if exists
	 * @param AdminUsername username of the admin
	 * @param AdminPassword password of the admin
	 * @param AdminIP ip of the admin
	 * @param UsernameToSuspend player username whom account is going to suspend
	 * @return String containing success or error message
	 */
	synchronized public String suspendAccount(String AdminUsername, String AdminPassword, String AdminIP, String UsernameToSuspend) {
		logger.info("IP : " + AdminIP + ", username : " + AdminUsername + ", start suspendAccount() operation.");
		Administrator adminObj;
		String key = Character.toString(AdminUsername.charAt(0));
		if (adminserverData.containsKey(key)) {
			ConcurrentHashMap<String,Administrator> temp = adminserverData.get(key);
			if (temp.containsKey(AdminUsername)) {
				adminObj = temp.get(AdminUsername);
			} else {
				adminObj = new Administrator(AdminUsername, AdminPassword, AdminIP);
			}
		} else {
			adminObj = new Administrator(AdminUsername, AdminPassword, AdminIP);
			ConcurrentHashMap<String,Administrator> temp1 = new ConcurrentHashMap<>();
			temp1.put(AdminUsername, adminObj);
			adminserverData.put(key, temp1);
		}
		adminObj.setPassword(AdminPassword);
		String UsernameToSuspendKey = Character.toString(UsernameToSuspend.charAt(0));
		if (adminObj.userName.equals("Admin") && adminObj.password.equals("Admin")) {
			if (playerserverData.containsKey(UsernameToSuspendKey)) {
				ConcurrentHashMap<String, Player> playerData = playerserverData.get(UsernameToSuspendKey);
				if (playerData.containsKey(UsernameToSuspend)) {
					playerData.remove(UsernameToSuspend);
					logger.info("IP : " + AdminIP + ", username : " + AdminUsername + ", Result suspendAccount() : Player account ("+ UsernameToSuspend +") is suspended");
					return "Player account ("+ UsernameToSuspend +") is suspended";
				}
			}
			logger.info("IP : " + AdminIP + ", username : " + AdminUsername + ", Result suspendAccount() : Player account ("+ UsernameToSuspend +") doesn't exists");
			return "Player account ("+ UsernameToSuspend +") doesn't exists";
		}
		logger.info("IP : " + AdminIP + ", username : " + AdminUsername + ", Result suspendAccount() : invalid username or password");
		return "Invalid username or password";
	}
	
	/**
	 * This synchronized method is used to create the player account
	 * @param firstName firstname of the player
	 * @param lastName lastname of the player
	 * @param age age of the player
	 * @param userName username of the player
	 * @param password password of the player
	 * @param ipAddress ip of the player
	 * @return String containing success or error message
	 */
	synchronized public String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress) {
		logger.info("IP : " + ipAddress + ", username : " + userName + ", start createPlayerAccount() operation.");
		Player playerObj;
		String key = Character.toString(userName.charAt(0));
		if (playerserverData.containsKey(key)) {
			ConcurrentHashMap<String,Player> temp = playerserverData.get(key);
			if (temp.containsKey(userName)) {
				playerObj = temp.get(userName);
				logger.info("IP : " + ipAddress + ", username : " + userName + ", Result createPlayerAccount() : Player already exists");
				return "Player already exists";
			} else {
				playerObj = new Player(firstName, lastName, age, userName, password, ipAddress);
				temp.put(userName, playerObj);
				logger.info("IP : " + ipAddress + ", username : " + userName + ", Result createPlayerAccount() : Player created successfully");
				return "Player created successfully";
			}
		} else {
			playerObj = new Player(firstName, lastName, age, userName, password, ipAddress);
			ConcurrentHashMap<String,Player> temp1 = new ConcurrentHashMap<>();
			temp1.put(userName, playerObj);
			playerserverData.put(key, temp1);
			logger.info("IP : " + ipAddress + ", username : " + userName + ", Result createPlayerAccount() : Player created successfully");
			return "Player created successfully";
		}
	}
	
	/**
	 * This synchronized method is used to sign in into the player account
	 * @param userName username of the player
	 * @param password password of the player
	 * @param ipAddress ip of the player
	 * @return String containing success or error message
	 */
	synchronized public String playerSignIn(String userName, String password, String ipAddress) {
		logger.info("IP : " + ipAddress + ", username : " + userName + ", start playerSignIn() operation.");
		Player playerObj;
		String key = Character.toString(userName.charAt(0));
		if (playerserverData.containsKey(key)) {
			ConcurrentHashMap<String,Player> temp = playerserverData.get(key);
			if (temp.containsKey(userName)) {
				playerObj = temp.get(userName);
				if (playerObj.userName.equals(userName) && playerObj.password.equals(password) && playerObj.ipAddress.equals(ipAddress)) {
					if (playerObj.isStatus()) {
						logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignIn() : Player already signed in");
						return "Player already signed in";
					} 
					else {
						playerObj.setStatus(true);
						logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignIn() : Player sign in successfully");
						return "Player sign in successfully";
					}
				}
				playerObj.setStatus(false);
				logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignIn() : Invalid password or IP address");
				return "Invalid password or IP address";
			}
		}
		logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignIn() : Player account ("+ userName +") doesn't exists");
		return "Player account ("+ userName +") doesn't exists";
	}
	
	/**
	 * This synchronized method is used to sign out from the player account
	 * @param userName username of the player
	 * @param ipAddress ip of the player
	 * @return String containing success or error message
	 */
	synchronized public String playerSignOut(String userName, String ipAddress) {
		logger.info("IP : " + ipAddress + ", username : " + userName + ", start playerSignOut() operation.");
		Player playerObj;
		String key = Character.toString(userName.charAt(0));
		if (playerserverData.containsKey(key)) {
			ConcurrentHashMap<String,Player> temp = playerserverData.get(key);
			if (temp.containsKey(userName)) {
				playerObj = temp.get(userName);
				if (playerObj.userName.equals(userName) && playerObj.ipAddress.equals(ipAddress)) {
					if (!playerObj.isStatus()) {
						logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignOut() : Player is not signed in");
						return "Player is not signed in";
					} else {
						playerObj.setStatus(false);
						logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignOut() : Player sign out successfully");
						return "Player sign out successfully";
					}
				}
				logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignOut() : Invalid IP address");
				return "Invalid IP address";
			}
		}
		logger.info("IP : " + ipAddress + ", username : " + userName + ", Result playerSignOut() : Player account ("+ userName +") doesn't exists");
		return "Player account ("+ userName +") doesn't exists";
	}
	
	/**
	 * This synchronized method is used to transfer the player account
	 * @param Username username of the player
	 * @param Password password of the player
	 * @param OldIPAddress old ip of the player
	 * @param NewIPAddress new ip of the player
	 * @return
	 */
	synchronized public String transferAccount(String Username, String Password, String OldIPAddress, String NewIPAddress) {
		logger.info("IP : " + OldIPAddress + ", username : " + Username + ", start transferAccount() operation.");
		Player playerObj;
		String key = Character.toString(Username.charAt(0));
		if (playerserverData.containsKey(key)) {
			ConcurrentHashMap<String,Player> playerData = playerserverData.get(key);
			if (playerData.containsKey(Username)) {
				playerObj = playerData.get(Username);
				if (playerObj.userName.equals(Username) && playerObj.ipAddress.equals(OldIPAddress) && playerObj.password.equals(Password)) {
					int port = getServerPort(NewIPAddress);
					String data = transferAccountToOtherServer(playerObj.getUserName(), playerObj.getPassword(), playerObj.age, NewIPAddress, playerObj.getFirstName(), playerObj.getLastName(),port, "transferAccount");
					if (data.trim().equals("Player created successfully")) {
						playerData.remove(Username);
						logger.info("IP : " + OldIPAddress + ", username : " + Username + ", Result transferAccount() : Player account ("+ Username +") is transfered from " + OldIPAddress + " to " + NewIPAddress);
						return "Player account ("+ Username +") is transfered from " + OldIPAddress + " to " + NewIPAddress;
					} else {
						logger.info("IP : " + OldIPAddress + ", username : " + Username + ", Result transferAccount() : Player account is not transfered");
						return "Player account is not transfered";
					}
				}
				logger.info("IP : " + OldIPAddress + ", username : " + Username + ", Result transferAccount() : Invalid IP address or Password");
				return "Invalid IP address or Password";
			}
		}
		logger.info("IP : " + OldIPAddress + ", username : " + Username + ", Result transferAccount() : Player account ("+ Username +") doesn't exists");
		return "Player account ("+ Username +") doesn't exists";
	}
	
	/**
	 * This method used to communicate with server to transfer player
	 * @param username username of the player
	 * @param password password of the player
	 * @param age age of the player
	 * @param ip new ip of the player
	 * @param port server port number 
	 * @param fun type of method
	 * @return
	 */
	synchronized public String transferAccountToOtherServer(String username, String password, String age, String ip, String firstName, String lastName, int port, String fun) {

		DatagramSocket ds = null;
		byte[] b = new byte[65535];
		try {
			String request =  fun + "," + username + "," + password + "," + age + "," + ip + "," + firstName + "," + lastName;
			ds = new DatagramSocket();
			DatagramPacket dp = new DatagramPacket(
				request.getBytes(), request.getBytes().length,
				InetAddress.getByName("localhost"), port
			);
			ds.send(dp);
			DatagramPacket dp1 = new DatagramPacket(b, b.length);
			ds.receive(dp1);
			String returnData = new String(dp1.getData());
			return returnData.trim();
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			ds.close();
		}
		return "";
	}
	
	/**
	 * This method is used to return port based on ip
	 * @param ip new ip of transfer account
	 * @return ip
	 */
	public int getServerPort(String ip) {
		if (ip.startsWith("132")) {
			return 8882;
		}
		else if (ip.startsWith("93")) {
			return 8880;
		}
		else if (ip.startsWith("182")) {
			return 8881;
		}
		return 0;	
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

}
