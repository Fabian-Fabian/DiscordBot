package fabian.discord.bot.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import fabian.discord.bot.util.Statics;

import fabian.discord.bot.core.commandHandler;

public class messageListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {

        //Listen for commands
        if(event.getMessage().getContent().startsWith(Statics.settings.getProperty("prefix")) && !event.getMessage().getAuthor().isBot()) {
            if (!event.getMessage().getContent().startsWith(Statics.settings.getProperty("prefix") + "hide")) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    event.getMessage().delete().queue();
                });
                thread.start();
                commandHandler.handle(event.getMessage().getContent().substring(Statics.settings.getProperty("prefix").length()), event);
            } else {
                event.getMessage().delete().queue();
                commandHandler.handle(event.getMessage().getContent().replace(Statics.settings.getProperty("prefix") + "hide ",""),event);
            }
        }
    }

}
