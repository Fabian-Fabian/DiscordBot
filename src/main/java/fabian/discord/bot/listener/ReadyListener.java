package fabian.discord.bot.listener;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{
    @Override
    public void onReady(ReadyEvent event) {

        System.out.println("[INFO] Name of Bot: " + event.getJDA().getSelfUser().getName());

        if(event.getJDA().getGuilds().size() > 0) {
            System.out.println("[INFO] Bot is currently on following servers: ");

            for (Guild g : event.getJDA().getGuilds())
                System.out.println(g.getName());
        } else {
            System.out.println("[INFO] This Bot is currently on no servers!");
        }
    }
}
