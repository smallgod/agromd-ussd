/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.util;

/**
 *
 * @author smallgod
 */
import java.util.Arrays;
import java.util.List;

/*
 * This class generates numeric IDs incrementally starting from a 
 * given ID
 * 
 * @author imal hasaranga 
 * @version 1.0
 * @since 2014-07-13
 * 
 * */
public class NumericIDGenerator {

    // This is my number set it contains 62 symbols
    private final List<Character> numberset = Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );
    private String initAL; // keep starting AN
    private int BASE; // base of numberset is 62 so, addition = (anynumber %
    // BASE) and spare = (anynumber / 62)

    public NumericIDGenerator(String initAL) {
        // initializing the values
        this.initAL = initAL;
        this.BASE = numberset.size();
    }

    private synchronized String nextAN() {

        char[] number = initAL.toCharArray();
        int spare = 0;
        int addition = 1;

        /*
		 * Here what i'm doing is i'm looping the given string backwards and get
		 * char by char
         */
        for (int i = number.length - 1; i >= 0; i--) {
            
           

            int lastnumber = numberset.indexOf(number[i]);
            /*
			 * above i'm getting number associated with the last char, example
			 * letter "A" means 10 and in the below line i'm adding +1, also if
			 * there is number left in the previous calculation I'm adding it
			 * too
             */

            int newnumb = lastnumber + addition + spare;

            /*
			 * now i'm checking whether the new number is exceeding the base,
			 * example, in normal number set is 9+1 = 10
             */
            if (newnumb >= BASE) {
                // calculate spare and the addition
                number[i] = numberset.get(newnumb % BASE);
                spare = newnumb / BASE;
            } else {
                /*
				if the addition is not exceeding the base then we can just
				add them
				and stop there
                 */
                number[i] = numberset.get(newnumb);
                break;
            }
            /*
			checkin a special situation, spare can be 1 but sometimes number
			might have end in the next iteration, in this case i'm adding the
			spare to the front
			and stoping
             */
            if ((spare > 0) && ((i - 1) < 0)) {
                number = (numberset.get(spare) + new String(number)).toCharArray();
                break;
            }
        }

        return initAL = new String(number);
    }

    public static String generateNextId(String idNumberToIncrement) {

        NumericIDGenerator generator = new NumericIDGenerator(idNumberToIncrement);
        String generatedNum = generator.nextAN();

        /*int counter = 0;
        while (true) {
            ++counter;
            if ((String.valueOf(idNumberToIncrement)).equalsIgnoreCase("YYYYZ")) {
                break;
            }

            generatedNum = generator.nextAN();
            logger.debug("ID: " + generatedNum + " Counter :" + counter);
        }*/
        
        return generatedNum;

    }
}
