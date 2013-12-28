package uk.co.stikman.dominion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.stikbot.User;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main m = new Main();
		try {
			m.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void run() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		DominionGame game = new DominionGame("", null);
		game.setOutput(new DominionOutput() {
			private StringBuilder	sb	= new StringBuilder();

			@Override
			public DominionOutput out(String s) {
				sb.append(s);
				return this;
			}

			@Override
			public DominionOutput bold(String s) {
				sb.append(s);
				return this;
			}

			@Override
			public DominionOutput soft(String s) {
				sb.append(s);
				return this;
			}

			@Override
			public DominionOutput card(Card card, boolean showCost) {
//				char ch = card.getKeyletter();
				String s = card.getName();
				int colour;
				switch (card.getCardType()) {
				case ACTION:
					colour = 3;
					break;
				case TREASURE:
					colour = 4;
					break;
				case VICTORY:
					colour = 5;
					break;
				default:
					break;
				}
//				int p = s.indexOf(Character.toUpperCase(ch));
//				if (p == -1)
//					p = s.indexOf(Character.toLowerCase(ch));
//				if (p == -1)
//					throw new RuntimeException("Character is not part of the phrase");
//				out(s.substring(0, p));
//				bold("[" + ch + "]");
//				out(s.substring(p + 1));
				out(s);
				if (showCost)
					soft("(" + card.getCost() + ")");
				return this;
			}

			@Override
			public void send() {
				System.out.println(sb.toString());
				sb = new StringBuilder();
			}

			@Override
			public void cardtype(CardType cardType) {
			}

		});

		List<String> inputQ = new ArrayList<>();
		inputQ.add("0join");
		inputQ.add("1join");
		inputQ.add("2join");

		for (;;) {
			try {
				String s;
				if (inputQ.isEmpty())
					s = br.readLine().trim();
				else
					s = inputQ.remove(0);

				if (s.length() == 0)
					continue;
				String name = null;
				switch (s.charAt(0)) {
				case '0':
					name = "Stik";
					break;
				case '1':
					name = "Amy";
					break;
				case '2':
					name = "Derpy";
					break;
				}
				if (name == null)
					throw new RuntimeException("No name specified (0 or 1)");
				s = s.substring(1).trim();
				game.onMessage(new User(name), s, null);

			} catch (DominionError de) {
				System.err.println(de.getMessage());
			} catch (Throwable th) {
				th.printStackTrace();
			}

		}
	}

}
