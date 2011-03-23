package is.objects;

import java.util.Date;

/**
 * Class used to keep the relation between a camera model and its released date
 *
 * Makes the comparishon easier
 *
 * @author msimoes
 */
public class ModelAndDate {
    private String model;
    private Date date;

    public ModelAndDate(){

    }

    public ModelAndDate(String model, Date date){
        this.model = model;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isOlder(Date date){
        return this.date.after(date);
    }

    public boolean isMoreRecent(Date date){
        return this.date.before(date);
    }

    public boolean isEquals(Date date){
        return (!this.date.after(date) && !this.date.before(date));
    }

}
