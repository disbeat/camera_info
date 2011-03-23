/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.objects;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Element;

/**
 *
 * Summary object for internal representation
 *
 * @author disbeat
 */
public class Summary {

    private String brand = null;
    private int numCameras = 0;
    private LinkedList<ModelAndDate> mostRecentList = new LinkedList<ModelAndDate>(), olderList = new LinkedList<ModelAndDate>();
    private LinkedList<ModelAndResolution> maxResolutionList = new LinkedList<ModelAndResolution>(), minResolutionList = new LinkedList<ModelAndResolution>();
    private ModelAndDate mostRecent = null, older = null;
    private ModelAndResolution maxResolution = null, minResolution = null;
    private List <String> models = new LinkedList<String>();

    private Element element = null;
    private SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");

    public Summary(){
        
    }

    public void createXMLElement(){
        element = new Element("manufacturer");
        element.setAttribute("name", brand);

        Element totCameras = new Element("totalCameras");
        totCameras.addContent(Integer.toString(numCameras));
        element.addContent(totCameras);

        //most recent cam node
        Element mostRecentCams= new Element("mostRecentCameras");
        Element modelList = new Element("modelsList");
        Element date = new Element("releasedDate");
        Element tmp = null;
        mostRecentCams.addContent(date);
        mostRecentCams.addContent(modelList);

        for (ModelAndDate cam : mostRecentList){
            tmp = new Element("model");
            tmp.addContent(cam.getModel());
            modelList.addContent(tmp);
        }
        date.addContent(outputDate.format(mostRecent.getDate()));
        element.addContent(mostRecentCams);

        //older cam
        Element olderCams= new Element("olderCameras");
        modelList = new Element("modelsList");
        date = new Element("releasedDate");

        olderCams.addContent(date);
        olderCams.addContent(modelList);

       for (ModelAndDate cam : olderList){
            tmp = new Element("model");
            tmp.addContent(cam.getModel());
            modelList.addContent(tmp);
        }
        date.addContent(outputDate.format(older.getDate()));
        element.addContent(olderCams);


        Element resolutions = new Element("resolutions");

        //min res cams
        Element minCams = new Element("minResolutionCameras");
        modelList = new Element("modelsList");
        Element res = new Element("resolution");

        minCams.addContent(res);
        minCams.addContent(modelList);



        Integer total = minResolution.getTotalResolution();

        res.setAttribute("totalPixels", total.toString());

        for (ModelAndResolution cam : minResolutionList){
            tmp = new Element("model");
            tmp.addContent(cam.getModel());
            modelList.addContent(tmp);
        }

        //max res cams
        Element maxCams = new Element("maxResolutionCameras");
        modelList = new Element("modelsList");
        res = new Element("resolution");

        maxCams.addContent(res);
        maxCams.addContent(modelList);



        total = maxResolution.getTotalResolution();

        res.setAttribute("totalPixels", total.toString());

        for (ModelAndResolution cam : maxResolutionList){
            tmp = new Element("model");
            tmp.addContent(cam.getModel());
            modelList.addContent(tmp);
        }


        resolutions.addContent(minCams);
        resolutions.addContent(maxCams);

        element.addContent(resolutions);


        //models list
        Element modelsList = new Element("modelsList");
        Iterator <String>it = models.iterator();
        Element modelName = null;
        while(it.hasNext()){
            String name = it.next();
            modelName = new Element("model");
            modelName.addContent(name);
            modelsList.addContent(modelName);
        }
        element.addContent(modelsList);
    }


    public Element getXMLElement(){
        return element;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ModelAndResolution getMaxResolution() {
        return maxResolution;
    }

    public void setMaxResolution(ModelAndResolution maxResolution) {
        this.maxResolution = maxResolution;
    }

    public LinkedList<ModelAndResolution> getMaxResolutionList() {
        return maxResolutionList;
    }

    public void setMaxResolutionList(LinkedList<ModelAndResolution> maxResolutionList) {
        this.maxResolutionList = maxResolutionList;
    }

    public ModelAndResolution getMinResolution() {
        return minResolution;
    }

    public void setMinResolution(ModelAndResolution minResolution) {
        this.minResolution = minResolution;
    }

    public LinkedList<ModelAndResolution> getMinResolutionList() {
        return minResolutionList;
    }

    public void setMinResolutionList(LinkedList<ModelAndResolution> minResolutionList) {
        this.minResolutionList = minResolutionList;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public ModelAndDate getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(ModelAndDate mostRecent) {
        this.mostRecent = mostRecent;
    }

    public LinkedList<ModelAndDate> getMostRecentList() {
        return mostRecentList;
    }

    public void setMostRecentList(LinkedList<ModelAndDate> mostRecentList) {
        this.mostRecentList = mostRecentList;
    }

    public int getNumCameras() {
        return numCameras;
    }

    public void setNumCameras(int numCameras) {
        this.numCameras = numCameras;
    }

    public ModelAndDate getOlder() {
        return older;
    }

    public void setOlder(ModelAndDate older) {
        this.older = older;
    }

    public LinkedList<ModelAndDate> getOlderList() {
        return olderList;
    }

    public void setOlderList(LinkedList<ModelAndDate> olderList) {
        this.olderList = olderList;
    }


}
