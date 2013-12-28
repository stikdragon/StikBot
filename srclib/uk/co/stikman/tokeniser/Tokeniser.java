package uk.co.stikman.tokeniser;

public class Tokeniser {

	
	
	//
	// Adapted from
	// http://stackoverflow.com/questions/10695143/split-a-quoted-string-with-a-delimiter
	//
	public static TokenList tokenise(String text) {
		TokenList res = new TokenList();
		if (text == null)
			return res;

		int wordQuoteStartIndex = 0;
		int wordQuoteEndIndex = 0;

		int wordSpaceStartIndex = 0;
		int wordSpaceEndIndex = 0;

		boolean foundQuote = false;
		for (int index = 0; index < text.length(); index++) {
			if (text.charAt(index) == '\"') {
				if (foundQuote == true) {
					wordQuoteEndIndex = index + 1;
					// Print the quoted word
					res.add(TokenType.STRING, text.substring(wordQuoteStartIndex + 1, wordQuoteEndIndex - 1));
					// here you can remove quotes by changing to
					// (wordQuoteStartIndex+1, wordQuoteEndIndex-1)
					foundQuote = false;
					if (index + 1 < text.length()) {
						wordSpaceStartIndex = index + 1;
					}
				} else {
					wordSpaceEndIndex = index;
					if (wordSpaceStartIndex != wordSpaceEndIndex) {
						// print the word in spaces
						addToken(res, text.substring(wordSpaceStartIndex, wordSpaceEndIndex));
					}
					wordQuoteStartIndex = index;
					foundQuote = true;
				}
			}

			if (foundQuote == false) {
				if (text.charAt(index) == ' ') {
					wordSpaceEndIndex = index;
					if (wordSpaceStartIndex != wordSpaceEndIndex) {
						// print the word in spaces
						addToken(res, text.substring(wordSpaceStartIndex, wordSpaceEndIndex));
					}
					wordSpaceStartIndex = index + 1;
				}

				if (index == text.length() - 1) {
					if (text.charAt(index) != '\"') {
						// print the word in spaces
						addToken(res, text.substring(wordSpaceStartIndex, text.length()));
					}
				}
			}
		}

		return res;
	}

	private static void addToken(TokenList toks, String s) {
		if (s.matches("[-+]?[0-9]*(\\.?[0-9]+)"))
			toks.add(TokenType.NUMBER, s);
		else
			toks.add(TokenType.NORMAL, s);
	}

}
