package uk.co.stikman.stikbot;

import java.util.Iterator;

import org.jibble.pircbot.Colors;

import uk.co.stikman.stikbot.StikBot.FormatterSendResponseHandler;

public class Formatter implements IrcFormatter {

	private static final String[]			COLOURS		= new String[] { Colors.NORMAL, Colors.BLUE, Colors.DARK_GRAY, Colors.PURPLE, Colors.OLIVE, Colors.DARK_GREEN };
	private static final int				MAX_LENGTH	= 460;
	private FormatterSendResponseHandler	sender;
	private StringBuilder					sb			= new StringBuilder();
	private int								lastC		= 1;
	private int								pos			= 0;
	private boolean							underline;

	public Formatter(FormatterSendResponseHandler formatterSendResponseHandler) {
		this.sender = formatterSendResponseHandler;
	}

	@Override
	public IrcFormatter box(String s) {
		colour(4, "[");
		colour(1, s);
		colour(4, "]");
		return this;
	}

	@Override
	public IrcFormatter bold(String s) {
		return colour(2, s);
	}

	@Override
	public IrcFormatter quiet(String s) {
		return colour(3, s);
	}

	@Override
	public IrcFormatter colour(int colour, String s) {
		if (colour < 1 || colour > COLOURS.length)
			throw new IllegalArgumentException("Invalid colour");
		if (s == null || s.length() == 0)
			return this;
		
		if (sb.length() + s.length() > MAX_LENGTH) 
			send();
		
		if (lastC != colour)
			sb.append(COLOURS[colour - 1]);
		lastC = colour;
		
		sb.append(s);
		pos += s.length();
		return this;
	}

	@Override
	public IrcFormatter c1(String s) {
		return colour(1, s);
	}

	@Override
	public IrcFormatter c2(String s) {
		return colour(2, s);
	}

	@Override
	public IrcFormatter c3(String s) {
		return colour(3, s);
	}

	@Override
	public IrcFormatter c4(String s) {
		return colour(4, s);
	}

	@Override
	public IrcFormatter c5(String s) {
		return colour(5, s);
	}

	@Override
	public IrcFormatter out(String s) {
		return colour(1, s);
	}

	@Override
	public void send() {
		sender.send(sb.toString());
		underline = false;
		sb = new StringBuilder();
		lastC = 1;
	}

	@Override
	public IrcFormatter at(int position) {
		while (pos < position) {
			sb.append(" ");
			++pos;
		}
		return this;
	}

	@Override
	public IrcFormatter boxlist(Iterable<?> items) {
		for (Iterator<?> i = items.iterator(); i.hasNext();) {
			box(i.next().toString());
			if (i.hasNext())
				quiet(", ");
		}
		return this;
	}

	@Override
	public IrcFormatter error(String s) {
		colour(6, "[");
		colour(2, "!");
		colour(6, "]");
		out(" ");
		bold(s);
		return this;
	}

	@Override
	public IrcFormatter underline(boolean enable) {
		if (underline ^ enable) {
			sb.append(Colors.UNDERLINE);
			underline = enable;
		}
		return this;
	}

}
