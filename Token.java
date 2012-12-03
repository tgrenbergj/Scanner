public class Token {
	public enum TokenType { VALID, INVALID, DONE, WHITESPACE };
	
	private TokenType type;
	private String name;
	private String token;
	
	public Token(TokenType type, String name, String token) {
		this.type = type;
		this.name = name;
		this.token = token;
	}
	
	public Token(TokenType type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public boolean isDone() {
		return type.equals(TokenType.DONE);
	}
	
	public boolean isValid() {
		return type.equals(TokenType.VALID);
	}
	
	public boolean isWhitespace() {
		return type.equals(TokenType.WHITESPACE);
	}
	
	public String toString() {
		return String.format("Type: %s Name: %s Token: %s", type, name, token);
	}
	
}
