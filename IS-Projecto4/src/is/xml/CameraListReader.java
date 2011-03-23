
package is.xml;

import is.objects.ModelAndDate;
import is.objects.ModelAndResolution;
import is.objects.Summary;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * Parses de XML info and extracts the best and worse resolutions, released dates, etc
 * 
 * @author disbeat
 */
public class CameraListReader{


    private Document doc = null;
    
    private Summary summary;

    public CameraListReader(Document d) throws JDOMException, IOException{

        this.doc = d;

        summary = new Summary();
    }

    public Summary parse(){
        Element base = doc.getRootElement();

          summary.setBrand(base.getAttributeValue("brand"));

          List<Element> list = base.getChildren();

          Iterator <Element> it = list.iterator();
          Element tmp = null;
          String tmpModel = null, tmpStr = null;
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

          Date tmpDate = null;
          while (it.hasNext()){
              tmp = it.next();

              tmpModel = tmp.getChildText("name");
              if (tmpModel != null){
                  summary.getModels().add(tmpModel);
              }

              ModelAndDate mostRecent = summary.getMostRecent();
              ModelAndDate older = summary.getOlder();
              tmpStr = tmp.getChildText("releasedDate");
              if (tmpStr != null){
                    try {
                        tmpDate = df.parse(tmpStr);
                        if (mostRecent == null || mostRecent.isOlder(tmpDate)){
                            summary.setMostRecent(new ModelAndDate(tmpModel, tmpDate));
                            summary.getMostRecentList().clear();
                            summary.getMostRecentList().add(summary.getMostRecent());
                        }else{
                            if (mostRecent.isEquals(tmpDate))
                                summary.getMostRecentList().add(new ModelAndDate(tmpModel, tmpDate));
                        }

                        if (older == null || older.isMoreRecent(tmpDate)){
                            summary.setOlder(new ModelAndDate(tmpModel, tmpDate));
                            summary.getOlderList().clear();
                            summary.getOlderList().add(summary.getOlder());
                        }else{
                            if (older.isEquals(tmpDate))
                                summary.getOlderList().add(new ModelAndDate(tmpModel, tmpDate));
                        }

                    } catch (ParseException ex) {
                        Logger.getLogger(CameraListReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }

              Element resolutionsElement = tmp.getChild("resolutions");
              if (resolutionsElement != null){

                  ModelAndResolution maxResolution = summary.getMaxResolution();
                  Element resolution = resolutionsElement.getChild("maxResolution");
                  if (resolution != null){
                      Integer maxRes[] = new Integer[2];
                      maxRes[0] = Integer.parseInt(resolution.getAttributeValue("width"));
                      maxRes[1] = Integer.parseInt(resolution.getAttributeValue("height"));

                      if (maxResolution == null || maxResolution.isSmaller(maxRes)){
                          summary.setMaxResolution(new ModelAndResolution(tmpModel, maxRes));
                          summary.getMaxResolutionList().clear();
                          summary.getMaxResolutionList().add(summary.getMaxResolution());
                      }else{
                          if (maxResolution.isEquals(maxRes))
                              summary.getMaxResolutionList().add(new ModelAndResolution(tmpModel, maxRes));
                      }
                  }


                  Element lowResolutions = resolutionsElement.getChild("lowResolutions");
                  if (lowResolutions != null){

                    ModelAndResolution minResolution = summary.getMinResolution();
                    List<Element> resolutions = lowResolutions.getChildren();
                    Iterator<Element> resIt = resolutions.iterator();
                    while(resIt.hasNext()){
                        resolution = resIt.next();
                        if (resolution != null){
                            Integer res[] = new Integer[2];
                            res[0] = Integer.parseInt(resolution.getAttributeValue("width"));
                            res[1] = Integer.parseInt(resolution.getAttributeValue("height"));

                            if (minResolution == null || minResolution.isBigger(res)){
                                summary.setMinResolution(new ModelAndResolution(tmpModel, res));
                                summary.getMinResolutionList().clear();
                                summary.getMinResolutionList().add(summary.getMinResolution());
                            }else{
                                if (minResolution.isEquals(res))
                                    summary.getMinResolutionList().add(new ModelAndResolution(tmpModel, res));
                            }
                        }
                    }
                  }
              }
          }
          summary.setNumCameras(summary.getModels().size());

          return summary;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    

    
}
