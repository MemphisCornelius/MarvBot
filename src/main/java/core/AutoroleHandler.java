package core;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;

public class AutoroleHandler extends ListenerAdapter {

    HashMap<String, List<String>> autoroles = commands.CmdAutorole.getAutorole();

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        Guild g = event.getGuild();
        Member member = event.getMember();

        List<String> roles = autoroles.get(g.getId());

        for (String r : roles) {

            g.getController().addSingleRoleToMember(member, g.getRoleById(r));

        }
    }
}
