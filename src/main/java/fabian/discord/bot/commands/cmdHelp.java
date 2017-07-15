package fabian.discord.bot.commands;

import fabian.discord.bot.core.commandHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;

public class cmdHelp implements Command{
    @Override
    public String help() {
        return "Displays this Help";
    }

    @Override
    public void action(ArrayList<String>  args, MessageReceivedEvent event) {
        ArrayList<Command> commands = commandHandler.getCommands();

        EmbedBuilder help = new EmbedBuilder();

        help.setColor(Color.cyan);

        for (Command c:commands) {
            help.addField(commandHandler.getName(c), c.help(), false);
        }

        event.getTextChannel().sendMessage(help.build()).queue();

    }

    @Override
    public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
        return true;
    }
}
