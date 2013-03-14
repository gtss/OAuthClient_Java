/**
 * Created with IntelliJ IDEA.
 * User: tpalmer
 * Date: 3/12/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */

package src;

import play.Play;

public class OAuth2ClientCredentials {

    /** Value of the "API Key". */
    public static final String CLIENT_ID = Play.application().configuration().getString("client_id");

    /** Value of the "API Secret". */
    public static final String CLIENT_SECRET = Play.application().configuration().getString("client_secret");

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
