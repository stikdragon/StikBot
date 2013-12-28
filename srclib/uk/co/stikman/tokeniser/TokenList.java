package uk.co.stikman.tokeniser;

import java.util.ArrayList;
import java.util.List;

public class TokenList {

	private int			position;
	private List<Token>	tokens  = new ArrayList<Token>();
	private boolean		caseSensitive;

	public TokenList() {
		position = 0;
	}

	/**
	 * DEtermines if there are more tokens.  If this returns <code>true</code> then you can safely call <code>next()</code>
	 * @return true or false
	 */
	public boolean hasNext() {
		return (tokens.size() > position);
	}

	
	/**
	 * Returns the next token in the list. 
	 * @return the next token
	 * @throws NoMoreTokensError if there are no more tokens
	 */
	public Token next() throws NoMoreTokensError {
		if (tokens.size() <= position)
			throw new NoMoreTokensError("No more tokens");
		return tokens.get(position++);
	}

	/**
	 * Discards the next token
	 * @throws NoMoreTokensError if its run out of tokens
	 */
	public void consume() throws NoMoreTokensError {
		next();
	}

	
	/**
	 * Discard the next token after checking that its value == @string and its type == @expectedType 
	 * @param string the value to look for
	 * @param expectedType the type to look for
	 * @throws TokenException if either condition is not met
	 * @throws NoMoreTokensError if its run out of tokens
	 */
	public void consume(String string, TokenType expectedType) throws TokenException {
		if (string == null)
			throw new IllegalArgumentException("String cannot be null");
		if (tokens.size() <= position)
			throw new NoMoreTokensError("No more tokens.  Expected: " + string);
		Token t = next();
		String s = t.getVal();
		if (!t.getType().equals(expectedType))
			throw new TokenException("Expected token of type " + expectedType.toString() + " but found " + s + " (" + t.getType().toString() + ")");
		if (caseSensitive) {
			if (!string.equals(s))
				throw new NoMoreTokensError("Expected token " + string + " but found " + s);
		} else {
			if (!string.equalsIgnoreCase(s))
				throw new NoMoreTokensError("Expected token " + string + " but found " + s);
		}
	}

	public void setCaseSensitive(boolean b) {
		this.caseSensitive = b;
	}

	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}
	
	
	public String nextString() throws NoMoreTokensError {
		return next().getVal();
	}

	/**
	 * Returns the value of the next token after verifying that it is of @expectedType
	 * 
	 * This is the same as calling
	 * <pre>
	 * Token t = toks.next();
	 * t.expectType(expectedType);
	 * String s = t.getVal();
	 * </pre>
	 * @param expectedType the type to check for
	 * @return the string value of the token
	 * @throws NoMoreTokensError if its run out of tokens
	 * @throws TokenException if the type does not match 
	 */
	public String next(TokenType expectedType) throws TokenException {
		Token t = next();
		if (!t.getType().equals(expectedType))
			throw new TokenException("Expected token of type " + expectedType.toString() + " but found " + t.getType().toString() + " (" + t.getVal() + ")");
		return t.getVal();
	}
	
	/**
	 * Returns the value of the next token after verifying that it is of type @expectedType and that its value is one of @expectedValues
	 * 
	 * This is the same as calling
	 * <pre>
	 * Token t = toks.next();
	 * t.expectType(expectedType);
	 * t.expectValue(expectedValues);
	 * String s = t.getVal();
	 * </pre>
	 * @param expectedType the type to check for
	 * @param expectedValues a list of String values to check for inclusion in.  Case insensitive.
	 * @return the string value of the token
	 * @throws NoMoreTokensError if its run out of tokens
	 * @throws TokenException if the type of value conditions are not met
	 */
	public String next(TokenType expectedType, String ... expectedValues) throws TokenException {
		Token t = next();
		if (!t.getType().equals(expectedType))
			throw new TokenException("Expected token of type " + expectedType.toString() + " but found " + t.getType().toString() + " (" + t.getVal() + ")");
		t.expectValue(expectedValues);		
		return t.getVal();
	}
	
	/**
	 * Returns the value of the next token after verifying that its value is one of @expectedValues
	 * 
	 * This is the same as calling
	 * <pre>
	 * Token t = toks.next();
	 * t.expectValue(expectedValues);
	 * String s = t.getVal();
	 * </pre>
	 * @param expectedValues a list of String values to check for inclusion in.  Case insensitive.
	 * @return the string value of the token
	 * @throws NoMoreTokensError if its run out of tokens
	 * @throws TokenException if the type of value conditions are not met
	 */
	public String next(String ... expectedValues) throws TokenException {
		Token t = next();
		t.expectValue(expectedValues);		
		return t.getVal();
	}

	/**
	 * Adds a new Token of type <code>type</code> and value <code>s</code> 
	 * @param type type of token
	 * @param s value of token
	 */
	public void add(TokenType type, String s) {
		tokens.add(new Token(type, s));		
	}

	/**
	 * REturns the number of tokens in the list
	 * @return number of tokens
	 */
	public int size() {
		return tokens.size();
	}
	
	/**
	 * Returns the token at <code>i</code>.  
	 * @param i
	 * @return
	 */
	public Token peek(int i) {
		return tokens.get(i);
	}

}
