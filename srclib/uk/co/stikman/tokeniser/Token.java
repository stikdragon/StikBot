package uk.co.stikman.tokeniser;

public class Token {
	private TokenType	type;
	private String		val;

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public Token(TokenType type, String val) {
		super();
		this.type = type;
		this.val = val;
	}
	
	
	/**
	 * Returns the value of the token
	 */
	@Override
	public String toString() {
		return val;
	}

	/**
	 * Checks that the token is of TokenType @type
	 * @param type the type to expect
	 * @throws TokenException if it is not @type
	 */
	public void expectType(TokenType type) throws TokenException {
		if (this.type.equals(type))
			return;
		throw new TokenException("Expected token type " + this.type.toString() + " but found " + type.toString());
		
	}

	/**
	 * Checks that the token is one of the strings passed to the function.  It is case insensitive
	 * @param oneOf Variable argument list
	 * @throws TokenException if the token does not equal one of the values passed 
	 */
	public void expectValue(String... oneOf) throws TokenException {
		for (String s: oneOf)
			if (s.equalsIgnoreCase(val))
				return;
		StringBuilder b = new StringBuilder();
		for (String s: oneOf) 
			b.append(s + " ");			
		throw new TokenException("Expected token value as one of: " + b.toString() + ". Found " + val);
		
	}

	
	

	/**
	 * Returns the value of the token and first checks it is of the expected type. 
	 * Raises exception if not.  This is the same as calling 
	 * <code>
	 * t.expectType(expectedType);
	 * String s = t.getVal();
	 * </code>
	 * @param expectedType the TokenType to expect
	 * @return the value of the token
	 * @throws TokenException
	 */
	public String getVal(TokenType expectedType) throws TokenException {
		expectType(expectedType);
		return val;
	}

}
