package Events;

import Commands.TierSystem;
import net.appartus.Tutorial.tutorial;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;

/**
 * Created by Alois on 27.11.2016.
 */
public class APPermBlock {


    @Listener
    public void onPlayerPlace(ChangeBlockEvent.Place event, @First Player player){

        if(player == null) return;

        String Block_AllInfo = event.getTransactions().get(0).getFinal().getState().getName();

        if(tutorial.DetailDebuger.contains(player.getName())) return;
        //if(tutorial.PermDebuger.contains(player.getName())) return;
        event.setCancelled(cantDO(player,Block_AllInfo));

    }
    @Listener
    public void onInteractBlock (InteractBlockEvent event, @First Player player){
        if(player == null) return;



        if(tutorial.DetailDebuger.contains(player.getName())) return;

        String Block_AllInfo = event.getTargetBlock().getState().getName();
        String Block_Type = Parse_Block_Name(Block_AllInfo);
        String Block_Mod = Parse_Block_Mod(Block_AllInfo);
        String Block_Info = Parse_Block_Info(Block_AllInfo);

            if (player.getItemInHand(MAIN_HAND).toString().contains(tutorial.DebugTool))
                if(tutorial.PermDebuger.contains(player.getName())) {
                    if (Block_Info != null) {
                        player.sendMessage(Text.of(TextColors.RED,tutorial.BlockPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info));
                        return;
                    }
                    if (Block_Info == null) {
                        player.sendMessage(Text.of(TextColors.RED,tutorial.BlockPermission + Block_Mod + ":" + Block_Type));
                        return;
                    }
                }
            if(tutorial.DetailDebuger.contains(player.getName())) {
                player.sendMessage(Text.of(Block_AllInfo.toString()));
                player.sendMessage(Text.of(player.getItemInHand(MAIN_HAND).toString()));
                return;
            }
            event.setCancelled(cantDO(player,Block_AllInfo));
    }


    public static boolean cantDO(Player player, String BlockInfo){
        String Block_AllInfo = BlockInfo;
        String Block_Type = Parse_Block_Name(Block_AllInfo);
        String Block_Mod = Parse_Block_Mod(Block_AllInfo);
        String Block_Info = Parse_Block_Info(Block_AllInfo);

        if(player.hasPermission(tutorial.BlockPermission + Block_Mod)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,"Tvoje třída má zakázaný tento mod!"));
            if(player.hasPermission(tutorial.DebugerPermission)) {
                player.sendMessage(Text.of("Pro povolení je třeba mít tyto permise:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + ":" + Block_Info + "  Nebo:"));
            }
            return true;
        }
        if(player.hasPermission(tutorial.BlockPermission + Block_Mod + ":" + Block_Type)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type)) return false;
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":"+ Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,"Tvoje třída má zakázané všechny varianty tohoto předmětu!"));
            if(player.hasPermission(tutorial.DebugerPermission)) {
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + ":" + Block_Info + "  Nebo:"));
            }
            return true;
        }
        if(player.hasPermission(tutorial.BlockPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) {
            if(player.hasPermission(tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) return false;
            player.sendMessage(Text.of(TextColors.RED,"Tvoje třída má zakázaný tento předmět!"));
            if(player.hasPermission(tutorial.DebugerPermission)) {
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + "  Nebo:"));
                player.sendMessage(Text.of(TextColors.AQUA,tutorial.AllowPermission + ":" + Block_Mod + ":" + Block_Type + ":" + Block_Info + "  Nebo:"));
            }
            return true;
        }
        //player.sendMessage(Text.of("Vyskytla se nejaka chyba"));
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
