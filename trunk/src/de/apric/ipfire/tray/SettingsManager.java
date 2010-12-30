/*
 * This file is part of IPFireTray.
 *
 * IPFireTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IPFireTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IPFireTray.  If not, see <http://www.gnu.org/licenses/>.
 */


package de.apric.ipfire.tray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * settings class responsible for loading, validating and saving properties
 *
 * @author apric
 */
class SettingsManager {

    static final String PROP_FILE_NAME = "settings.properties";

    static final String DEFAULT_IPFIRE_HOST = "ipfire.home";
    static final String DEFAULT_IPFIRE_PORT = "444";
    static final String DEFAULT_IPFIRE_USER = "admin";
    static final String DEFAULT_IPFIRE_PASS = "password";


    /**
     * load (and validate) properties from disk
     * if the configuration file does not exist, a default one will be created, saved and returned
     *
     * @return the properties from disk, or the default properties, if none existed
     * @throws IOException
     */
    public static Properties loadProperties() throws Exception {

        File propFile = null;

        propFile = new File(PROP_FILE_NAME);

        if (propFile.exists() && propFile.isFile() && propFile.canRead()){
            Properties properties = new Properties();
            properties.load(new FileInputStream(propFile));

            validate(properties);
            
            return properties;
        }
        else {
            return saveAndGetSettings(getDefaultProperties());
        }
    }


    /**
     * validate the given Properties file, any error throws an Exception
     *
     * @param properties
     * @throws IllegalArgumentException in case a property value is invalid
     */
    public static void validate(Properties properties) throws IllegalArgumentException{

        if (properties == null){
            throw new NullPointerException("Cannot validate properties file: NULL"); // uh-oh
        }

        /* host */
        try {
            new URI(properties.getProperty("host")); // very basic validation, @TODO: improve validation of host...
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("host must be a valid URI", ex);
        }

        /* port */
        try {
            Integer.parseInt(properties.getProperty("port"));
        }
        catch (NumberFormatException ex){
            throw new IllegalArgumentException("port must an integer and > 0. Check the settings file.", ex);
        }
        
        /* interval */
        try {
            final long refreshInterval = Long.parseLong(properties.getProperty("interval"));
            if (refreshInterval < 1) {
                throw new IllegalArgumentException("interval must be > 0. Check the settings file.");
            }
        }
        catch (NumberFormatException ex){
            throw new IllegalArgumentException("interval must an integer (milliseconds) and > 0. Check the settings file.", ex);
        }
        
        /* maxDownKBpS */
        try {
            final float maxDownKBpS = Float.parseFloat(properties.getProperty("maxDownKBpS"));
            if (maxDownKBpS < 1) {
                throw new IllegalArgumentException("maxDownKBpS must be > 0. Check the settings file.");
            }
        }
        catch (NumberFormatException ex){
            throw new IllegalArgumentException("maxDownKBpS must be a float value (KB/s) and > 0. Check the settings file.", ex);
        }

        /* maxUpKBpS */
        try {
            final float maxUpKBpS = Float.parseFloat(properties.getProperty("maxUpKBpS"));
            if (maxUpKBpS < 1) {
                throw new IllegalArgumentException("maxUpKBpS must be > 0. Check the settings file.");
            }
        }
        catch (NumberFormatException ex){
            throw new IllegalArgumentException("maxUpKBpS must be a float value (KB/s) and > 0. Check the settings file.", ex);
        }
        
    }


    /**
     * save properties file to disk
     *
     * @param properties
     * @return the properties file we just saved (validated)
     * @throws IOException
     */
    public static Properties saveAndGetSettings(Properties properties) throws Exception{

        validate(properties);
        
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(PROP_FILE_NAME));
            properties.store(fos, "config for IPFireTray");

            return properties;
        }
        catch (IOException e){
            throw e; // re-throw Exception
        }
        finally {
            if (fos != null) fos.close(); // cleanup
        }
    }


    /**
     * @return default properties
     */
    protected static Properties getDefaultProperties(){

        Properties properties = new Properties();
        properties.setProperty("host",          DEFAULT_IPFIRE_HOST);
        properties.setProperty("port",          DEFAULT_IPFIRE_PORT);
        properties.setProperty("user",          DEFAULT_IPFIRE_USER);
        properties.setProperty("pass",          DEFAULT_IPFIRE_PASS);
        properties.setProperty("interval",      "1000");
        properties.setProperty("maxDownKBpS",   "1600");
        properties.setProperty("maxUpKBpS",     "100");

        return properties;
    }

}
