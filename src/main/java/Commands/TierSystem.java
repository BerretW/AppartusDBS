package Commands;


import net.appartus.Tutorial.tutorial;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;

/**
 * Created by Alois on 03.12.2016.
 * TierSystem class
 */

public class TierSystem {


    public void setPlayerTier(Player SrcPlayer) {

    }

    private void learnPlayerBlock(Player SrcPlayer){
        int line = GetUserLine(SrcPlayer);
        String Target = tutorial.PermAction.get(line).get(1);
        String Block = tutorial.PermAction.get(line).get(2);

        if(tutorial.PermAction.get(line).get(1) == null) {
            SrcPlayer.sendMessage(Text.of(TextColors.RED,tutorial.NotSelectedPlayer));
            ClearSelection(line);
            return;
        }
        if(tutorial.PermAction.get(line).get(2) == null) {
            SrcPlayer.sendMessage(Text.of(TextColors.RED,tutorial.NotSelectedBlock));
            ClearSelection(line);
            return;
        }

        Player TargetPlayer = Sponge.getServer().getPlayer(tutorial.PermAction.get(line).get(1)).get();

        if(TargetPlayer.hasPermission(tutorial.AllowPermission + Block)){
            SrcPlayer.sendMessage(Text.of(TextColors.RED,tutorial.AlreadyKnow));
            return;
        }

        if (GetPlayerLevel(TargetPlayer) <= tutorial.BlockLearnLevel){
            SrcPlayer.sendMessage(Text.of(TextColors.RED,String.format(tutorial.LowLevel, Target, GetPlayerLevel(TargetPlayer), tutorial.BlockLearnLevel)));
            return;
        }


        String Command = tutorial.PermCommand.replace("$user",Target);
        Command = Command.replace("$perm",tutorial.AllowPermission + Block);
        tutorial.runConsoleCommand(Command);

        tutorial.runConsoleCommand("xp -"+tutorial.BlockLearnLevel +"L " + Target);

        SrcPlayer.sendMessage(Text.of(TextColors.BLUE,String.format(tutorial.LernBlockPlayer ,Target,Block,tutorial.BlockLearnLevel)));
        ClearSelection(line);
    }

    private void ClearSelection(int Line){
        tutorial.PermAction.get(Line).set(1,null);
        tutorial.PermAction.get(Line).set(2,null);
    }
    private int GetPlayerLevel(Player player){
        return player.get(Keys.EXPERIENCE_LEVEL).get();
    }

    private int GetUserLine (Player player){
        int x = 0;
        String UserName = player.getName() ;
        if(!UserExist(UserName)){
            ArrayList<String> inner = new ArrayList<>();
            inner.add(0,UserName);
            inner.add(1,null);
            inner.add(2,null);
            tutorial.PermAction.add(inner);

            //player.sendMessage(Text.of("Byl jsi pridan na seznam" ));
        }

        for (int i = 0; i < tutorial.PermAction.size(); i++) {
            if (tutorial.PermAction.get(i).get(0).contains(UserName)) {
                x = i;
                break;
            }
        }
        return x;

    }


    private boolean UserExist(String PlayerName){
        if (tutorial.PermAction.size() == 0) return false;
        for (int i = 0; i < tutorial.PermAction.size(); i++) {

            if((tutorial.PermAction.get(i).get(0).contains(PlayerName))) return true;

        }
        return false;
    }




    @Listener
    public void getTargetPlayerName (InteractEntityEvent.Primary event, @First Player player){

        if (player.getItemInHand(MAIN_HAND).toString().contains(tutorial.TierTool)) {
            String TargetPlayerInfo = event.getTargetEntity().toString();

            if((event.getTargetEntity() instanceof Player)) {
                Player TargetedPlayer;
                String[] TargetPlayerName = TargetPlayerInfo.split("\\['");
                String Target_Player_Name = TargetPlayerName[1].split("'")[0];
                TargetedPlayer = Sponge.getServer().getPlayer(Target_Player_Name).get();


                int line = GetUserLine(player);

                tutorial.PermAction.get(line).set(1, Target_Player_Name);

                player.sendMessage(Text.of(TextColors.AQUA,String.format(tutorial.SelectedPlayer,tutorial.PermAction.get(line).get(1))));


                TargetedPlayer.sendMessage(Text.of(TextColors.AQUA,String.format( tutorial.SelectedByPlayer, player.getName())));
                learnPlayerBlock(player);

            }
        }
    }



    @Listener
    public void getTargetBlockName (InteractBlockEvent.Primary event, @First Player player){

        int line;
        String TargetedBlock;
        String Block_AllInfo = event.getTargetBlock().getState().getName();
        String Block_Type = tutorial.Parse_Block_Name(Block_AllInfo);
        String Block_Mod = tutorial.Parse_Block_Mod(Block_AllInfo);
        String Block_Info = tutorial.Parse_Block_Info(Block_AllInfo);
        TargetedBlock = Block_Mod + ":" + Block_Type + ":" + Block_Info;
        if (Block_Info == null) TargetedBlock = Block_Mod + ":" + Block_Type;

        if (!(player.getItemInHand(MAIN_HAND).toString().contains(tutorial.TierTool))) return;


        if(!tutorial.cantDO(player,TargetedBlock,tutorial.DenyPermToLearn)) {
            line = GetUserLine(player);
            tutorial.PermAction.get(line).set(2, TargetedBlock);
            player.sendMessage(Text.of(TextColors.AQUA,String.format(tutorial.SelectedBlock, tutorial.PermAction.get(line).get(2))));
            learnPlayerBlock(player);
        }
    }
}


