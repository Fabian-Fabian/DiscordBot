package fabian.discord.bot.core;

import fabian.discord.bot.commands.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;

public class commandHandler {

    private static Map<String, Command> commandContainer = new HashMap<>();
    private static Map<Command, String> commandContainer2 = new HashMap<>();

    public static void handle(String command, MessageReceivedEvent event) {

        ArrayList<String> split = new ArrayList<>();

        Collections.addAll(split, command.split(" "));

        String com = split.get(0);
        ArrayList<String> arg = new ArrayList<>();
        split.remove(0);
        arg.addAll(split);

        StringBuilder args = new StringBuilder();
        for(String s : arg)
            args.append("\"").append(s).append("\"").append(" ");

        if(commandContainer.containsKey(com)) {
            if (commandContainer.get(com).requirements(arg, event)) {
                commandContainer.get(com).action(arg, event);
            } else {
                event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Sorry!").setDescription("You don't have enought permission to do that!").build()).queue();
            }
        } else {
            event.getTextChannel().sendMessage(new EmbedBuilder().setColor(Color.red).setTitle("Error").setDescription("Error! Command " + com + " not found!").build()).queue();
        }

    }

    static void add(String string, Command c){
        commandContainer.put(string, c);
        commandContainer2.put(c, string);
    }

    public static String getName(Command command) {
        return commandContainer2.get(command);
    }

    public static ArrayList<Command> getCommands() {

        ArrayList<Command> commandArrayList = new ArrayList<>();

        for (String key : commandContainer.keySet()) {
            commandArrayList.add(commandContainer.get(key));
        }

        return commandArrayList;

    }

}