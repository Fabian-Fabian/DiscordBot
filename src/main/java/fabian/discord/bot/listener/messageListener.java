package fabian.discord.bot.listener;

import fabian.discord.bot.core.commandHandler;
import fabian.discord.bot.util.Statics;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class messageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getMessage().getContent().startsWith(Statics.settings.getProperty("prefix")) && !event.getMessage().getAuthor().isBot()) {
            if (!event.getMessage().getContent().startsWith(Statics.settings.getProperty("prefix") + "hide")) {
                commandHandler.handle(event.getMessage().getContent().substring(Statics.settings.getProperty("prefix").length()), event);
            } else {
                event.getMessage().delete().queue();
                commandHandler.handle(event.getMessage().getContent().replace(Statics.settings.getProperty("prefix") + "hide ", ""), event);
            }
        }
    }
}
