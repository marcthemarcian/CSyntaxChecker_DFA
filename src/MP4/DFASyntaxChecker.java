import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author MarcTheMarcian
 */
public class DFASyntaxChecker {
  
  private final File input;
  private final File output;
  private final BufferedReader br;
  private final BufferedWriter bw;

  private static final ArrayList<String> dataTypes = new ArrayList(
    Arrays.asList("char", "int", "float", "double", "void"));
  
  private static final ArrayList<String> prefix1 = new ArrayList(
    Arrays.asList("unsigned", "signed"));
  
  private static final ArrayList<String> prefix2 = new ArrayList(
    Arrays.asList("long", "short"));
  
  private static final ArrayList<String> reservedKeywords = new ArrayList(
    Arrays.asList("auto", "break", "case", "const", "continue", "default",
                  "do", "else", "enum", "extern", "for", "goto", "if",
                  "register", "return", "signed", "sizeof", "static",
                  "struct", "switch", "typedef", "union", "unsigned",
                  "void", "volatile", "while", "_Packed"));
  
  private static final ArrayList<String> operators = new ArrayList(
    Arrays.asList("+", "-", "*", "/", "%"));
  
  private static final ArrayList<String> comparators = new ArrayList(
    Arrays.asList(">", "<", "="));
    
  private DFA dfa;
  private static int numOfLines;
  private static ArrayList<ArrayList<Token>> inputCodes;
  
  public DFASyntaxChecker(File input, File output) throws FileNotFoundException, IOException {    
    this.input = input;
    br = new BufferedReader(new FileReader(input));
    this.output = output;
    bw = new BufferedWriter(new FileWriter(output));
    
    dfa = new DFA();
    numOfLines = Integer.parseInt(br.readLine());
    inputCodes = initializeInputs();    
  }
    
  public static void main(String[] args) throws IOException {
    DFASyntaxChecker checker = new DFASyntaxChecker(new File("mp4.in"), new File("mp4.out"));
    
    checker.checkCodes();
  }
  
  private ArrayList<ArrayList<Token>> initializeInputs() throws IOException {
    ArrayList<ArrayList<Token>> result = new ArrayList();
    
    for (int i = 0; i < numOfLines; i++) {
      String code = br.readLine();
      
      StringTokenizer tokenizer = new StringTokenizer(code, " *(,)[];+-/%><=&|{}", true);
      result.add(refineInput(tokenizer));
    }
    
    return result;
  }
  
  private ArrayList<Token> refineInput(StringTokenizer t) {
    ArrayList<Token> result = new ArrayList();
    boolean prefixed = false;
    String prefix = "";
    int prefix2counter = 0;
    while(t.hasMoreTokens()){
      String code = t.nextToken();
      
      while(code.equals(" ")) {
        code = t.nextToken();
      }
      
      if (prefix1.contains(code)) {
        if (!prefixed) {
          prefix += code;
          prefixed = true;
        } else {
          result.add(new Token(code, Type.UNDEFINED));
        }
      } else if (prefix2.contains(code)) {
        if (prefix2counter < 2) {
          prefix += code;
          prefix2counter++;
        } else {
          result.add(new Token(code, Type.UNDEFINED));
        }
      } else if (code.equals("+") && result.get(result.size()-1).getCode().equals("+")) {
        result.remove(result.size()-1);
        code += "+";
        result.add(new Token(code, Type.INCREMENTDECREMENT));
      } else if (code.equals("-") && result.get(result.size()-1).getCode().equals("-")) {
        result.remove(result.size()-1);
        code += "-";
        result.add(new Token(code, Type.INCREMENTDECREMENT));
      } else if (code.equals("=") && operators.contains(result.get(result.size()-1).getCode())) {
        String operator = result.get(result.size()-1).getCode();
        code = operator + code;     
        result.remove(result.size()-1);
        result.add(new Token(code, Type.ASSIGNMENT));
      } else if (code.equals("=") && comparators.contains(result.get(result.size()-1).getCode())) {
        String comparator = result.get(result.size()-1).getCode();
        code = comparator + code;     
        result.remove(result.size()-1);
        result.add(new Token(code, Type.COMPARATOR));
      } else if (code.equals("&") && result.get(result.size()-1).getCode().equals("&")) {
        result.remove(result.size()-1);
        code += "&";
        result.add(new Token(code, Type.GATE));
      } else if (code.equals("|") && result.get(result.size()-1).getCode().equals("|")) {
        result.remove(result.size()-1);
        code += "|";
        result.add(new Token(code, Type.GATE));
      } else if (dataTypes.contains(code)) {
        result.add(new Token(prefix + code, Type.DATATYPE));
        prefix = "";
        prefix2counter = 0;
        prefixed = false;
      } else if (code.equals("*")) {
        result.add(new Token(code, Type.ASTERISK));
      } else if (code.equals("(")) {
        result.add(new Token(code, Type.OPENINGPARENTHESIS));
      } else if (code.equals(",")) {
        result.add(new Token(code, Type.COMMA));
      } else if (code.equals(")")) {
        result.add(new Token(code, Type.CLOSINGPARENTHESIS));
      } else if (code.equals("[")) {
        result.add(new Token(code, Type.OPENINGBRACKET));
      } else if (code.equals("]")) {
        result.add(new Token(code, Type.CLOSINGBRACKET));
      } else if (code.equals(";")) {
        result.add(new Token(code, Type.SEMICOLON));
      } else if (code.equals("for")) {
        result.add(new Token(code, Type.FOR));
      } else if (reservedKeywords.contains(code)){
        result.add(new Token(code, Type.UNDEFINED));
      } else if (code.equals("=")) {
        result.add(new Token(code, Type.ASSIGNMENT));
      } else if (operators.contains(code)) {
        result.add(new Token(code, Type.OPERATOR));
      } else if (comparators.contains(code)) {
        result.add(new Token(code, Type.COMPARATOR));        
      } else if (code.equals("&") || code.equals("|")) {
        result.add(new Token(code, Type.UNDEFINED));
      } else if (Character.isDigit(code.charAt(0))) {
        result.add(new Token(code, Type.DIGIT));
      } else if (code.equals("{")) {
        result.add(new Token(code, Type.OPENINGBRACE));
      } else if (code.equals("}")) {
        result.add(new Token(code, Type.CLOSINGBRACE));
      } else {
        boolean checker = true;
        
        if (!Character.isLetter(code.charAt(0)) && code.charAt(0) != '_') {
          checker = false;
        } else {
          for (int i = 1; i < code.length(); i++) {
            if (!(Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
              checker = false;
              break;
            }
          }
        }
        
        if (checker) {
          result.add(new Token(code, Type.IDENTIFIER));
        } else {
          result.add(new Token(code, Type.UNDEFINED));
        }
      }
    }
    
    return result;
  }
  
  public void checkCodes() throws IOException {
    for(ArrayList<Token> line : inputCodes) {
      int currentState;
      int type;
      int nextState = 0;
      int[][] states;
      ArrayList<Integer> finalstates;
      
      if (line.get(0).getType() == Type.FOR) {
        states = dfa.getForLoopDFA();
        finalstates = dfa.getForLoopDFAfinalStates();
      } else {
        states = dfa.getFunctionDeclarationDFA();
        finalstates = dfa.getFuncDecDFAfinalStates();
      }
      
      for (Token word : line) {
        currentState = nextState;
        type = word.getType().getValue();
        nextState = states[currentState][type];
        
        System.out.println(word.getCode() + " " + currentState + " " + word.getType() + " " + nextState);
      }
      
      if (finalstates.contains(nextState)) {
        bw.write("ACCEPTED\r\n");
      } else {
        bw.write("DENIED\r\n");
      }
      
      System.out.println("-------------------\n");
    }
    
    bw.flush();
    bw.close();
  }
}
