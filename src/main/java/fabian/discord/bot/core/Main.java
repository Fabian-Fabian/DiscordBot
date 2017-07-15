package fabian.discord.bot.core;

import fabian.discord.bot.listener.ReadyListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) {

        if(args.length < 1 || args[0].length() != 59) {
            System.err.println("[ERROR] Please enter a valid Discord-Bot Token as parameter!");
            System.exit(-1);
        }

        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(args[0])
                .addEventListener(new ReadyListener());

        try {
            JDA jda = jdaBuilder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
