package fabian.discord.bot.commands;

import fabian.discord.bot.util.Statics;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class cmdSet implements Command {
    @Override
    public String help() {
        return "Set properties of Bot Config";
    }

    @Override
    public void action(ArrayList<String> args, MessageReceivedEvent event) {
        if(args.size() != 2)
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("ERROR").setColor(Color.red).setDescription("Usage: " + Statics.settings.getProperty("prefix") + "set <property> <value>").build()).queue();

        if(Statics.settings.getProperty(args.get(0)) != null) {
            String previous = Statics.settings.getProperty(args.get(0));
            Statics.settings.setProperty(args.get(0), args.get(1));
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Success!").setDescription("Successfully set parameter \"" + args.get(0) + "\" to \"" + args.get(1) + "\"! Previous Value: \"" + previous + "\"!").setColor(Color.green).build()).queue();
        } else
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("ERROR").setDescription("Property " + args.get(0) + " does not exist!").setColor(Color.red).build()).queue();
    }

    @Override
    public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
        return Objects.equals(event.getAuthor().getId(), Statics.settings.getProperty("owner-id"));
    }
}
