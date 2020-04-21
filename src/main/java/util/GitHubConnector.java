package util;

import core.ServerSettingsHandler;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;


import java.io.IOException;

public class GitHubConnector {

    private static GHRepository repo;

    private GitHubConnector() {

    }

    private static void repoConnect() {

        try {
            GitHub gitHub = GitHub.connectUsingPassword(ServerSettingsHandler.getGHLogin(), ServerSettingsHandler.getGHPW());
            repo = gitHub.getRepository("MemphisCornelius/MarvBot");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GHRepository getRepo() {
        if (repo == null) {
            repoConnect();
        }

        return repo;
    }
}
