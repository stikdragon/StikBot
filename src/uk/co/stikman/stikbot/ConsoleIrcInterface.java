package uk.co.stikman.stikbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class ConsoleIrcInterface implements IrcInterface {

	public class InputReader extends Thread {
		private BufferedReader	br;
		private boolean			enabled	= true;

		public InputReader() {
			br = new BufferedReader(new InputStreamReader(System.in));
			start();
		}

		@Override
		public void run() {
			while (enabled) {
				try {
					String s = null;
					if (System.in.available() > 0)
						s = br.readLine();
					else
						Thread.sleep(250);

					if (s != null && s.length() > 0) {
						char usrnum = s.charAt(0);
						String name = null;
						switch (usrnum) {
							case '0':
								name = "userA";
								break;
							case '1':
								name = "userB";
								break;
							case '2':
								name = "userC";
								break;
							default:
								throw new RuntimeException("Precede with 0, 1 or 2");
						}
						s = "sleipnir: " + s.substring(1).trim();
						handler.onMessage("sleipnir", name, "x", "localhost", s);
					}
				} catch (InterruptedException e) {

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		public void end() {
			enabled = false;
			interrupt();
		}

	}

	private String				nick;
	private IrcEventsHandler	handler;
	private InputReader			reader;

	@Override
	public void setLogin(String string) {
	}

	@Override
	public void joinChannel(String string) {
	}

	@Override
	public void partChannel(String channel) {
	}
	
	@Override
	public String getNick() {
		return nick;
	}

	@Override
	public void setName(String nick) {
		this.nick = nick;
	}

	@Override
	public void connect(String server) throws NickAlreadyInUseException, IOException, IrcException {
		reader = new InputReader();
		handler.onJoin("channel", "userA", "x1", "localhost");
		handler.onJoin("channel", "userB", "x2", "localhost");
		handler.onJoin("channel", "userC", "x3", "localhost");
	}

	@Override
	public void setIrcEventsHandler(IrcEventsHandler handler) {
		this.handler = handler;
	}

	@Override
	public void sendMessage(String target, String message) {
		message = Colors.removeFormattingAndColors(message);
		System.out.println("bot: " + message);
	}

	@Override
	public void terminate() {
		reader.end();
	}

	@Override
	public void quitServer(String reason) {
		handler.onDisconnect();
	}



}
