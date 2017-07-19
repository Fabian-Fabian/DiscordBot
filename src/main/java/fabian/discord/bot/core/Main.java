package fabian.discord.bot.core;

import fabian.discord.bot.commands.*;
import fabian.discord.bot.listener.ReadyListener;
import fabian.discord.bot.listener.messageListener;
import fabian.discord.bot.util.Statics;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

class Main {

    public static void main(String[] args) {

        if(Statics.settings.getProperty("owner-id") == null)
            Statics.settings.setProperty("owner-id","ENTER ID HERE");

        if(Statics.settings.getProperty("prefix") == null)
            Statics.settings.setProperty("prefix", "!");

        if(Statics.settings.getProperty("bitlyAPIKEY") == null)
            Statics.settings.setProperty("bitlyAPIKEY","ENTER VALID BITLY API KEY!");

        if(Statics.settings.getProperty("googleapikey") == null)
            Statics.settings.setProperty("googleapikey","ENTER VALID GOOGLE API KEY!");

        if (args.length < 1 || args[0].length() != 59) {
            System.err.println("[ERROR] Please enter a valid Discord-Bot Token as parameter!");
            System.exit(-1);
        }

        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(args[0])
                .addEventListener(new ReadyListener())
                .addEventListener(new messageListener());

        commandHandler.add("help", new cmdHelp());
        commandHandler.add("short", new cmdShortLink());
        commandHandler.add("ytsearch", new cmdYoutubeSearch());
        commandHandler.add("set",new cmdSet());
        commandHandler.add("gg",new cmdGG());

        try {
            //noinspection unused
            JDA jda = jdaBuilder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
