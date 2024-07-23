package org.MemeUnit;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendPhoto;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import java.util.List;

public class Main {
    // Create a scheduled executor with 1 thread
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String botToken = System.getenv("MEMEUNITAPI").trim();
    private static final String tokenAddress = "4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
    private static int offset = 0;
    // create telegram api bot with bot token
    public static void main(String[] args) {
        if (botToken.isEmpty()) {
            logger.error("MEMEUNITAPI is not set or is invalid");
            return;
        }
        TelegramBot bot = new TelegramBot(botToken);

        // Poll for updates
        while (!Thread.currentThread().isInterrupted()) {
            try {
                GetUpdates getUpdates = new GetUpdates().limit(20).offset(offset).timeout(30);
                GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
                List<Update> updates = updatesResponse.updates();
                for (Update update : updates) {
                    processUpdate(bot, update);
                    offset = update.updateId() + 1;
                }
                Thread.sleep(2000); // Consider making this configurable
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("Interrupted, shutting down.");
            } catch (Exception e) {
                logger.error("Error processing updates: {}", e.getMessage());
                // Implement backoff strategy here
            }
        }
    }
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .build();
    // Telegram bot process and commands
    private static void processUpdate(TelegramBot bot, Update update) {
        // Check if update is null
        if (update == null || update.message() == null) {
            return;
        }
        String messageText = update.message().text();
        // Check if update is a message and is not null
        if (update.message() != null) {
            // If messageText is null, skip this iteration
            if (messageText == null) {
                return; // skips executing the rest of this iteration (i.e., goes to next update)
            }
            // If @ is present, this will separate the command from the bot's username.
            String[] split = messageText.split("@");
            String command = (split.length > 0) ? split[0] : "";
            // Process known commands
            switch (command) {
                case "/invite", "invite" -> {
                    String InviteMessage = """
                            [You joined Meme Unit yet?](https://getrichormemetrying.site)
                            Building the Meme Unit,
                            one meme at a time.
                            [My story so far](https://www.youtube.com/watch?v=yL_sv70FUcQ&t=2s)
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), InviteMessage).parseMode(ParseMode.Markdown));
                }
                case "/roadmap", "roadmap" -> {
                    String imageUrl = "https://imgur.com/a/07oSfAy"; // replace with your image URL
                    SendPhoto sendPhotoRequest = new SendPhoto(update.message().chat().id(), imageUrl);
                    bot.execute(sendPhotoRequest);
                    String RoadmapMessage = """
                            *Meme Unit Roadmap*
                            \s
                            *🌐 Phase 1: Building Meme Unit 🌐*
                            \s
                            ✅ or x mint |date|
                            \s
                            *And we won't stop there.*
                            \s
                            *🌐 Phase 2: 🌐*
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), RoadmapMessage).parseMode(ParseMode.Markdown));
                }
                case "/work", "welcome" -> {
                    String WorkMessage = """
                            *Ready to dive into the crypto scene and be part of something groundbreaking?*
                            \s
                            *List of commands of @MemeUnitBot, please do /worklist*
                            \s
                            Meme Unit website: https://getrichormemetrying.site
                            \s
                            *Social Networks*
                            \s
                            Meme Unit Ecosystem | [Join us on Telegram](https://t.me/getrichormemetrying)
                            Meme Unit Ecosystem | [Website](https://getrichormemetrying.site)
                            Meme Unit Ecosystem | [Follow on X](https://x.com/memeunit_ent)
                            \s
                            *Market and DEFi*
                            \s
                            Buy on Raydium: [Start swapping]
                            CHART: [GeckoTerminal]
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), WorkMessage).parseMode(ParseMode.Markdown));
                }
                case "/price", "price" -> {
                    String tokenPrice = fetchTokenPrice();
                    bot.execute(new SendMessage(update.message().chat().id(), tokenPrice));
                }
                case "/scam", "scam" -> {
                    String ScamMessage = """
                            *PLEASE, KEEP THE FOLLOWING IN MIND WHILE IN OUR ECOSYSTEM!*
                            \s
                            As our community continues to grow, it is important to stay vigilant against potential scams.
                            \s
                            *MEME UNIT RULES OF THUMBS*
                            \s
                            *1.* Do Not Trust Direct Messages (DMs): Our team will never reach out to you via direct message for personal information, investment opportunities, or any other sensitive matters.
                            \s
                            *2.* Follow Pinned Messages: Always refer to the pinned messages in our official channels for the most accurate and up-to-date information.
                            \s
                            *3.* Report Suspicious Activity: If you encounter any suspicious activity or receive unsolicited messages, please report them to our moderators immediately.
                            \s
                            *Your safety and security are our top priorities. Stay informed and protect yourself against scams.*
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), ScamMessage).parseMode(ParseMode.Markdown));
                }
                case "/ca", "ca" -> {
                    String ca = "4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), ca));
                }
                case "/twitter" -> {
                    String ca = "https://x.com/memeunit_ent";
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
                case "/combot" -> {
                    String url = "https://combot.org/commands";
                    bot.execute(new SendMessage(update.message().chat().id(), "Here's the full ComBot command list: " + url));
                }
                case "buy" -> {
                    String url = "https://raydium.io/swap/?inputMint=sol&outputMint=4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), "You can buy $MINDN: " + url));
                }
                case "/jupiter" -> {
                    String url = "https://jup.ag/swap/SOL-4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
                    bot.execute(new SendMessage(update.message().chat().id(), "You can buy $MINDN on Jupiter: " + url));
                }
                case "/worklist" -> {
                    String WorkListMessage = """
                            *\uD83C\uDF1F Here's the full list of commands I can do! \uD83C\uDF1F*
                            \s
                            *\uD83C\uDF10 Meme Unit default commands*
                            /work, /worklist, /roadmap, /invite, /buy, /raydium, /jupiter, /twitter
                            \s
                            *\uD83C\uDF10 Meme Unit Ecosystem commands*
                            /scam, /chart, /ca, /price, /inject
                            \s
                            *\uD83C\uDF10 Meme Unit partners*
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), WorkListMessage).parseMode(ParseMode.Markdown));
                }
            }
        }
    }

    private static String fetchTokenPrice() {
        String API_KEY = System.getenv("API_KEY");
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "API key for BirdEye is not set or is invalid.";
        }
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("public-api.birdeye.so")
                .addPathSegment("defi")
                .addPathSegment("price")
                .addQueryParameter("address", Main.tokenAddress)
                .addQueryParameter("chainId", "101")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-API-KEY", API_KEY)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "Error fetching token price: " + response;
            }
            ResponseBody responseBody = response.body();
            String responseString = responseBody.string();
            JSONObject jsonObject = new JSONObject(responseString);
            JSONObject dataObject = jsonObject.optJSONObject("data");
            if (dataObject == null) {
                return "Invalid data in response";
            }
            double price = dataObject.optDouble("value", -1.0);
            if (price < 0) {
                return "Invalid price in data response";
            }
            DecimalFormat df = new DecimalFormat("0.0000000000");
            return "Current price for 1 $MINDN is " + "$" + df.format(price);
        } catch (IOException e) {
            return "Exception when making request - " + e.getMessage();
        }
    }
}