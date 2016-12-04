package net.appartus.Tutorial;


import Commands.GodExec;
import Commands.TierSystem;
import Commands.debuger;
import Events.APPermBlock;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Plugin(id="appartusbds", name="Appartus Block Deny System", version = "0.1")

public class tutorial {

    public static List<String> PermDebuger = new ArrayList<String>();
    public static List<String> DetailDebuger = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> PermAction = new ArrayList<ArrayList<String>>();
    public static String DebugTool = "item.carrotOnAStick";
    public static String TierTool = "item.shears";
    public static String BlockPermission = "appartus.block.";
    public static String AllowPermission = "appartus.allow.";
    public static String DebugerPermission = "appartus.debuger";
    public static String PermCommand = "pm users $user set permission $perm";
    public static String TierCommand = "pm users $user group add $tier";
    public static int BlockLearnLevel = 10;
    public static int TierLearnLevel = 10;

    @Inject
    Game game;

    @Inject
    Logger logger;


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
}



