package fabian.discord.bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import fabian.discord.bot.util.Statics;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class cmdTranslate implements Command {

    private static TreeMap<String,String> languages;
    public static HashMap<String,Integer> languageMessages = new HashMap<>();
    private static ArrayList<String> navigationEmotes;

    @Override
    public String help() {
        return "Translates a sentence or word to given language!";
    }

    @Override
    public void action(ArrayList<String> args, MessageReceivedEvent event) {
        if(Statics.settings.getProperty("yandexapikey") == null) {
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("ERROR").setDescription("Please enter a correct Yandex API Key in bot.config!").setColor(Color.red).build()).queue();
            return;
        }

        try {
            switch(args.size()) {
                case 0:
                    printUsage(event);
                    break;
                case 1:
                    if(Objects.equals(args.get(0), "langs"))
                        printLangs(event);
                    else
                        printUsage(event);
                    break;
                default:
                    translate(event,args);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translate(MessageReceivedEvent event, ArrayList<String> args) throws Exception{
        boolean langExists = false;
        for(Map.Entry<String,String> entry : getLanguages().entrySet())
            if(Objects.equals(entry.getKey(), args.get(0)))
                langExists = true;
            else if(Objects.equals(entry.getValue(), args.get(0))) {
                args.set(0,entry.getKey());
                langExists = true;
            }
        if(!langExists) {
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("ERROR").setDescription("This language don't exists! Please enter a valid language code! To get all valid language codes enter \"" + Statics.settings.getProperty("prefix") + "translate langs\"!").setColor(Color.red).build()).queue();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : args.subList(1,args.size()))
            stringBuilder.append(s).append(" ");
        URLConnection connection = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + Statics.settings.getProperty("yandexapikey") + "&text=" + URLEncoder.encode(stringBuilder.toString(),"UTF-8") + "&lang=" + args.get(0)).openConnection();
        connection.connect();
        StringBuilder responseJsonBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            responseJsonBuilder.append(line);
        }

        String responseJson = responseJsonBuilder.toString();
        event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Result").setDescription("Result: " + new JsonParser().parse(responseJson).getAsJsonObject().get("text").getAsString()).setColor(Color.blue).setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png").build()).queue();
    }

    private void printUsage(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Usage:").setDescription(Statics.settings.getProperty("prefix") + "translate langs\nReturns all supported languages!\n\n" + Statics.settings.getProperty("prefix") + "translate <target language> <sentence>\nTranslates a sentence( or more)!").setColor(Color.blue).build()).queue();
    }

    private static TreeMap<String,String> getLanguages() throws Exception{
        if(languages == null) {
            URLConnection connection = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + Statics.settings.getProperty("yandexapikey") + "&ui=en").openConnection();
            connection.connect();
            StringBuilder responseJsonBuilder = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                responseJsonBuilder.append(line);
            }

            String responseJson = responseJsonBuilder.toString();
            Set<Map.Entry<String, JsonElement>> langs = new JsonParser().parse(responseJson).getAsJsonObject().get("langs").getAsJsonObject().entrySet();

            languages = new TreeMap<>();
            for(Map.Entry<String,JsonElement> entry : langs) {
                languages.put(entry.getKey(),entry.getValue().getAsString());
            }

        }
        return languages;
    }

    public static ArrayList<String> getNavigationEmotes() {
        if(navigationEmotes == null) {
            navigationEmotes = new ArrayList<>();
            navigationEmotes.add(Statics.emote_forward);
            navigationEmotes.add(Statics.emote_stop);
            navigationEmotes.add(Statics.emote_back);
        }
        return navigationEmotes;
    }

    private void printLangs(MessageReceivedEvent event) throws Exception{

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Languages:");
        embedBuilder.setColor(Color.green);

        int i = 0;
        for(Map.Entry<String,String> entry : getLanguages().entrySet()) {
            if(i == 25)
                break;
            embedBuilder.addField(entry.getKey(),entry.getValue(),true);
            i++;
        }

        embedBuilder.setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png");
        String messageID = event.getTextChannel().sendMessage(embedBuilder.build()).complete().getId();
        event.getTextChannel().addReactionById(messageID, Statics.emote_stop).queue();
        event.getTextChannel().addReactionById(messageID, Statics.emote_forward).queue();
        languageMessages.put(messageID, 0);
    }

    @Override
    public boolean requirements(ArrayList<String> args, MessageReceivedEvent event) {
        return true;
    }

    public static void switchPage(MessageReactionAddEvent event) throws Exception {
        switch(event.getReactionEmote().getName()) {
            case Statics.emote_forward:
                int pageForward = languageMessages.get(event.getMessageId());
                pageForward++;
                switchToPage(event, pageForward);
                break;
            case Statics.emote_stop:
                event.getTextChannel().getMessageById(event.getMessageId()).queue(message -> message.delete().queue());
                break;
            case Statics.emote_back:
                int pageBack = languageMessages.get(event.getMessageId());
                pageBack--;
                switchToPage(event, pageBack);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private static void switchToPage(MessageReactionAddEvent event, int page) throws Exception {
        Message message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        message.clearReactions().queue();
        switch(page) {
            case 0:
                EmbedBuilder embedBuilder1 = new EmbedBuilder();
                embedBuilder1.setTitle("Supported Languages:");

                int counter1 = 0;
                for(Map.Entry<String,String> entry : getLanguages().entrySet()) {
                    if(counter1 == 25)
                        break;
                    embedBuilder1.addField(entry.getKey(), entry.getValue(), true);
                    counter1++;
                }

                embedBuilder1.setColor(Color.green);
                embedBuilder1.setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png");
                message.editMessage(embedBuilder1.build()).queue();
                message.addReaction(Statics.emote_stop).queue();
                message.addReaction(Statics.emote_forward).queue();
                languageMessages.remove(message.getId());
                languageMessages.put(message.getId(),0);
                break;
            case 1:
                EmbedBuilder embedBuilder2 = new EmbedBuilder();
                embedBuilder2.setTitle("Supported Languages:");

                int counter2 = 0;
                for(Map.Entry<String,String> entry : getLanguages().entrySet()) {
                    if(counter2 < 25) {
                        counter2++;
                        continue;
                    }
                    if(counter2 == 50)
                        break;
                    embedBuilder2.addField(entry.getKey(), entry.getValue(), true);
                    counter2++;
                }

                embedBuilder2.setColor(Color.green);
                embedBuilder2.setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png");
                message.editMessage(embedBuilder2.build()).queue();
                message.addReaction(Statics.emote_back).queue();
                message.addReaction(Statics.emote_stop).queue();
                message.addReaction(Statics.emote_forward).queue();
                languageMessages.remove(message.getId());
                languageMessages.put(message.getId(),1);
                break;
            case 2:
                EmbedBuilder embedBuilder3 = new EmbedBuilder();
                embedBuilder3.setTitle("Supported Languages:");

                int counter3 = 0;
                for(Map.Entry<String,String> entry : getLanguages().entrySet()) {
                    if(counter3 < 50) {
                        counter3++;
                        continue;
                    }
                    if(counter3 == 75)
                        break;
                    embedBuilder3.addField(entry.getKey(), entry.getValue(), true);
                    counter3++;
                }

                embedBuilder3.setColor(Color.green);
                embedBuilder3.setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png");
                message.editMessage(embedBuilder3.build()).queue();
                message.addReaction(Statics.emote_back).queue();
                message.addReaction(Statics.emote_stop).queue();
                message.addReaction(Statics.emote_forward).queue();
                languageMessages.remove(message.getId());
                languageMessages.put(message.getId(),2);
                break;
            case 3:
                EmbedBuilder embedBuilder4 = new EmbedBuilder();
                embedBuilder4.setTitle("Supported Languages:");

                int counter4 = 0;
                for(Map.Entry<String,String> entry : getLanguages().entrySet()) {
                    if(counter4 < 75) {
                        counter4++;
                        continue;
                    }
                    if(counter4 == 100)
                        break;
                    embedBuilder4.addField(entry.getKey(), entry.getValue(), true);
                    counter4++;
                }

                embedBuilder4.setColor(Color.green);
                embedBuilder4.setFooter("Powered by Yandex.Translate","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex_logo_en.svg/2000px-Yandex_logo_en.svg.png");
                message.editMessage(embedBuilder4.build()).queue();
                message.addReaction(Statics.emote_back).queue();
                message.addReaction(Statics.emote_stop).queue();
                languageMessages.remove(message.getId());
                languageMessages.put(message.getId(),3);
                break;
            default:
                throw new IllegalStateException();
        }
    }
}