package commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.util.Random;

public class CmdCoinflip implements Command {


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        User user = event.getAuthor();
        TextChannel tc = event.getTextChannel();

            int rn = new Random().nextInt(2);

            if (rn == 0) {
                MessageMask.msgWithPicture(tc, user, Color.gray,"https://cdn.discordapp.com/attachments/281445240081088513/457211741512925185/Marvbot_Coin_0.png","Head!");
            } else {
                MessageMask.msgWithPicture(tc, user, Color.gray,"https://cdn.discordapp.com/attachments/281445240081088513/457211743924387840/Marvbot_Coin_1.png","Tail!");
            }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_COINFLIP.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }


}
