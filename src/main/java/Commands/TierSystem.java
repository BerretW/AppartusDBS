package Commands;

import Events.APPermBlock;
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
 */

public class TierSystem {


    public void setPlayerTier(Player SrcPlayer) {

    }

    private void learnPlayerBlock(Player SrcPlayer){
        int line = GetUserLine(SrcPlayer);
        String Target = tutorial.PermAction.get(line).get(1);
        String Block = tutorial.PermAction.get(line).get(2);
        String Command = tutorial.PermCommand.replace("$user",Target);
        Player TargetPlayer = Sponge.getServer().getPlayer(tutorial.PermAction.get(line).get(1)).get();


        if(tutorial.PermAction.get(line).get(1) == null) {
            SrcPlayer.sendMessage(Text.of("Nemáš vybraného hráče!"));

            return;
        }
        if(tutorial.PermAction.get(line).get(2) == null) {
            SrcPlayer.sendMessage(Text.of("Nemáš vybraný blok!"));

            return;
        }
        if (GetPlayerLevel(TargetPlayer) <= 29){
            SrcPlayer.sendMessage(Text.of(TextColors.RED,"Tebou vybrany hrac nema dost vysokou uroven aby se mohl naucit tento blok"));

            return;
        }
        SrcPlayer.sendMessage(Text.of(TextColors.BLUE,String.format("Naučení tohoto předmětu stálo hráče: %1, %2 Levelů.",Target,tutorial.BlockLearnLevel)));

        tutorial.runConsoleCommand("xp -"+tutorial.BlockLearnLevel +"L " + Target);

        Command = Command.replace("$perm",tutorial.AllowPermission + Block);
        tutorial.runConsoleCommand(Command);
        SrcPlayer.sendMessage(Text.of(String.format("Naučil jsi hráče %1 používat blok %2",Target,Block)));

        Sponge.getServer().getConsole().sendMessage(Text.of("Hrac " + SrcPlayer.getName() + " naucil hrace " + Target + " pouzivat blok " + Block));
        tutorial.PermAction.get(line).set(1,null);
        tutorial.PermAction.get(line).set(2,null);

    }


    private int GetPlayerLevel(Player player){
        //Integer x = 20;
        //player.get(Keys.EXPERIENCE_LEVEL).get()= x;
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

            player.sendMessage(Text.of("Byl jsi pridan na seznam"));
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


    private boolean CanDo(Player player,String Block){
        String[] Block_AllInfo = Block.split(":");
        String Block_Mod = Block_AllInfo[0];
        String Block_Type = Block_AllInfo[1];
        String Block_Info = null;

        if (Block_AllInfo[2] != null) Block_Info = Block_AllInfo[2];

        if (player.getItemInHand(MAIN_HAND).toString().contains(tutorial.TierTool)) {
            if (player.hasPermission(tutorial.BlockPermission + "." + Block_Mod)){
                if (!player.hasPermission(tutorial.AllowPermission + "." + Block_Mod)) {
                    player.sendMessage(Text.of("Nemáš oprávnění učit kohokoliv s tímto blokem"));
                    return false;
                }
            }
            if (player.hasPermission(tutorial.BlockPermission  + Block_Mod + ":" + Block_Type)){
                if (!player.hasPermission(tutorial.AllowPermission  + Block_Mod + ":" + Block_Type)) {
                    player.sendMessage(Text.of("Nemáš oprávnění učit kohokoliv s tímto blokem"));
                    return false;
                }
            }
            if (player.hasPermission(tutorial.BlockPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)){
                if (!player.hasPermission(tutorial.AllowPermission + Block_Mod + ":" + Block_Type + ":" + Block_Info)) {
                    player.sendMessage(Text.of("Nemáš oprávnění učit kohokoliv s tímto blokem"));
                    return false;
                }
            }
            return true;
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

                //player.sendMessage(Text.of(tutorial.PermAction.size()));
                //player.sendMessage(Text.of(tutorial.PermAction.get(0).size()));

                int line = GetUserLine(player);

                tutorial.PermAction.get(line).set(1, Target_Player_Name);

                player.sendMessage(Text.of("Vybral jsi hráče: " + tutorial.PermAction.get(line).get(1)));
                //player.sendMessage(Text.of(tutorial.PermAction.get(line).toString()));

                TargetedPlayer.sendMessage(Text.of("Byl jsi vybrán hráčem " + player.getName() + "."));
                learnPlayerBlock(player);

                //String command ="gamemode 1 " + Target_Player_Name;
                //

            }
        }
    }



    @Listener
    public void getTargetBlockName (InteractBlockEvent.Secondary event, @First Player player){
        int line;
        String TargetedBlock;
        String Block_AllInfo = event.getTargetBlock().getState().getName();
        String Block_Type = APPermBlock.Parse_Block_Name(Block_AllInfo);
        String Block_Mod = APPermBlock.Parse_Block_Mod(Block_AllInfo);
        String Block_Info = APPermBlock.Parse_Block_Info(Block_AllInfo);

        TargetedBlock = Block_Mod + ":" + Block_Type + ":" + Block_Info;

        if(CanDo(player,TargetedBlock)) {
            if (Block_Info == null) TargetedBlock = Block_Mod + ":" + Block_Type;
            line = GetUserLine(player);
            tutorial.PermAction.get(line).set(2, TargetedBlock);
            player.sendMessage(Text.of("Vybral jsi blok " + tutorial.PermAction.get(line).get(2)));
            learnPlayerBlock(player);
        }
    }
}


