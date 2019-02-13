package core;

import commands.CmdAutorole;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class AutoroleHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        Guild g = event.getGuild();
        Member member = event.getMember();

        List<String> roles = CmdAutorole.getAutoroles(g.getId());
        if (roles != null && !roles.isEmpty())
        for (String r : roles) {

            g.getController().addSingleRoleToMember(member, g.getRoleById(r)).queue();

        }
    }
}
