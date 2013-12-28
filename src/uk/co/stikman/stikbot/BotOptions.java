package uk.co.stikman.stikbot;

import java.util.ArrayList;
import java.util.List;

public class BotOptions {

	private String			server;
	private List<String>	nicks			= new ArrayList<>();
	private boolean			console;
	private String			channel;
	private boolean			showHelp;
	private int				channelCount	= 6;

	public void setServer(String string) {
		this.server = string;
	}

	public void addUserName(String string) {
		this.nicks.add(string);
	}

	public String getServer() {
		return server;
	}

	public List<String> getNicks() {
		return nicks;
	}

	public void setConsole(boolean b) {
		this.console = b;
	}

	public boolean isConsole() {
		return console;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String string) {
		if (!string.startsWith("#"))
			string = "#" + string;
		this.channel = string;
	}

	public void setHelp(boolean b) {
		this.showHelp = b;
	}

	public boolean isShowHelp() {
		return showHelp;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

}