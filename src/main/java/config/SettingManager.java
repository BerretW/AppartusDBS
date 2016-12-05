package config;

import net.appartus.Tutorial.tutorial;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Alois on 04.12.2016.
 */
public class SettingManager {
    private Logger logger;
    private File settingFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode settingConfig;

    private tutorial plugin;

    public SettingManager(tutorial plugin){
        this.plugin = plugin;
        logger = plugin.getLogger();
        setupSettingConfig();
    }

    private void  setupSettingConfig(){
        settingFile = new File(plugin.getConfigDir().toFile(), "setting.conf");
        loader = HoconConfigurationLoader.builder().setFile(settingFile).build();

        try {
            settingConfig = loader.load();

            if(!settingFile.exists()) {
                settingConfig.getNode("Placeholde").setValue(true);
                loader.save(settingConfig);
            }

        }catch (IOException e){
            logger.warning("Chyba pri nacintani konfigurace");
        }
    }
}
