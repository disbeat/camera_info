
package is.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Regular expressions interface between the app and the file
 *
 * 
 * @author msimoes
 */
public class ParserUtils {
    
    public static boolean isLoaded = false;

    public static String base_url;
    public static String REGEX_SUMMARY;
    public static String REGEX_IMG;
    public static String REGEX_INDEPTHREVIEW;
    public static String REGEX_MAXRESOLUTION;
    public static String REGEX_MAXSHUTTER;
    public static String REGEX_MINSHUTTER;
    public static String REGEX_EFFECTIVEPIXELS;
    public static String REGEX_IMAGERATIO;
    public static String REGEX_SENSORSIZE;
    public static String REGEX_LOWRESOLUTION;
    public static String REGEX_ISORATING;
    public static String REGEX_LINKS;
    public static String REGEX_NAMES;
    public static String REGEX_DESCRIPTIONS;
    public static String REGEX_RELEASED_DATES;
    

    public static boolean loadRegexPatterns(){
        File configurationFile =  new File("conf" + File.separatorChar + "CameraSearchXML.properties");
        if (configurationFile.exists()) {
            Properties configuration = new Properties();
            try {
                configuration.load(new FileInputStream(configurationFile));

                base_url = configuration.getProperty("base_url");
                REGEX_EFFECTIVEPIXELS = configuration.getProperty("REGEX_EFFECTIVEPIXELS");
                REGEX_IMAGERATIO = configuration.getProperty("REGEX_IMAGERATIO");
                REGEX_IMG = configuration.getProperty("REGEX_IMG");
                REGEX_INDEPTHREVIEW = configuration.getProperty("REGEX_INDEPTHREVIEW");
                REGEX_ISORATING = configuration.getProperty("REGEX_ISORATING");
                REGEX_LOWRESOLUTION = configuration.getProperty("REGEX_LOWRESOLUTION");
                REGEX_MAXRESOLUTION = configuration.getProperty("REGEX_MAXRESOLUTION");
                REGEX_MAXSHUTTER = configuration.getProperty("REGEX_MAXSHUTTER");
                REGEX_MINSHUTTER = configuration.getProperty("REGEX_MINSHUTTER");
                REGEX_SENSORSIZE = configuration.getProperty("REGEX_SENSORSIZE");
                REGEX_SUMMARY = configuration.getProperty("REGEX_SUMMARY");
                REGEX_LINKS = configuration.getProperty("REGEX_LINKS");
                REGEX_NAMES = configuration.getProperty("REGEX_NAMES");
                REGEX_DESCRIPTIONS = configuration.getProperty("REGEX_DESCRIPTIONS");
                REGEX_RELEASED_DATES = configuration.getProperty("REGEX_RELEASED_DATES");
                isLoaded = true;
                return true;
            } catch (Exception e) {
               System.out.println("Could not load configuration file \""
                                   + configurationFile + "\" - " + e.getMessage());
               e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Configuration file not found: "+configurationFile);
            return false;
        }
    }

}
