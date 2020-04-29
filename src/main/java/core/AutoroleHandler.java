package core;

import commands.CmdSet;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class AutoroleHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (CmdSet.configList.containsKey(event.getGuild().getId()) &&
                CmdSet.configList.get(event.getGuild().getId()).containsKey("autorole")) {
            event.getGuild().getController().addSingleRoleToMember(
                    event.getMember(), event.getGuild().getRoleById(
                            CmdSet.configList.get(event.getGuild().getId()).get("autorole"))).queue();
        }
    }
}
