package commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import util.Config;
import util.GitHubConnector;
import util.MessageMask;
import util.Time;

import java.awt.Color;
import java.io.IOException;

public class CmdGithubIssue implements Command {

    private String getLabelByNumber(String s) {
        switch (s) {
            case "1":
                return "bug";
            case "2":
                return "duplicate";
            case "3":
                return "enhancement";
            case "4":
                return "help wanted";
            case "5":
                return "invalid";
            case "6":
                return "question";
            default:
                return null;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length >= 2) {
            if (args[0].equals("1") || args[0].equals("2") || args[0].equals("3") || args[0].equals("4") || args[0].equals("5") || args[0].equals("6")) {
                String content = event.getMessage().getContentDisplay().replaceFirst(String.format("%s%s", Config.PREFIX, Config.CMD_GITHUBISSUE), "");
                content = content.replaceFirst(args[0], "");

                try {
                    GHIssueBuilder issueBuilder = GitHubConnector.getRepo().createIssue("Issue reported by" + event.getAuthor());
                    GHIssue issue = issueBuilder.
                            label(getLabelByNumber(args[0])).
                            body(content).
                            create();

                    String issueUrl = issue.getHtmlUrl().toString();

                    MessageMask.msg(tc, user, Color.green, "Done!\n\nYou cann follow your ticket here: " + issueUrl);

                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }


            } else {
                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                        "Invalid argument! Please enter a valid label number! \n\nLabel numbers:\n1: bug, 2: duplicate, 3: enhancement, " +
                                "4: help wanted, 5: invalid, 6: question");
            }
        } else {
            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                    String.format("Invalid arguments!\nPlease enter this command like this:\n`%s%s` <Label number> <Ticket description>\n\nLabel numbers:\n1: bug, 2: duplicate, 3: enhancement, " +
                            "4: help wanted, 5: invalid, 6: question", Config.PREFIX, Config.CMD_GITHUBISSUE));
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_GITHUBISSUE.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
