package is.parsing;


import is.objects.Camera;
import is.util.URLCaller;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 *
 * This class retrieves the info from the first page, where all cameras are
 * listed. It instanciate a list of camera objects an calls it's parsers to
 * retrieve the remaining information
 *
 *
 * @author msimoes
 */
public class CameraListParser {
    

    ArrayList <Camera> cameraList;

    private String INPUT_PAGE = null;

    public CameraListParser(String url) throws IOException {
        if(!ParserUtils.isLoaded)
            ParserUtils.loadRegexPatterns();
        
        URLCaller urlCaller = new URLCaller(url);
	INPUT_PAGE = urlCaller.getPage();

        cameraList = new ArrayList<Camera>();

    }


    public void parse() throws IOException{

       CameraParser cp = null;
       this.parseLinks();

       this.parseNames();
       this.parseDescriptions();
       this.parseReleasedDates();

       for (int i=0; i < cameraList.size(); i++){
           cp = new CameraParser(cameraList.get(i));
           cp.parseCamera();
       }
    }

   private void parseLinks(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_LINKS);
       Matcher m = p.matcher(INPUT_PAGE); // get a matcher object

       Camera tmp = null;
       int id = 0;
       while(m.find()) {
            try {
                tmp = new Camera(ParserUtils.base_url + m.group(1), id++);
                cameraList.add(tmp);
            } catch (IOException ex) {
                Logger.getLogger(CameraListParser.class.getName()).log(Level.SEVERE, null, ex);
            }
       }

   }

   private void parseNames(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_NAMES);
       Matcher m = p.matcher(INPUT_PAGE); // get a matcher object

       Camera tmp = null;
       int count = 0;
       while(m.find()) {
           tmp = cameraList.get(count);
           tmp.setName(m.group(1)+(m.group(2) != null?" "+m.group(2):""));
           count++;
       }
   }


   private void parseDescriptions(){
   
       Pattern p = Pattern.compile(ParserUtils.REGEX_DESCRIPTIONS);
       Matcher m = p.matcher(INPUT_PAGE); // get a matcher object

       Camera tmp = null;
       StringBuilder sb = null;
       int count = 0;
       while(m.find()) {
           tmp = cameraList.get(count);
           sb = new StringBuilder();
           for (int i = 1; i <= 4; i++ ){
               if (m.group(i) != null){
                   if (sb.length() > 0)
                        sb.append("; ");
                   sb.append(m.group(i));
               }
           }
           String aux = sb.toString();

           aux = aux.replaceAll("&nbsp;; ", "");
           aux = aux.replaceAll("&sup2;", "^2");
        
           tmp.setDescription(aux);
           count++;
       }
   }


   private void parseReleasedDates(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_RELEASED_DATES);
       Matcher m = p.matcher(INPUT_PAGE); // get a matcher object

       SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

       Camera tmp = null;
       int count = 0;
       while(m.find()) {
           tmp = cameraList.get(count);
            try {
                tmp.setReleasedDate(inputFormat.parse(m.group(1)));
            } catch (ParseException ex) {
                Logger.getLogger(CameraListParser.class.getName()).log(Level.SEVERE, null, ex);
            }
           count++;
       }
   }

    public ArrayList<Camera> getCameraList() {
        return cameraList;
    }

   
}
