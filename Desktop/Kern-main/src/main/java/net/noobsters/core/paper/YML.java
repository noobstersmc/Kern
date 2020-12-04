package net.noobsters.core.paper;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class YML{
    File directory;
    File file;
    FileConfiguration fileConfiguration;

    public YML(@NotNull File dir, String fileName, boolean overwrite) {
        this.directory = dir;

        if(!this.directory.exists()){
            directory.mkdirs();
        }

        fileName.replace("/","_");
        fileName.replace("\\","_");

        this.file = new File(this.directory, fileName+ ".yml");

        if (!exists_file() || overwrite) {
            if (overwrite) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException exception) {
                Bukkit.getConsoleSender().sendMessage("[ERROR] An error ocurred while creating " + fileName );
                exception.printStackTrace();
            }
        }
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves configurationFile
     */
    public void saveFile(){
        if (!exists_file()){
            return;
        }
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets file FileConfiguration
     * @return FileConfiguration object
     */
    public FileConfiguration getFile(){
        if (!exists_file()){
            return null;
        }
        return fileConfiguration;
    }

    /**
     * Reload the FileConfiguration
     */
    public void reloadFile(){
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private boolean exists_file(){
        return file.exists();
    }
}

