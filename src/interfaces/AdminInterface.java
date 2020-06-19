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
	 * This interface method is used to suspend player account
	 * @param AdminUsername username of the admin
	 * @param AdminPassword password of the admin
	 * @param AdminIP ip of the admin
	 * @param UsernameToSuspend player username
	 * @return String containing success/error message
	 * @throws IOException
	 */
	public String suspendAccount(String AdminUsername, String AdminPassword, String AdminIP, String UsernameToSuspend) throws IOException;

}
