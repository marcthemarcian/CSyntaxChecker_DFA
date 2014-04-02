/**
 *
 * @author MarcTheMarcian
 */
public class Token {
  private final String code;
  private final Type type;
  
  public Token(String code, Type type){
    this.code = code;
    this.type = type;
  }
  
  public String getCode() {
    return code;
  }
  
  public Type getType() {
    return type;
  }
}
