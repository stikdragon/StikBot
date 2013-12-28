package uk.co.stikman.stikbot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;

public class Main {

	

	private static BotOptions parseProgramArgs(String[] args) {
		BotOptions opts = new BotOptions();

		int i = 0;
		String arg;

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.equals("-help"))
				opts.setHelp(true);

			if (arg.equals("-console"))
				opts.setConsole(true);

			if (arg.equals("-channel")) {
				if (i >= args.length)
					throw new RuntimeException("-channel requires an argument");
				opts.setChannel(args[i++]);
			}

			if (arg.equals("-server")) {
				if (i >= args.length)
					throw new RuntimeException("-server requires an argument");
				opts.setServer(args[i++]);
			}

			if (arg.equals("-nick")) {
				if (i >= args.length)
					throw new RuntimeException("-nick requires a username");
				opts.addUserName(args[i++]);
			}
			
			if (arg.equals("-channelcount")) {
				if (i >= args.length)
					throw new RuntimeException("-channelcount requires a number");
				opts.setChannelCount(Integer.parseInt(args[i++]));
			}
		}

		return opts;
	}

	public static void main(String[] args) {

		BotOptions opts = parseProgramArgs(args);
		if (opts.isShowHelp()) {
			System.out.println("StikBot " + StikBot.VERSION);
			System.out.println("Arguments:");
			System.out.println(" -console           Run as an interactive console, instead of connecting to IRC");
			System.out.println(" -server <server>   Specify the server to connect to (REQUIRED)");
			System.out.println(" -channel <channel> Specify the channel to join (REQUIRED)");
			System.out.println(" -nick <nick>       Add a nick to use.  Can specify many times");
			System.out.println(" -channelcount <n>  Set number of game channels to join");
			System.out.println(" -help              Show this message");
			return;
		}

		try {
			IrcInterface intf;
			if (opts.isConsole())
				intf = new ConsoleIrcInterface();
			else
				intf = new PircIrcInterface();

			if (opts.getServer() == null)
				throw new RuntimeException("No server set");
			if (opts.getChannel() == null)
				throw new RuntimeException("No channel set");

			StikBot bot = new StikBot(intf);
			// bot.begin("irc.canternet.org");
			// bot.begin("stikman.plus.com");
			// bot.begin("irc.quakenet.org");
			bot.setBotNames(opts.getNicks());
			bot.begin(opts);
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}

}
