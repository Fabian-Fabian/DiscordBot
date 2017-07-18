package fabian.discord.bot.commands;

import com.google.gson.JsonParser;
import fabian.discord.bot.util.Statics;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

public class cmdShortLink implements Command {
    @Override
    public String help() {
        return "Shorts links!";
    }

    @Override
    public void action(ArrayList<String> args, MessageReceivedEvent event) {
        if(args.size() == 1) {
            String enteredURL = args.get(0);
            if(!enteredURL.startsWith("http://") && !enteredURL.startsWith("https://"))
                enteredURL = "http://" + enteredURL;
            String accessToken = Statics.settings.getProperty("bitlyAPIKEY");
            String requestUrl;

            try {
                requestUrl = "https://api-ssl.bitly.com/v3/shorten?access_token=" + accessToken + "&longUrl=" + URLEncoder.encode(enteredURL, "UTF-8");

                URLConnection connection = new URL(requestUrl).openConnection();
                connection.connect();

                StringBuilder responseJsonBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    responseJsonBuilder.append(line);
                }

                String responseJson = responseJsonBuilder.toString();
                String returnValue = new JsonParser().parse(responseJson).getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString();

                if (Objects.equals(returnValue, ""))
                    returnValue = "ERROR";

                event.getTextChannel().sendMessage(new EmbedBuilder().setDescription("Shorted Link: " + returnValue).setTitle("Shorted Link:", returnValue).build()).queue();

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                event.getTextChannel().sendMessage(new EmbedBuilder().setDescription(sw.toString()).build()).queue();
            }
        } else
            event.getTextChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("ERROR").setDescription("Please enter a valid URL as parameter!").build()).queue();
    }

    @Override
    public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
        return true;
    }
}
