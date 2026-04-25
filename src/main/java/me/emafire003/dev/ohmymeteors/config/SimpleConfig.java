package me.emafire003.dev.ohmymeteors.config;

/*
 * Copyright (c) 2021 magistermaks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//Modified by Emafire003 (same license)

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import org.apache.commons.io.FileDeleteStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.LOGGER;

public class SimpleConfig {

    private final HashMap<String, String> config = new HashMap<>();
    private final ConfigRequest request;
    private boolean broken = false;

    public interface DefaultConfig {
        String get( String namespace );

        static String empty( String namespace ) {
            return "";
        }
    }

    public static class ConfigRequest {

        private final File file;
        private final String filename;
        private DefaultConfig provider;

        private ConfigRequest(File file, String filename ) {
            this.file = file;
            this.filename = filename;
            this.provider = DefaultConfig::empty;
        }

        /**
         * Sets the default config provider, used to generate the
         * config if it's missing.
         *
         * @param provider default config provider
         * @return current config request object
         * @see DefaultConfig
         */
        public ConfigRequest provider( DefaultConfig provider ) {
            this.provider = provider;
            return this;
        }

        /**
         * Loads the config from the filesystem.
         *
         * @return config object
         * @see SimpleConfig
         */
        public SimpleConfig request() {
            return new SimpleConfig( this );
        }

        public String getConfig() {
            return provider.get( filename ) + "\n";
        }

    }

    /**
     * Creates new config request object, ideally `namespace`
     * should be the name of the mod id of the requesting mod
     *
     * @param filename - name of the config file
     * @return new config request object
     */
    //Modified to fit the directory
    public static ConfigRequest of( String filename ) {
        Path path = OhMyMeteors.PATH;
        return new ConfigRequest( path.resolve( filename + ".yml" ).toFile(), filename );
    }

    private void createConfig() throws IOException {

        LOGGER.debug("Creating config " + request.filename);
        // try creating missing files
        request.file.getParentFile().mkdirs();
        Files.createFile( request.file.toPath() );

        // write default config data
        PrintWriter writer = new PrintWriter(request.file, StandardCharsets.UTF_8);
        writer.write( request.getConfig() );
        writer.close();

    }

    private void loadConfig() throws IOException {
        Scanner reader = new Scanner( request.file );
        for( int line = 1; reader.hasNextLine(); line ++ ) {
            parseConfigEntry( reader.nextLine(), line );
        }
        reader.close();
    }

    private String current_config;

    //FINNALLY IT WORKS
    /**Updates from one config version to the other*/
    public void updateValues(HashMap<Pair<String, ?>, Pair<String, ?>> sub_map) throws IOException {
        current_config = request.getConfig();
        current_config = current_config.replaceAll("\\{", "éàè");
        current_config = current_config.replaceAll("}", "àéà");

        sub_map.forEach((regneedone, editedone) -> {
            String already_modified_string = editedone.getFirst() + ":" + editedone.getSecond();
            String regenned_string = regneedone.getFirst() + ":" + regneedone.getSecond();

            // The {} of maps is wrongly treated as a regex, so i needed to get a bit creative.
            // It subs all instances of {} with a string of unlikely chars which will be subbed before writing to file again by {}
            regenned_string = regenned_string.replaceAll("\\{", "éàè");
            regenned_string = regenned_string.replaceAll("}", "àéà");
            already_modified_string = already_modified_string.replaceAll("\\{", "éàè");
            already_modified_string = already_modified_string.replaceAll("}", "àéà");

            if(!editedone.getFirst().equalsIgnoreCase("version") && editedone.getSecond() != null && regneedone.getSecond() != null){
                current_config = current_config.replaceAll(regenned_string, already_modified_string);
            }
        });
        //Replacing the weird chars back to {}
        current_config = current_config.replaceAll("éàè", "{");
        current_config = current_config.replaceAll("àéà", "}");

        // try creating missing files
        request.file.delete();
        request.file.getParentFile().mkdirs();
        Files.createFile( request.file.toPath() );

        PrintWriter writer = new PrintWriter(request.file, StandardCharsets.UTF_8);
        writer.write(current_config);
        writer.close();
    }

    /**Used for migration to the new config format. Needs a list of the old config settings.
     * Uses reflection to update to the new format*/
    public void migrateToNew(List<String> settingNames) throws IOException {

        settingNames.forEach(setting -> {
            if(!Objects.equals(setting, "spacer")){
                updateSettingByFieldString(setting);
            }
        });

        /*File target = OhMyMeteors.PATH.resolve(OhMyMeteors.MOD_ID + ".oldformat").toFile();
        try{
            FileUtils.copyFile(request.file, target);
            if(CONFIG.delete()){
                LOGGER.info("Config deleted successfully");
            }else{
                LOGGER.error("The config could not be deleted");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }*/
        request.file.renameTo(new File(request.file.getPath()+".oldformat"));

        OhMyMeteors.CONFIG.save();
    }

    public void updateSettingByFieldString(String fieldName){
        Field currentSetting = null;
        try {
            currentSetting = OhMyMeteors.CONFIG.getClass().getField(fieldName);
            currentSetting.setAccessible(true);
            if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG)){
                LOGGER.error("There has been an error while trying to migrate the config!");
            }

        } catch (NoSuchFieldException ignored) {
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.meteorSpawning.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.meteorSpawning)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.meteorBehaviourSection.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.meteorBehaviourSection)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.notificationSection.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.notificationSection)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.lasersSection.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.lasersSection)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.visualsSection.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.visualsSection)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        if(currentSetting == null){
            try {
                currentSetting = OhMyMeteors.CONFIG.meteorShowerSection.getClass().getField(fieldName);
                currentSetting.setAccessible(true);
                if(!updateSettingByType(currentSetting, fieldName, OhMyMeteors.CONFIG.meteorShowerSection)){
                    LOGGER.error("There has been an error while trying to migrate the config!");
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
    }

    private boolean updateSettingByType(Field currentSetting, String fieldName, Object object) {
        Class<?> type = currentSetting.getType();
        try {
            if(type == int.class){
                currentSetting.set(object, getOrDefault(fieldName, -1));
            }
            if(type == double.class){
                currentSetting.set(object, getOrDefault(fieldName, -1.0d));
            }
            if(type == float.class){
                currentSetting.set(object, getOrDefault(fieldName, -1.0f));
            }
            if(type == boolean.class){
                currentSetting.set(object, getOrDefault(fieldName, false));
            }
            if(type == double.class){
                currentSetting.set(object, getOrDefault(fieldName, -1.0d));
            }
            if(type == String.class){
                currentSetting.set(object, getOrDefault(fieldName, "error"));
            }
            if(type == List.class){
                currentSetting.set(object, getOrDefault(fieldName, List.of("error")));
            }
            if(type == Map.class){
                currentSetting.set(object, getOrDefault(fieldName, Map.of("error", -1)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**Used to write to file the updated values stored in the UPDATE MAP*/
    /*public void update() throws IOException {
        LOGGER.info("Trying to update!");
        updateValues(UPDATE_MAP);
    }*/

    /**Used to write to file the updated values stored in the UPDATE MAP*/
    public void update() throws IOException {
        // try creating missing files
        request.file.delete();
        request.file.getParentFile().mkdirs();
        Files.createFile( request.file.toPath() );

        PrintWriter writer = new PrintWriter(request.file, StandardCharsets.UTF_8);
        writer.write(current_config);
        writer.close();
    }

    // Modification by Kaupenjoe & Emafire003
    public void parseConfigEntry( String entry, int line ) {
        if( !entry.isEmpty() && !entry.startsWith( "#" )) {
            String[] parts = entry.split(":", 2);
            if( parts.length == 2) {
                
                // Recognizes comments after a value
                String temp = parts[1].split(" #")[0];
                config.put( parts[0], temp );
            }else{
                throw new RuntimeException("Syntax error in config file on line " + line + "!");
            }
        }
    }

    private SimpleConfig(ConfigRequest request) {
        this.request = request;
        String identifier = "Config '" + request.filename + "'";

        if( !request.file.exists() ) {
            LOGGER.info( identifier + " is missing, generating default one..." );

            try {
                createConfig();
            } catch (IOException e) {
                LOGGER.error( identifier + " failed to generate!" );
                e.printStackTrace();
                broken = true;
            }
        }

        if( !broken ) {
            try {
                loadConfig();
            } catch (Exception e) {
                LOGGER.error( identifier + " failed to load!" );
                e.printStackTrace();
                broken = true;
            }
        }

    }

    /**
     * Queries a value from config, returns `null` if the
     * key does not exist.
     *
     * @return  value corresponding to the given key
     * @see     SimpleConfig#getOrDefault
     */
    @Deprecated
    public String get( String key ) {
        return config.get( key );
    }

    /**
     * Returns string value from config corresponding to the given
     * key, or the default string if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public List<String> getOrDefault( String key, List<String> def ) {
        String val = get(key);
        if(val == null){
            return def;
        }
        //Remove "[" and "]"
        val = val.substring(1, val.length() - 1);
        return Arrays.asList(val.split(", ", -1));
    }

    /**
     * Returns string value from config corresponding to the given
     * key, or the default string if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public Map<String, Integer> getOrDefault( String key, Map<String, Integer> def ) {
        String val = get(key);
        if(val == null){
            return def;
        }

        //An example/sample is: < dimension_chances:{minecraft:overworld=20000, minecraft:the_end=200000} >

        //Remove "{" and "}"
        val = val.substring(1, val.length() - 1);

        //Now it's: < minecraft:overworld=20000, minecraft:the_end=200000 >

        @NotNull String[] vals = val.split(", ", -1);

        //Now each entry/value is < minecraft:overworld=20000 >

        HashMap<String, Integer> map = new HashMap<>();

        for(String entry : vals){
            @NotNull String[] the_entry = entry.split("=");
            map.put(the_entry[0], Integer.parseInt(the_entry[1]));
        }

        return map;
    }

    /**
     * Returns string value from config corresponding to the given
     * key, or the default string list if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public String getOrDefault( String key, String def ) {
        String val = get(key);
        if(val == null){
            LOGGER.info("Returing the default value!!!!!!");
            return def;
        }

        return val;
    }

    public HashMap<String, String> getConfigCopy(){
        return config;
    }

    /**
     * Returns integer value from config corresponding to the given
     * key, or the default integer if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public int getOrDefault( String key, int def ) {
        /*try {
            return Integer.parseInt( get(key) );
        } catch (Exception e) {
            LOGGER.info("ERROR! NOTHING FOUND");
            e.printStackTrace();
            return def;
        }*/
        return Integer.parseInt( get(key) );
    }

    /**
     * Returns boolean value from config corresponding to the given
     * key, or the default boolean if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public boolean getOrDefault( String key, boolean def ) {
        String val = get(key);
        if( val != null ) {
            return val.equalsIgnoreCase("true");
        }

        return def;
    }

    /**
     * Returns double value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public double getOrDefault( String key, double def ) {
        try {
            return Double.parseDouble( get(key) );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns float value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    public float getOrDefault( String key, float def ) {
        try {
            return Float.parseFloat( get(key) );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns double value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     * Returns the minimum value if the one specified is below it
     *
     * @return  value corresponding to the given key, or the default value
     */
    public double getOrDefaultOrMin( String key, double def , double min) {
        try {
            double d = Double.parseDouble( get(key) );
            return Math.max(d, min);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * If any error occurred during loading or reading from the config
     * a 'broken' flag is set, indicating that the config's state
     * is undefined and should be discarded using `delete()`
     *
     * @return the 'broken' flag of the configuration
     */
    public boolean isBroken() {
        return broken;
    }

    /**
     * deletes the config file from the filesystem
     *
     * @return true if the operation was successful
     */
    public boolean delete() throws IOException {
        if(request.file.delete()){
            return true;
        }else{
            LOGGER.warn("Normal deletion not possible, force deleting the config file!");
            FileDeleteStrategy.FORCE.delete(request.file);
        }
        LOGGER.warn( "Config '" + request.filename + "' was removed from existence!" );
        return true;
    }

    /**Saves a new boolean value to the config file*/
    public void set( String key, boolean def ) {
        if(current_config == null || current_config.isEmpty()){
            current_config = request.getConfig();
        }

        String new_string = key + ":" + def;
        String old_string = key + ":" + this.get(key);
        current_config = current_config.replaceAll(old_string, new_string);
    }

    /**Saves a new String value to the config file*/
    public void set( String key, String def ) {
        if(current_config == null || current_config.isEmpty()){
            current_config = request.getConfig();
        }

        String new_string = key + ":" + def;
        String old_string = key + ":" + this.get(key);
        current_config = current_config.replaceAll(old_string, new_string);
    }

    /**Saves a new int value to the config file*/
    public void set( String key, int def ) {
        if(current_config == null || current_config.isEmpty()){
            current_config = request.getConfig();
        }

        String new_string = key + ":" + def;
        String old_string = key + ":" + this.get(key);
        current_config = current_config.replaceAll(old_string, new_string);
    }

    /**Saves a new double value to the config file*/
    public void set( String key, double def ) {
        if(current_config == null || current_config.isEmpty()){
            current_config = request.getConfig();
        }

        String new_string = key + ":" + def;
        String old_string = key + ":" + this.get(key);
        current_config = current_config.replaceAll(old_string, new_string);
    }



}

