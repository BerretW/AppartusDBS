package Commands;

import net.appartus.Tutorial.tutorial;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

/**
 * Created by Alois on 28.11.2016.
 */
public class debuger implements CommandExecutor {
    public static String debugmode;

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (debugmode == "Off") {
            debugmode = "All";
            tutorial.PermDebuger.remove(src.getName());
            tutorial.DetailDebuger.add(src.getName());
            src.sendMessage(Text.of("Debug mod nastaven na režim detailu"));
            return CommandResult.success();
        }
        if (debugmode == "All") {
            debugmode = "Perm";
            tutorial.DetailDebuger.remove(src.getName());
            tutorial.PermDebuger.add(src.getName());
            src.sendMessage(Text.of("Debug mod nastaven na režim permise"));
            return CommandResult.success();
        }
        debugmode = "Off";
        if(tutorial.PermDebuger.contains(src.getName())) tutorial.PermDebuger.remove(src.getName());
        if(tutorial.DetailDebuger.contains(src.getName())) tutorial.DetailDebuger.remove(src.getName());
        src.sendMessage(Text.of("Debug mod vypnut"));
        return CommandResult.success();
    }

}
