package fabian.discord.bot.commands;

import fabian.discord.bot.util.Statics;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class cmdYoutubeSearch implements Command {

    private YouTube youtube;

    @Override
    public String help() {
        return "Searches YouTube for a given sentence!";
    }

    @Override
    public void action(ArrayList<String> args, MessageReceivedEvent event) {
        try {
            if (args.size() == 0) {
                event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("ERROR").setColor(Color.RED).setDescription("Please enter a search term!").build()).queue();
                return;
            }

            if (Statics.settings.getProperty("googleapikey") == null || Objects.equals(Statics.settings.getProperty("googleapikey"), "ENTER VALID GOOGLE API KEY!")) {
                event.getTextChannel().sendMessage(new EmbedBuilder().setDescription("Please enter a correct API Key in bot.config!").setTitle("ERROR").setColor(Color.RED).build()).queue();
                return;
            }

            if (youtube == null) {
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
                }).setApplicationName("fabian-discord-bot").build();
            }

            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(Statics.settings.getProperty("googleapikey"));

            StringBuilder stringBuilder = new StringBuilder();
            for (String s : args)
                stringBuilder.append(s).append(" ");
            search.setQ(stringBuilder.toString());

            search.setType("video");
            search.setFields("items(id/kind,snippet/title,snippet/channelTitle,id/videoId)");
            search.setMaxResults(5L);

            SearchListResponse response = search.execute();
            List<SearchResult> searchResultList = response.getItems();
            if (searchResultList == null) {
                throw new Exception("YouTube Api returned null");
            }


            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Results for Search: " + stringBuilder.toString());
            embedBuilder.setColor(Color.GREEN);

            for (SearchResult result : searchResultList)
                embedBuilder.addField(result.getSnippet().getTitle(), "Creator: " + result.getSnippet().getChannelTitle() + "   " + "Link: " + "https://www.youtube.com/watch?v=" + result.getId().getVideoId(), false);

            event.getTextChannel().sendMessage(embedBuilder.build()).queue();
        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            event.getTextChannel().sendMessage(new EmbedBuilder().setDescription(sw.toString()).build()).queue();
        }
    }
    @Override
    public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
        return true;
    }
}
