package Commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alois on 25.11.2016.
 */
public class GodExec implements CommandExecutor {
    public static List<String> Gods = new ArrayList<String>();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException{
        Player target;
        if(!args.hasAny("target")){
            if(!(src instanceof Player)){
                src.sendMessage(Text.of("Console už bůh je"));
                return CommandResult.success();
            }
            target = (Player)src;
            if(Gods.contains(target.getName())){
                Gods.remove(target.getName());
                target.sendMessage(Text.of("Byl jsi odebrán ze seznamu bohů"));
                return CommandResult.success();
            }
            Gods.add(target.getName());
            target.sendMessage(Text.of("Byl jsi přidán na seznam bohů"));
            return CommandResult.success();
        }

        target = args.<Player>getOne("player").get();
        if(Gods.contains(target.getName())){
            Gods.remove(target.getName());
            target.sendMessage(Text.of("Byl jsi odebrán ze seznamu bohů"));
            return CommandResult.success();
        }
        Gods.add(target.getName());
        src.sendMessage(Text.of(target.getName(), "Byl přidán na seznam bohů"));
        target.sendMessage(Text.of("Byl jsi přidán na seznam bohů"));
        return CommandResult.success();


    }
    public List<String> getList(){
        return Gods;
    }
}
