/**
 *
 * @author MarcTheMarcian
 */
public enum Type {
  DATATYPE(0), ASTERISK(1), IDENTIFIER(2), OPENINGPARENTHESIS(3), COMMA(4), CLOSINGPARENTHESIS(5), 
  OPENINGBRACKET(6), CLOSINGBRACKET(7), SEMICOLON(8), UNDEFINED(9), FOR(10), ASSIGNMENT(11), OPERATOR(12),
  COMPARATOR(13), GATE(14), INCREMENTDECREMENT(15), DIGIT(16), OPENINGBRACE(17), CLOSINGBRACE(18);
  private final int value;
  
  private Type(int value) {
    this.value = value;
  }
  
  public int getValue() {
    return value;
  }
};
