package me.emafire003.dev.ohmymeteors.util.datasaver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class SimpleDataSaverUtils {

    public static SimpleData DATA = new SimpleData();
    public static final String FILENAME = OhMyMeteors.MOD_ID + "_data.json";
    public static Path PATH = OhMyMeteors.PATH.resolve(FILENAME);

    /** Saves or generates the file with the new DATA
     * */
    public static void saveFile(){
        try (FileWriter fileWriter = new FileWriter(String.valueOf(PATH.toFile()), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter, 4096)) {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            //Map<String, String> newMap = gson.fromJson(json, typeOfHashMap); // This type must match TypeToken


            gson.toJson(DATA, bufferedWriter);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**Reads the pokemons that should spawn with meteors from the config file*/
    public static SimpleData readFile(){
        try (FileReader fileReader = new FileReader(String.valueOf(PATH.toFile()), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(fileReader, 4096)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            //Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
            return gson.fromJson(bufferedReader, SimpleData.class);
        } catch (FileNotFoundException e){
            saveFile();
            return readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
