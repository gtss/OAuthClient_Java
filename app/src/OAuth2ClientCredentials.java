/**
 * Created with IntelliJ IDEA.
 * User: tpalmer
 * Date: 3/12/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */

package src;

public class OAuth2ClientCredentials {

    /** Value of the "API Key". */
    public static final String CLIENT_ID = "rwcas";

    /** Value of the "API Secret". */
    public static final String CLIENT_SECRET = "V7R76wX2ma0Mnnd7rxZWuEC9te0=";

    public static void errorIfNotSpecified() {
        if (CLIENT_ID.startsWith("Enter ")) {
            System.err.println(CLIENT_ID);
            System.exit(1);
        }
        if (CLIENT_SECRET.startsWith("Enter ")) {
            System.err.println(CLIENT_SECRET);
            System.exit(1);
        }
    }
}
