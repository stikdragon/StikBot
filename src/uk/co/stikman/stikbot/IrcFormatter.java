package uk.co.stikman.stikbot;


public interface IrcFormatter {
	IrcFormatter box(String s); // [val], with [ and ] in c4
	IrcFormatter bold(String s); // uses bold font (c2)
	IrcFormatter quiet(String s); // uses non-bold font (c3) 
	IrcFormatter colour(int colour, String s); // uses one of the five colours (c1, c2, c3, c4, c5)
	IrcFormatter c1(String s); // normal text
	IrcFormatter c2(String s); // bold
	IrcFormatter c3(String s); // dim colour
	IrcFormatter c4(String s); // decoration colour
	IrcFormatter c5(String s); // decoration colour 2
	IrcFormatter out(String s); // normal text in c1
	IrcFormatter at(int position); // moves to position (must be to the right or nothing will happen)
	IrcFormatter boxlist(Iterable<?> items); // list like [item1], [item2], [item3]
	void send(); // call this when finished or nothing will happen
	IrcFormatter error(String s);
	IrcFormatter underline(boolean enable);
}
