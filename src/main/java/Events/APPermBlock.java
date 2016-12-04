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
 * Appartus Block Deny System
 */
public class APPermBlock {


    @Listener
    public void onPlayerPlace(ChangeBlockEvent.Place event, @First Player player){

        if(player == null) return;

        String Block_AllInfo = event.getTransactions().get(0).getFinal().getState().getName();

        if(tutorial.DetailDebuger.contains(player.getName())) return;
        event.setCancelled(tutorial.cantDO(player,Block_AllInfo,tutorial.DenyPerm));

    }
    @Listener
    public void onInteractBlock (InteractBlockEvent event, @First Player player){
        if(player == null) return;



        if(tutorial.DetailDebuger.contains(player.getName())) return;

        String Block_AllInfo = event.getTargetBlock().getState().getName();
        String Block_Type = tutorial.Parse_Block_Name(Block_AllInfo);
        String Block_Mod = tutorial.Parse_Block_Mod(Block_AllInfo);
        String Block_Info = tutorial.Parse_Block_Info(Block_AllInfo);

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
                player.sendMessage(Text.of(Block_AllInfo));
                player.sendMessage(Text.of(player.getItemInHand(MAIN_HAND).toString()));
                return;
            }
            event.setCancelled(tutorial.cantDO(player,Block_AllInfo,tutorial.DenyPerm));
    }






}
