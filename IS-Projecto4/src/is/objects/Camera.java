package is.objects;


import is.util.URLCaller;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.*;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * Camera object for internal representation.
 *
 * @author lopes and msimoes
 */
public class Camera{


    String name, description, effectivePixels, minShutter, maxShutter,
                linkInDepthReview, linkPicture, summary;
    Date releasedDate;
    Integer maxResolution[];

    SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");

    LinkedList <Integer[]> lowResolution, imageRatio;
    LinkedList <String> ISOrating;

    Float sensorSize[];
    String page = null, urlReview = null, base_url = "http://www.dpreview.com";

    Element cameraElement;

    int id;



    public Camera(String url, int id) throws IOException
    {
        URLCaller urlCaller = new URLCaller(url);
        page = urlCaller.getPage();
	this.id = id;
    }
    
    public String getStringXMLDoc()
    {
        Document myDocument = new Document(cameraElement);
        XMLOutputter xout = new XMLOutputter();
        xout.setFormat(Format.getPrettyFormat());
        System.out.println("XML"+xout.outputString(myDocument));
        return xout.outputString(myDocument);
    }



    //creates the XML element which will be used to build the final XML Doc.
    public void createXMLElement(){
        cameraElement = new Element("camera");
        cameraElement.setAttribute("id", Integer.toString(id));
        Element el;

        el = new Element("name");
        el.addContent(name);
        cameraElement.addContent(el);

        el = new Element("description");
        el.addContent(description);
        cameraElement.addContent(el);

        el = new Element("summary");
        el.addContent(summary);
        cameraElement.addContent(el);

        el = new Element("effectivePixels");
        el.addContent(effectivePixels);
        cameraElement.addContent(el);

        el = new Element("shutterTime");
        el.setAttribute("min", minShutter);
        el.setAttribute("max", maxShutter);
        cameraElement.addContent(el);

        Element links = new Element("links");


        el = new Element("inDepthReview");
        el.addContent(linkInDepthReview);
        links.addContent(el);


        el = new Element("picture");
        el.addContent(linkPicture);
        links.addContent(el);

        cameraElement.addContent(links);

        el = new Element("releasedDate");
        el.addContent(outputDate.format(releasedDate));
        cameraElement.addContent(el);

        Element resolutions = new Element("resolutions");

        el = new Element("maxResolution");
        el.setAttribute("width", maxResolution[0]+"");
        el.setAttribute("height",  maxResolution[1]+"");
        resolutions.addContent(el);

        ListIterator it;
        Integer nums[];

        if (lowResolution != null)
        {
            Element res = new Element("lowResolutions");
            it=lowResolution.listIterator();
            while (it.hasNext())
            {
                nums=(Integer[]) it.next();
                el = new Element("resolution");
                el.setAttribute("width", nums[0].toString());
                el.setAttribute("height", nums[1].toString());
                res.addContent(el);
            }
            resolutions.addContent(res);
        }

        cameraElement.addContent(resolutions);

        if (imageRatio!=null)
        {
            Element ir = new Element("imageRatios");
            it=imageRatio.listIterator();
            while (it.hasNext())
            {
                nums=(Integer[]) it.next();
                el = new Element("imageRatio");
                el.setAttribute("width", nums[0].toString());
                el.setAttribute("height", nums[1].toString());
                ir.addContent(el);
            }
            cameraElement.addContent(ir);
        }


        String v;
        if (ISOrating!=null)
        {
            Element ir = new Element("ISOratings");
            it=ISOrating.listIterator();
            while (it.hasNext())
            {
                v=(String) it.next();
                if (v != null){
                    el = new Element("ISOrating");
                    el.addContent(v);
                    ir.addContent(el);
                }
            }
            cameraElement.addContent(ir);
        }



        el = new Element("sensorSize");
        el.setAttribute("width", sensorSize[0].toString());
        el.setAttribute("height",  sensorSize[1].toString());
        cameraElement.addContent(el);
    }

    public Element getXMLElement()
    {
        return this.cameraElement;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEffectivePixels() {
        return effectivePixels;
    }

    public void setEffectivePixels(String effectivePixels) {
        this.effectivePixels = effectivePixels;
    }

    public String getLinkPicture() {
        return linkPicture;
    }

    public void setLinkPicture(String linkPicture) {
        this.linkPicture = linkPicture;
    }

    public String getLinkInDepthReview() {
        return linkInDepthReview;
    }

    public void setLinkInDepthReview(String linkReview) {
        this.linkInDepthReview = linkReview;
    }

    public String getMaxShutter() {
        return maxShutter;
    }

    public void setMaxShutter(String maxShutter) {
        this.maxShutter = maxShutter;
    }

    public String getMinShutter() {
        return minShutter;
    }

    public void setMinShutter(String minShutter) {
        this.minShutter = minShutter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = releasedDate;
    }

    public Integer[] getMaxResolution() {
        return maxResolution;
    }

    public void setMaxResolution(Integer[] resolution) {
        this.maxResolution = resolution;
    }

    public Float[] getSensorSize() {
        return sensorSize;
    }

    public void setSensorSize(Float[] sensorSize) {
        this.sensorSize = sensorSize;
    }

    public String getUrlReview() {
        return urlReview;
    }

    public void setUrlReview(String urlReview) {
        this.urlReview = urlReview;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LinkedList<Integer[]> getImageRatio() {
        return imageRatio;
    }

    public void setImageRatio(LinkedList<Integer[]> imageRatio) {
        this.imageRatio = imageRatio;
    }

    public LinkedList<Integer[]> getLowResolution() {
        return lowResolution;
    }

    public void setLowResolution(LinkedList<Integer[]> lowResolution) {
        this.lowResolution = lowResolution;
    }

    public LinkedList<String> getISOrating() {
        return ISOrating;
    }

    public void setISOrating(LinkedList<String> ISOrating) {
        this.ISOrating = ISOrating;
    }

    
}
