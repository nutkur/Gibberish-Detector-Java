/*
 * GibberishNameDetector
 * 
 * Pass in a list of names and a Markov probability table
 * Returns names with positive score, negative score, final score, and 1/0 for pass/fail
 *
 * Depends on pre-built detector Markov chains
 * 
 * Markov probability table includes:
 * - Threshold
 * - array of good probabilities
 * - array of bad probabilities
 * Generated by GibberishDetector.train goodProbability, badProbability Lists
 *
 * Final utils and flow:
 *
 * Create script-based probability markov chains
 *  - add negative lists markov chain builder based off CLDR keyboard maps and language
 *    - always include Latin-101 keyboard
 *    - add html, script, camelcase, .com names
 *  - add Collections writer to utils + threshold
 *  - add script detector module based off ICU UCD script in utils - script of line, throw out common
 *  - add alphabet builder, including accented chars
 *  - add probability value display to line check
 *  - auto-gibberish name is wrong punct in string, like email, html, script @ \w.\w http
 */
package GibberishNameDetector;

import com.paypal.gibberishdetector.GibberishDetector;
import com.paypal.gibberishdetector.GibberishDetectorExtended;
import com.paypal.gibberishdetector.GibberishDetectorFactory;

/**
 *
 * @author mimckenna
 */
public class GibberishNameDetector {
    public static void main(String[] args) {
        
        String[] goodEnglishSentences = {
                "my name is Shir", 
                "hello world", 
                "and you can tell everybody that this is your song", 
                "ALEXANDRAMARIALARA",
                "ALLSTAR",
                "ALMIGHTLYGOD",
                "ALOTTAFAGINA",
                "ALPHONSEBROWN",
                "ALUMINUMMAN",
                "JOHNJOHN",
                "KARLCHEN",
                "KARLDALL",
                "KARLTHEODORFRANZJOSEFMARIAVONUNDZUGUTTENBERG",
                "ZECKE",
                "ZELDA",
                "ZINEDINEZIDANE"
        };
	String[] badEnglishSentences = {
                "2 chhsdfitoixcv", 
                "Wm the 3rd",
                "fasdf asg ggd fhgkv", 
                "qmdu poebc vuutkl jsupwre",
                "asdf asdf", 
                "sam123",
                "mike@paypal.com",
                "http://unicode.org",
                "AAAAEEEESSSS",
                "AABCDEFGH",
                "JP",
                "GIJOE",
                "Mike.McKenna657",
                "eeeeeeeeeeeee",
                "Jane 8 Tau",
                "GIHOHOSD",
                "KAPTNBLAUBR",
                "KARLHEINZBHM",
                "GIJANE",
                "ZLATKO",
                "ÈIHÁÈKOVÁ DØÍMALOVÁ",
                "ŠŹOVÍÈKOVÁ ŠIMÁKOVÁ",
                "hjkhjkhjk",
                "qwerty",
                "asdfghjk",
                "zxcvbnm",
                "$$$$$$$",
                "not in alphabet",
                "☃☃☃☃",
                "Tex", "Texin", "Ute", "May", "Stu", "Rex", "Sex"

        };
	// String alphabet = "abcdefghijklmnopqrstuvwxyz ";
	// Get alphabet from CLDR Exemplar characters for language group
        // String alphabet = "abcdefghijklmnopqrstuvwxyz 0123456789!@#$%^&*().,<>?/:;'-";
        String alphabet = " 0123456789!\"#&'()*,-./:;?@[]abcdefghijklmnopqrstuvwxyz§àáâãäåæçèéêëìíîïñòóôöøùúûüÿāăēĕīĭōŏœūŭ‐–—‘’“”†‡…′″";

	GibberishDetectorFactory factory = new GibberishDetectorFactory(GibberishDetectorExtended.class);
	
        /*
         * Build and train probability matrix
         * Use default values based on Census data if not passed in args
         */
        GibberishDetector gibberishDetector = factory.createGibberishDetectorFromLocalFile2("en-training-data.txt",
										"en-good-names.txt", "badEnglish.txt", "en-alphabet.txt");
        //GibberishDetector gibberishDetector = factory.createGibberishDetectorFromLocalFile2("cs-train.txt",
	//									"cs-good.txt", "badEnglish.txt", "cs-alphabet.txt");
        GibberishDetector negDetector = factory.createGibberishDetectorFromLocalFile2("keybd-en.txt",
										"keybd-en-good.txt", "keybd-en-bad.txt", "keybd-en-alphabet.txt");

        System.out.println("------------");
        System.out.println("Test lines");
        System.out.printf("alphabet: %s%n", alphabet);
	System.out.println("------------");

        for (String line : goodEnglishSentences) {
            double goodProb = (gibberishDetector.getProbability(line) - gibberishDetector.threshold) * gibberishDetector.weight;
            double negProb = (negDetector.getProbability(line) - negDetector.threshold) * negDetector.weight;
            double finalGibberish = goodProb - negProb/2;
            
            System.out.printf("Gibberish: %s %f : %s\n",
                    finalGibberish < 0 ? "TRUE  :" : "false : ", 
                        finalGibberish, line);
            System.out.printf("      Pos: %s   :  %f\n", 
                    gibberishDetector.isGibberish(line)? "yes" : "no ", goodProb);
            System.out.printf("      Neg: %s   : %f\n\n", 
                    negDetector.isGibberish(line)? "no " : "yes", negProb);
        }		

        System.out.println("------------");
        System.out.println("Test bad lines"); // Display the string.
	System.out.println("------------");

        for (String line : badEnglishSentences) {
            double goodProb = (gibberishDetector.getProbability(line) - gibberishDetector.threshold) * gibberishDetector.weight;
            double negProb = (negDetector.getProbability(line) - negDetector.threshold) * negDetector.weight;
            double finalGibberish = goodProb - negProb/2;
            
            System.out.printf("Gibberish: %s %f : %s\n",
                    finalGibberish < 0 ? "TRUE  :" : "false : ", 
                        finalGibberish, line);
            System.out.printf("      Pos: %s   :  %f\n", 
                    gibberishDetector.isGibberish(line)? "yes" : "no ", goodProb);
            System.out.printf("      Neg: %s   : %f\n\n", 
                    negDetector.isGibberish(line)? "no " : "yes", negProb);
        }
    }
}