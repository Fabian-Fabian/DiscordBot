package fabian.discord.bot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public interface Command {

    String help();
    void action(ArrayList<String> args, MessageReceivedEvent event);
    boolean requirements( ArrayList<String> args, MessageReceivedEvent event);

}
