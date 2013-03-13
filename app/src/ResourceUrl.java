/**
 * Created with IntelliJ IDEA.
 * User: tpalmer
 * Date: 3/12/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
package src;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

public class ResourceUrl extends GenericUrl {

    @Key
    private String fields;

    public ResourceUrl(String encodedUrl) {
        super(encodedUrl);
    }

    /**
     * @return the fields
     */
    public String getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(String fields) {
        this.fields = fields;
    }
}