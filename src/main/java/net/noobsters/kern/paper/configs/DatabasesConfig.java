package net.noobsters.kern.paper.configs;

import com.google.gson.JsonElement;

import net.noobsters.kern.paper.utils.JsonConfig;

public class DatabasesConfig extends JsonConfig {

    public DatabasesConfig() throws Exception {
        super("databases.json");
        if (!getJsonObject().has("mongodb-connection-uri"))
            addDefaults();
    }

    /**
     * Quick reload method, data might be lost.
     * 
     * @throws Exception
     */
    public void reload() throws Exception {
        this.load();
    }

    /**
     * Obtain MongoURI if present, otherwise throws exception.
     * 
     * @return MongoURI from /condor/database.json
     */
    public String getMongoURI() throws Exception {
        return getElement("mongodb-connection-uri").getAsString();
    }

    private void addDefaults() throws Exception {
        var defaultJson = getJsonObject();
        defaultJson.addProperty("mongodb-connection-uri", "mongodb://localhost:27017");
        setJsonObject(defaultJson);
        this.save();
    }

    /**
     * Helper method to obtain properties of local jsons.
     * 
     * @param property key of the key-value pair.
     * @return value of the key-value pair.
     */
    public JsonElement getElement(String property) {
        return this.getJsonObject().get(property);
    }

}
