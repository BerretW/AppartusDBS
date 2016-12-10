package net.appartus.Tutorial;


import Commands.GodExec;
import Commands.TierSystem;
import Commands.debuger;
import Events.APPermBlock;
import com.google.inject.Inject;
import config.SettingManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Plugin(id="appartusbds", name="Appartus Block Deny System", version = "0.2")

public class tutorial {

    public static List<String> PermDebuger = new ArrayList<>();
    public static List<String> DetailDebuger = new ArrayList<>();
    public static ArrayList<ArrayList<String>> PermAction = new ArrayList<>();

    public static String DebugTool = "item.carrotOnAStick"; //
    public static String TierTool = "item.shears"; //
    public static String BlockPermission = "appartus.block."; //
    public static String AllowPermission = "appartus.allow.";//
    public static String DebugerPermission = "appartus.debuger";//
    public static String PermCommand = "pm users $user set permission $perm";//
    public static String TierCommand = "pm users $user group add $tier";
    public static int BlockLearnLevel = 10;//
    public static int TierLearnLevel = 10;//

    public static String DenyPerm = "Ještě neumíš používat tento předmět, musíš se ho nejdříve naučit.";
    public static String DenyPermNeeded = "Pro povoleni tohoto predmetu je potreba jedna z techto permisi:";
    public static String DenyPermToLearn = "Nemas opravneni kohokoliv ucit s timto blokem";
    public static String SelectedPlayer = "Vybral jsi hráče: %s";
    public static String SelectedBlock = "Vybral jsi blok: %s";
    public static String SelectedByPlayer = "Byl jsi vybrán hráčem: %s";
    public static String AlreadyKnow = "Vybraný hráč již tento předmět může používat.";
    public static String LowLevel = "Hrac %s, ma moc nizky level %s. Pro tuto akci je treba aby měl %s";
    public static String NotSelectedPlayer = "Nemáš vybraného hráče";
    public static String NotSelectedBlock = "Nemáš vybraný blok";
    public static String LernBlockPlayer = "Naučil jsi hráče %s používat blok %s, stálo ho to %s Levelů";

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path DefaultConfig;



    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private ConfigurationNode config;


    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;


    @Inject
    Logger logger;

    private SettingManager settingManager;

    @Listener
    public void preInit(GamePreInitializationEvent event){
        try {
            config = loader.load();

            if (!DefaultConfig.toFile().exists()) {
                config.getNode("DebugTool").setValue("item.carrotOnAStick");
                config.getNode("TierTool").setValue("item.shears");
                config.getNode("DenyPermission").setValue("appartus.block.");
                config.getNode("AllowPermission").setValue("appartus.allow.");
                config.getNode("DebugerPermission").setValue("appartus.debuger");
                config.getNode("PermissionCommand").setValue("pm users $user set permission $perm");
                config.getNode("Level to learn Block").setValue(10);
                config.getNode("Level to Learn Tier").setValue(30);



                loader.save(config);
            }
        } catch(IOException e){
            logger.warning("Error loading Default configuration");
        }

        settingManager = new SettingManager(this);
    }

    @Inject
    Game game;


    public void getConfig(){
        config.getNode("DebugTool").getValue();
    }



    public Logger getLogger(){
        return logger;
    }

    public Path getConfigDir(){
        return configDir;
    }

    public SettingManager getSettingManager(){
        return settingManager;
    }

    @Listener
    public void onInit(GameInitializationEvent e){

        CommandSpec godCMD = CommandSpec.builder()
                .description(Text.of("God mode plugin"))
                .executor(new GodExec())
                .permission(DebugerPermission)
                .arguments(GenericArguments.optional(
                        GenericArguments.none(),
                        GenericArguments.onlyOne(
                                GenericArguments.player(Text.of("target"))
                        )
                ))

                .build();

        CommandSpec scanMod = CommandSpec.builder()
                .description(Text.of("Permise nebo blockinfo"))
                .permission(DebugerPermission)
                .executor(new debuger())
                .build();

        game.getCommandManager().register(this, godCMD,"bozimod");
        game.getCommandManager().register(this,scanMod,"debugmode");
        game.getEventManager().registerListeners(this, new DamageListener());
        game.getEventManager().registerListeners(this, new APPermBlock());
        game.getEventManager().registerListeners(this, new TierSystem());

    }


    public class DamageListener{
        GodExec gExec;


        @Listener
        public void onDamageEvent(DamageEntityEvent e){
            Entity ent = e.getTargetEntity();
            if(!(ent instanceof Player)) return;
            Player player = (Player)ent;
            gExec = new GodExec();
            List<String> gods = gExec.getList();

            if(!gods.contains(player.getName())) return;
            e.setCancelled(true);

        }

    }

    public static void runConsoleCommand(String Command){
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Command);
    }

    public static boolean cantDO(Player player, String BlockInfo, String Deny){
        String Block_AllInfo = BlockInfo;
        String Block_Type = Parse_Block_Name(Block_AllInfo);
        String Block_Mod = Parse_Block_Mod(Block_AllInfo);
        String Block_Info = Parse_Block_Info(Block_AllInfo);

        if(player.hasPermission(tutorial.BlockPermission + Block_Mod)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":" + Block_Type)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,Deny));

            if(tutorial.PermDebuger.contains(player.getName())) {
                player.sendMessage(Text.of(tutorial.DenyPermNeeded));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod + ":" + Block_Type));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + ":" + Block_Info));
            }

            return true;
        }

        if(player.hasPermission(tutorial.BlockPermission + Block_Mod + ":" + Block_Type)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,Deny));

            if(tutorial.PermDebuger.contains(player.getName())) {
                player.sendMessage(Text.of(tutorial.DenyPermNeeded));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod ));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod + ":" + Block_Type ));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info));
            }

            return true;
        }

        if(player.hasPermission(tutorial.BlockPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,Deny));

            if(tutorial.PermDebuger.contains(player.getName())) {
                player.sendMessage(Text.of(tutorial.DenyPermNeeded));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod + ":" + Block_Type));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info));
            }

            return true;
        }

        return false;
    }

    public static String Parse_Block_Name(String BlockName){
        String[] Parse_Block_Name = BlockName.split("\\[");
        String[] Parse_Block_Type = Parse_Block_Name[0].split(":");
        String Block_Type = Parse_Block_Type[1];
        return Block_Type;
    }
    public static String Parse_Block_Mod (String BlockName){
        String[] Parse_Block_Mod = BlockName.split(":");
        String Block_Mod = Parse_Block_Mod[0];
        return Block_Mod;
    }
    public static String Parse_Block_Info (String BlockName){
        String Block_Info = null;
        if(BlockName.contains("type=")) {
            String[] Parse_Block_Info = BlockName.split("type=");
            Block_Info = Parse_Block_Info[1].substring(0,(Parse_Block_Info[1].length()-1));
            return Block_Info;
        }
        if(BlockName.contains("variant=")) {
            String[] Parse_Block_Info = BlockName.split("variant=");
            Block_Info = Parse_Block_Info[1].substring(0,(Parse_Block_Info[1].length()-1));
            return Block_Info;
        }
        return Block_Info;
    }
}



