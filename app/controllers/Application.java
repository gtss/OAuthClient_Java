package controllers;

import com.google.api.client.http.*;
import com.google.api.client.json.JsonObjectParser;
import play.*;
import play.mvc.*;

import src.OAuth2ClientCredentials;
import src.ResourceUrl;
import views.html.*;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class Application extends Controller {

    /**
     * OAuth 2 scope.
     */
    private static final String SCOPE = "clientaccess"; // Required. MUST BE LOWERCASE

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Global instances of TrainingPeaks OAuth server URLs.
     */
    private static final String TOKEN_SERVER_URL = "http://dev20-t430:8900/OAuthAuthorizationServer/OAuth/Token";
    private static final String AUTHORIZATION_SERVER_URL = "http://dev20-t430:8900/OAuthAuthorizationServer/OAuth/Authorize";
    private static final String RESOURCE_URL = "http://dev20-t430:8900/OAuthAuthorizationServer/ClientAccess/V1/TrainingPeaks";
    private static final String CALLBACK_URL = Play.application().configuration().getString("clientID");

    /**
     * Display the splash page for initiating the OAuth process
     */
    public static Result index() {
        return ok(index.render());
    }

    /**
     * Run OAuth
     */
    public static Result oauth() {
        try {
            OAuth2ClientCredentials.errorIfNotSpecified();
            return redirect(sendTokenRequest(CALLBACK_URL, OAuth2ClientCredentials.CLIENT_ID, SCOPE).toString());
        } catch (Throwable t) {
            return ok(t.getMessage());
        }
    }

    /**
     * Callback handler. Parses code from OAuth server, handles/stores credentials,
     * then directs to appropriate TrainingPeaks URL for the logged in user.
     */
    public static Result callback() throws IOException {
        Http.Request request = request();
        String error = request.getQueryString("error");
        String code = request.getQueryString("code");

        if (code != null) {
            final Credential credential = authorize(code, CALLBACK_URL);
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {
                            credential.initialize(request);
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    });

            ResourceUrl url = new ResourceUrl(RESOURCE_URL);
            url.setFields("id,tags,title,url");
            HttpRequest resourceRequest = requestFactory.buildGetRequest(url);
            String content = resourceRequest.execute().parseAsString();

            return redirect(content);
        } else {
            return ok("An error occurred on callback: " + error);
        }

    }

    /**
     * Builds the authorization code then creates and stores the credentials in the CredentialStore
     */
    private static Credential authorize(String code, String redirectUri)
            throws IOException {
        AuthorizationCodeFlow codeFlow =
                new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                        HTTP_TRANSPORT,
                        JSON_FACTORY,
                        new GenericUrl(TOKEN_SERVER_URL),
                        new ClientParametersAuthentication(
                                OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.CLIENT_SECRET),
                        OAuth2ClientCredentials.CLIENT_ID,
                        AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList(SCOPE)).build();

        TokenResponse response = codeFlow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .setScopes(Arrays.asList(SCOPE)).execute();

        return codeFlow.createAndStoreCredential(response, null); // You may want to set up the CredentialStore so it persists for you.
    }

    /**
     * Initial call to the TrainingPeaks OAuth server for token.
     */
    private static URI sendTokenRequest(String redirectUrl, String clientId, String scope) throws IOException {
        String authorizationUrl = new AuthorizationCodeRequestUrl(
                AUTHORIZATION_SERVER_URL, clientId).setRedirectUri(redirectUrl)
                .setScopes(Arrays.asList(scope)).build();

        return URI.create(authorizationUrl);
    }

}
