/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MP4;

/**
 *
 * @author MarcTheMarcian
 */
public class Test {
  public static void main(String[] args) {
       int monthNumber = 0;
       String month = "January";

        switch (month.toLowerCase()) {
            case "january":
                monthNumber = 1;
                break;
            default: 
                monthNumber = 0;
                break;
        }
        
        System.out.println(monthNumber);
  }
}
