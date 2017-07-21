package fabian.discord.bot.listener;

import fabian.discord.bot.commands.cmdTranslate;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class onReactionAdd extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot())
            return;
        if(cmdTranslate.languageMessages.containsKey(event.getMessageId()) && cmdTranslate.getNavigationEmotes().contains(event.getReactionEmote().getName()))
            try {
                cmdTranslate.switchPage(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
