
package is.parsing;

import is.objects.Camera;
import is.util.URLCaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Aplies the regular expressions to retrieve the camera's info
 *
 *
 * @author msimoes
 */
public class CameraParser {

    private Camera cam = null;
    private String page = null;


    public CameraParser(Camera cam){
        this.cam = cam;
        if (!ParserUtils.isLoaded)
            ParserUtils.loadRegexPatterns();
    }



    public void parseCamera() throws IOException{
        if (cam.getPage() == null){
            URLCaller urlCaller = new URLCaller(cam.getUrlReview());
            cam.setPage(urlCaller.getPage());
        }

        this.page = cam.getPage();

        parseSummary();
        parseImgLink();
        parseEffectivePixels();
        parseISORating();
        parseImageRatio();
        parseLinkInDepthReview();
        parseLowResolution();
        parseMaxResolution();
        parseMaxShutter();
        parseMinShutter();
        parseSensorSize();


        cam.createXMLElement();


    }



    public void parseSummary(){
       

       Pattern p = Pattern.compile(ParserUtils.REGEX_SUMMARY);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setSummary(m.group(1));
       }
    }

    public void parseImgLink(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_IMG);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setLinkPicture(m.group(1));
       }
    }


    public void parseLinkInDepthReview(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_INDEPTHREVIEW);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setLinkInDepthReview(ParserUtils.base_url + m.group(1));
       }
    }

    public void parseMaxResolution(){
       Integer[] maxResolution = new Integer[2];

       Pattern p = Pattern.compile(ParserUtils.REGEX_MAXRESOLUTION);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           maxResolution[0] = Integer.parseInt(m.group(1));
           maxResolution[1] = Integer.parseInt(m.group(2));
       }
       cam.setMaxResolution(maxResolution);
    }

    public void parseMaxShutter(){       

       Pattern p = Pattern.compile(ParserUtils.REGEX_MAXSHUTTER);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setMaxShutter(m.group(1));
       }
    }

    public void parseMinShutter(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_MINSHUTTER);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setMinShutter(m.group(1));
       }
    }

    public void parseEffectivePixels(){

       Pattern p = Pattern.compile(ParserUtils.REGEX_EFFECTIVEPIXELS);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           cam.setEffectivePixels(m.group(1));
       }
    }

    public void parseImageRatio(){

       String tmp = null;
       Integer[] imgRatio_tmp;
       LinkedList<Integer[]> imageRatio = new LinkedList<Integer[]>();

       Pattern p = Pattern.compile(ParserUtils.REGEX_IMAGERATIO);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           tmp = m.group(1);
       }

       String inner_regex = "(\\d+):(\\d+)";

       p = Pattern.compile(inner_regex);
       m = p.matcher(tmp); // get a matcher object

       while (m.find()) {
           imgRatio_tmp = new Integer[2];
           imgRatio_tmp[0] = Integer.parseInt(m.group(1));
           imgRatio_tmp[1] = Integer.parseInt(m.group(2));
           imageRatio.add(imgRatio_tmp);
       }
       cam.setImageRatio(imageRatio);
    }


    public void parseSensorSize(){
       Float[] sensorSize = new Float[2];

       Pattern p = Pattern.compile(ParserUtils.REGEX_SENSORSIZE);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           sensorSize[0] = Float.parseFloat(m.group(1));
           sensorSize[1] = Float.parseFloat(m.group(2));
       }
       cam.setSensorSize(sensorSize);
    }


    public void parseLowResolution(){
       
       String tmp = null;
       Integer[] resolution_tmp;
       LinkedList<Integer[]> lowResolution = new LinkedList<Integer[]>();

       Pattern p = Pattern.compile(ParserUtils.REGEX_LOWRESOLUTION);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           tmp = m.group(1);
       }

       String inner_resolution = "(\\d+)\\sx\\s(\\d+)";

       p = Pattern.compile(inner_resolution);
       m = p.matcher(tmp); // get a matcher object

       while (m.find()) {
           resolution_tmp = new Integer[2];
           resolution_tmp[0] = Integer.parseInt(m.group(1));
           resolution_tmp[1] = Integer.parseInt(m.group(2));
           lowResolution.add(resolution_tmp);
       }

       cam.setLowResolution(lowResolution);
    }


    public void parseISORating(){
       String tmp = null;
       LinkedList<String> ISOrating = new LinkedList<String>();

       Pattern p = Pattern.compile(ParserUtils.REGEX_ISORATING);
       Matcher m = p.matcher(page); // get a matcher object

       if (m.find()) {
           tmp = m.group(1);
       }

       String inner_regex = "([^,]*),?";

       p = Pattern.compile(inner_regex);
       m = p.matcher(tmp); // get a matcher object

       while (m.find()) {
           ISOrating.add(m.group(1));
       }
       cam.setISOrating(ISOrating);
    }

}
