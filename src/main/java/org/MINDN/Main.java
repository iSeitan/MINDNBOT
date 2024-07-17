package org.MINDN;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static javax.management.timer.Timer.ONE_MINUTE;

class Main {

    // Create a scheduled executor with 1 thread
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final String SCAM_ALERT_MESSAGE = "As our community continues to grow, it is important to stay vigilant against potential scams. Please keep the following in mind:\n" +
            "\n" +
            "1. Do Not Trust Direct Messages (DMs): Our team will never reach out to you via direct message for personal information, investment opportunities, or any other sensitive matters.\n" +
            "2. Follow Pinned Messages: Always refer to the pinned messages in our official channels for the most accurate and up-to-date information. These messages are the only official source of communication from our team.\n" +
            "3. Report Suspicious Activity: If you encounter any suspicious activity or receive unsolicited messages, please report them to our moderators immediately.\n" +
            "Your safety and security are our top priorities. Stay informed and protect yourself against scams.";

    // create telegram api bot with bot token
    public static void main(String[] args) throws InterruptedException {
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");

        if (botToken == null || botToken.isBlank()) {
            System.out.println("TELEGRAM_BOT_TOKEN is not set");
            return;
        }

        TelegramBot bot = new TelegramBot(botToken);
        int offset = 0;

        executor.scheduleAtFixedRate(() -> broadcastScamMessage(bot), 0, ONE_MINUTE, TimeUnit.SECONDS);

        while (true) {
            GetUpdates getUpdates = new GetUpdates().limit(20).offset(offset).timeout(30);
            GetUpdatesResponse updatesResponse = bot.execute(getUpdates);

            List<Update> updates = updatesResponse.updates();

            for (Update update : updates) {
                processUpdate(bot, update);
                offset = update.updateId() + 1;
            }

            Thread.sleep(5000);
        }
    }

    // This method is called periodically to send the scam alert message to all active channels
    private static void broadcastScamMessage(TelegramBot bot) {
        System.out.println("Broadcasting scam message...");  // Add this line
        for (Long channelId : channelIds) {
            SendMessage request = new SendMessage(channelId, SCAM_ALERT_MESSAGE);
            bot.execute(request);
        }
    }

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .build();
    // channelIds variable and telegram channel list
    private static final List<Long> channelIds = Arrays.asList(1002237433490L, 1002163410173L, 1002057091895L);
    // Add the token address here
    private static String tokenAddress = "4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
    // Telegram bot process
    private static void processUpdate(TelegramBot bot, Update update) {
        // Check if update is a message and is not null
        if (update.message() != null) {
            // Add your keywords here for the filters *not implemented yet*
            List<String> keywords = Arrays.asList("CA", "scam", "chart", "price");
            String messageText = update.message().text();

            // If messageText is null, skip this iteration
            if (messageText == null) {
                return; // skips executing the rest of this iteration (i.e., goes to next update)
            }

            // Check if the message is a command or contains a keyword
            if (!(messageText.startsWith("/") || keywords.stream().anyMatch(messageText::contains))) {
                return; // skips executing the rest of this iteration (i.e., goes to next update)
            }
            // If @ is present, this will separate the command from the bot's username. If not, this will just result in the same original command.
            String command = messageText.split("@")[0];

            // Process known commands
            switch (command) {
                case "/work" -> {
                    String workMessage = "**Reminder that the official MindBlown socials network works as follow:**\n" +
            " \n" +
            "Our own bot: @MindblownBot\n" +
            "https://linktr.ee/mindn\n" +
            " \n" +
            "$MINDN is the upgraded and decentralized version of channel points. You earn via your engagement in the community. **We’re MindBlown**\n" +
            " \n" +
            "**SOCIALS**\n" +
            "MindBlown Telegram News channel: https://t.me/MindBlownProject\n" +
            "MindBlown Telegram Chat and memes: https://t.me/MindBlowngraphicsmemes/1\n" +
            "MindBlown Telegram Raid competition and shilling: https://t.me/MindBlownCommunity\n" +
            "MindBlown Subreddit: https://www.reddit.com/r/ProjectMindBlown/\n" +
            "MindBlown Founder X: https://x.com/SeitanSurKick\n" +
            "MindBlown Discord invite link: https://discord.gg/93XJtRQWkW\n" +
            " \n" +
            "**IS MINDBLOWN A SCAM?**\n" +
            "RugCheck $MINDN please, we're real. https://www.solanatracker.io/tokens/4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545\n" +
            "Burnt LP Token: https://solscan.io/tx/3usMDeJyfFeKBrr6piNKwqMLUCBPqL14TQzeXPTEp5J3fqEWtT9gvxvhK6daES9pCFHHLgwncW4MHbyEabEPLkGZ\n" +
            " \n" +
            "To get the list of commands, please do /worklist";

                    bot.execute(new SendMessage(update.message().chat().id(), workMessage));
                }
                case "/price" -> {
                    String tokenPrice = fetchTokenPrice(tokenAddress);
                    bot.execute(new SendMessage(update.message().chat().id(), tokenPrice));
                }
                case "/scam" -> {
                    String scamMessage = "As our community continues to grow, it is important to stay vigilant against potential scams.\n" +
                            " \n" +
                            "Please keep the following in mind:\n" +
                            " \n" +
                            "$MINDN is the upgraded and decentralized version of channel points. You earn via your engagement in the community. We’re MindBlown!\n" +
                            " \n" +
                            "SOCIALS\n" +
                            "1. Do Not Trust Direct Messages (DMs): Our team will never reach out to you via direct message for personal information, investment opportunities, or any other sensitive matters.\n" +
                            " \n" +
                            "2. Follow Pinned Messages: Always refer to the pinned messages in our official channels for the most accurate and up-to-date information. These messages are the only official source of communication from our team. \n" +
                            " \n" +
                            "3. Report Suspicious Activity: If you encounter any suspicious activity or receive unsolicited messages, please report them to our moderators immediately. \n" +
                            " \n" +
                            "Your safety and security are our top priorities. Stay informed and protect yourself against scams.\n";
                    bot.execute(new SendMessage(update.message().chat().id(), scamMessage));
                }
                case "/guides" -> {
                    String url = "https://t.me/MindBlownProject/196";
                    bot.execute(new SendMessage(update.message().chat().id(), "Here's how you can earn passive rewards on your crypto and MINDN. A little bit like staking your crypto, but with liquidity pools. You can inject your own Raydium liquidity pool, Sol to $MINDN." + url));
                }
                case "/ca" -> {
                    String ca = "4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), ca));
                }
                case "/twitter" -> {
                    String ca = "https://x.com/mindblownsol";
                    bot.execute(new SendMessage(update.message().chat().id(), ca));
                }
                case "/chart" -> {
                    String ca = "https://www.geckoterminal.com/solana/pools/GXvnPwpJs22Q6YvUr6eA9EJV7Dt23RUH6m7jw9DW8o48";
                    bot.execute(new SendMessage(update.message().chat().id(), ca));
                }
                case "/raydium" -> {
                    String url = "https://raydium.io/swap/?inputMint=sol&outputMint=4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), "You can buy $MINDN on Raydium: " + url));
                }
                case "/buy" -> {
                    String url = "https://raydium.io/swap/?inputMint=sol&outputMint=4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), "You can buy $MINDN: " + url));
                }
                case "/jupiter" -> {
                    String url = "https://jup.ag/swap/SOL-4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), "You can buy $MINDN on Jupiter: " + url));
                }
                case "/worklist" -> {
                    bot.execute(new SendMessage(update.message().chat().id(), "Use the following commands [/work, /worklist, /guides, /ca, /chart, /price, /scam, /twitter, /buy, /raydium, /jupiter]"));
                }
            }
        }
    }

    private static String fetchTokenPrice(String tokenAddress) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("public-api.birdeye.so")
                .addPathSegment("defi")
                .addPathSegment("price")
                .addQueryParameter("address", tokenAddress)
                .addQueryParameter("chainId", "101")
                .build();
    
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-API-KEY", "{API_KEY}")
                .build();
    
        try {
            Response response = httpClient.newCall(request).execute();
    
            if (!response.isSuccessful()) {
                return "Error: " + response;
            }
    
            JSONObject jsonObject = new JSONObject(response.body().string());
            double price = jsonObject.getJSONObject("data").getDouble("value");
            DecimalFormat df = new DecimalFormat("0.000000000000"); 
            return "The current price of 1 $MINDN is: " + "$" + df.format(price) + " USDon Raydium.io /buy";
        } catch (IOException e) {
            return "Exception when making request - " + e.getMessage();
        }
    }
}