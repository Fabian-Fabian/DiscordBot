package fabian.discord.bot.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class Settings {

    private Properties config = new Properties();
    //private ArrayList<String> configEntries = new ArrayList<>();
    private String configFileName;

    public String getProperty(String key) {
        this.readProperties();
        return this.config.getProperty(key, null);
    }

    public void setProperty(String key, String value) {
        this.config.setProperty(key, value);
        this.writeProperties();
    }

    private void writeProperties() {
        try {
            this.config.store(new FileOutputStream(this.configFileName), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readProperties() {
        try {
            InputStream in = new FileInputStream(this.configFileName);
            this.config.load(in);
        } catch (FileNotFoundException e) {
            this.writeProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private boolean entryExist(String key){
        return this.configEntries.contains(key);
    }*/

    public Settings(String configFileName) {
        this.configFileName = configFileName;
    }

}
