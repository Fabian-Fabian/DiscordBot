package fabian.discord.bot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class cmdGG implements Command {
	@Override
	public String help() {
		return "Says GG!";
	}

	@Override
	public void action(ArrayList<String> args, MessageReceivedEvent event) {
		if(event.getJDA().getGuilds().contains(event.getJDA().getGuildById("328518349493370880"))) {
			event.getTextChannel().sendMessage(event.getJDA().getEmotesByName("gg",true).get(0).getAsMention()).queue();
			event.getMessage().delete().queue();
		} else
			System.out.println("[ERROR] Bot not on Server!");
	}

	@Override
	public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
		return true;
	}
}
