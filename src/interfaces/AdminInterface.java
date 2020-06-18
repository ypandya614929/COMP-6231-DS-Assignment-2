package interfaces;

import java.io.IOException;
import java.rmi.Remote;
/**
 *
 * @author ypandya
 */
public interface AdminInterface extends Remote {

	/**
	 * This interface method is used to get the status of all the players
	 * @param userName username of the admin
	 * @param password password of the admin
	 * @param ipAddress ip of the admin
	 * @return String containing number of online and offline players
	 * @throws IOException
	 */
	public String getPlayerStatus(String userName, String password, String ipAddress) throws IOException;
	
	/**
	 * @param AdminUsername
	 * @param AdminPassword
	 * @param AdminIP
	 * @param UsernameToSuspend
	 * @return
	 * @throws IOException
	 */
	public String suspendAccount(String AdminUsername, String AdminPassword, String AdminIP, String UsernameToSuspend) throws IOException;

}
