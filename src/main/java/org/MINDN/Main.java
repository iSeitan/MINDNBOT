package org.MINDN;

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
    private static final String botToken = System.getenv("TELEGRAM_BOT_TOKEN").trim();
    private static final String tokenAddress = "4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545";
    private static int offset = 0;
    // create telegram api bot with bot token
    public static void main(String[] args) {
        if (botToken.isEmpty()) {
            logger.error("TELEGRAM_BOT_TOKEN is not set or is invalid");
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
                            Here's the first [INVITATIONAL EVENT SEASON 1](https://t.me/subinvitebot/win?startapp=66918cb73c3724d15e5b9f4b)
                            INVITE EVERYONE IN THE WORLD, SHOW THE WORLD WE'RE MINDBLOWN WITH $MINDN!
                            [Are you MindBlown yet?](https://www.mindblown.world)
                            BUILDING THE CORE
                            AND THE BUILDING THE WORLD
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), InviteMessage).parseMode(ParseMode.Markdown));
                }
                case "/roadmap", "roadmap" -> {
                    String imageUrl = "https://imgur.com/a/07oSfAy"; // replace with your image URL
                    SendPhoto sendPhotoRequest = new SendPhoto(update.message().chat().id(), imageUrl);
                    bot.execute(sendPhotoRequest);
                    String RoadmapMessage = """
                            *MindBlown Ecosystem Roadmap*
                            \s
                            *🌐 Phase 1: Building the MINDN Core 🌐*
                            \s
                            ✅ First and last mint of 1B MindBlown |06-06-2024|
                            ✅ Launch of the MindBlown Ecosystem |06-08-2024|
                            ✅ Launch of the MindBlown Telegram Ecosystem |06-10-2024|
                            ✅ Launch of the MindBlown Reddit Ecosystem |06-10-2024|
                            ✅ Injection of the first Liquidity Pool on Raydium.io |06-28-2024|
                            ✅ Liquidity Pool LP Token burnt |06-30-2024|
                            ✅ Launch of Shill competition with rewards [Every 2 weeks] |07-01-2024|
                            ✅ Launch of the MindBlown Discord Ecosystem |06-08-2024|
                            ✅ Open source release of @MindBlownBot, our telegram bot |06-08-2024|
                            ✅ Launch of the MindBlown Ecosystem Website |07-17-2024|
                            ✅ Created a Pump.Fun token $GETMINDN, it's a $MINDN ad! |07-19-2024|
                            \s
                            *And we won't stop there to better the MINDN core.*
                            \s
                            *🌐 Phase 2: Changing the Cryptoverse 🌐*
                            \s
                            THIS MONTH: MINDN Team will add a new Liquidity Pool on Raydium.io. We will also get on DexScreener then.
                            \s
                            TBA: Announcing the first MindBlown Partnership. Many more will follow.
                            TBA: First MindBlown NFTs collection. NFTs that are worth something, but cost you less than a coffee.
                            \s
                            Outside of those... The world is not MindBlown enough yet.
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), RoadmapMessage).parseMode(ParseMode.Markdown));
                }
                case "/work", "welcome" -> {
                    String WorkMessage = """
                            *Get Ready to be MindBlown with $MINDN!*
                            \s
                            *List of commands of @MindBlownBot, please do /worklist*
                            \s
                            MindBlown Ecosystem website: https://www.mindblown.world
                            *$MINDN is the upgraded and decentralized version of channel points. You earn by engaging in the community.*
                            \s
                            *Social Networks*
                            \s
                            MindBlown Ecosystem | [Join us on Telegram](https://t.me/MindBlownProject)
                            MindBlown Ecosystem | [Join the chat](https://t.me/MindBlowngraphicsmemes/1)
                            MindBlown Ecosystem | [Promote and earn](https://t.me/MindBlownCommunity)
                            MindBlown Ecosystem | [r/ProjectMindBlown](https://www.reddit.com/r/ProjectMindBlown/)
                            MindBlown Ecosystem | [Join our Discord](https://discord.gg/93XJtRQWkW)
                            MindBlown Ecosystem | [Follow on X](https://x.com/mindblownsol)
                            \s
                            *Market and DEFi*
                            \s
                            Burnt LP Token: [View on Solscan](https://solscan.io/tx/3usMDeJyfFeKBrr6piNKwqMLUCBPqL14TQzeXPTEp5J3fqEWtT9gvxvhK6daES9pCFHHLgwncW4MHbyEabEPLkGZ)
                            Swap $MINDN on Raydium: [Start swapping](https://raydium.io/swap/?inputMint=sol&outputMint=4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545)
                            CHART [GeckoTerminal](https://www.geckoterminal.com/solana/pools/GXvnPwpJs22Q6YvUr6eA9EJV7Dt23RUH6m7jw9DW8o48)
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
                            *MINDBLOWN ECOSYSTEM RULES OF THUMBS*
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
                case "/21bits" -> {
                    String url = "https://www.21bits.io/code=Seitan1";
                    bot.execute(new SendMessage(update.message().chat().id(), "Enter my 21Bits referrals and you will earn from my winnings! 21Bits will give you a 100% Deposit Bonus USE CODE SEITAN1 " + url));
                }
                case "/gamingbets" -> {
                    String url = "https://gamingbets.com";
                    bot.execute(new SendMessage(update.message().chat().id(), "Code: SEITANONKICK | 100% Deposit Match Bonus + 100 FREE Spins on BetSoft | Min. $15 Deposit " + url));
                }
                case "/winspirit" -> {
                    String url = "https://pokiesgamer.com/seitan?utm_campaign=1";
                    bot.execute(new SendMessage(update.message().chat().id(), "Use my link, then deposit a minimum of $20 and play it 3x wager, send me proof in dm and receive $10 in your Solana wallet! " + url));
                }
                case "/mindblowenergy" -> {
                    String url = "https://www.mindblowenergy.com";
                    bot.execute(new SendMessage(update.message().chat().id(), "Amazon.ca #1 Nootropics supplement and energy drink! " + url));
                }
                case "/fndamentals" -> {
                    String url = "https://fndamentals.ca";
                    bot.execute(new SendMessage(update.message().chat().id(), "Canadian Premium Apparel. Get your FNDAMENTALS! " + url));
                }
                case "/airramedia" -> {
                    String url = "https://airramedia.ca";
                    bot.execute(new SendMessage(update.message().chat().id(), "Canadian production company " + url));
                }
                case "/inject" -> {
                    String InjectionMessage = """
                            *Inject your own Liquidity Pool in the MindBlown Ecosystem!*
                            \s
                            [Raydium docs for liquidity providers](https://docs.raydium.io/raydium/pool-creation/creating-a-constant-product-pool)
                            *This is recommended for MindBlown that are used to Staking, know how liquidity pools work and are ready to burn their LP tokens.*
                            \s
                            *1.*
                            Create a new pool on [Raydium](https://raydium.io/pools)
                            Add your Solana coins in your liquidity pool. ($SOL / $MINDN -> SWAP)
                            *Raydium will provide you with one LP Token. This LP Token is like an NFT, it's unique to you and your pool.*
                            \s
                            *2.*
                            You have two choices: Keep it or Burn it.
                            Burning it means you can't get your liquidity back and it's good for the ecosystem.
                            Not burning it means you can get your liquidity back at any time. It's up to you but this can means less trust in the ecosystem.
                            \s
                            You can burn it on tools like [Sol-Incinerator](https://sol-incinerator.com)
                            \s
                            *3.*
                            You could improve the ecosystem by creating an ecosystem farm.
                            It's like providing $SOL and $MINDN rewards to the community for using your pool.
                            [Here's a detailed and official guide on how all of this works](https://docs.raydium.io/raydium/pool-creation/creating-a-constant-product-pool/creating-an-ecosystem-farm)
                            \s
                            *Here's some further proof that MINDN is fully decentralized:*
                            \s
                            [MetaData, Rights revoked, LP Token Burnt.](https://explorer.solana.com/tx/56uAnmRdD7JqxH73ZcbhZp1S76dJ7zXDHEVm5ZZgFd1HX6p4ggVxZryBySmjgyJoFMgmqWeKRUBTmfnSWe7jQEoJ/inspect)
                            [Scan $MINDN on the Solana BlockChain](https://solscan.io/token/4bEMorkYYDojk98Pk2hRTScvh6HwKgvrikzEcP2dY545#metadata)
                            \s
                            I can provide any proof you need, just ask me @SeitanSurKick
                            \s
                           \s""";
                    bot.execute(new SendMessage(update.message().chat().id(), InjectionMessage).parseMode(ParseMode.Markdown));
                }
                case "/ca", "ca" -> {
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
                            *\uD83C\uDF10 MindBlown default commands*
                            /work, /worklist, /roadmap, /invite, /buy, /raydium, /jupiter, /twitter
                            \s
                            *\uD83C\uDF10 MindBlown Ecosystem commands*
                            /scam, /chart, /ca, /price, /inject
                            \s
                            *\uD83C\uDF10 MindBlown partners*
                            /mindblowenergy, /fndamentals, /airramedia, /gamingbets, /21bits, /winspirit
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