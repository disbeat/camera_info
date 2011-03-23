
package is.objects;

/**
 *
 * Class used to keep the relation between a camera model and its resolution
 * 
 * Makes the comparishon easier
 *
 * @author msimoes
 */
public class ModelAndResolution {

    private String model;
    private Integer resolution[];

    public ModelAndResolution(){

    }

    public ModelAndResolution(String model, Integer resolution[]){
        this.model = model;
        this.resolution = resolution;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer[] getResolution() {
        return resolution;
    }

    public Integer getTotalResolution(){
        return resolution[0] * resolution[1];
    }

    public void setResolution(Integer[] resolution) {
        this.resolution = resolution;
    }

    public boolean isBigger(Integer[] resolution){
        return (this.resolution[0] * this.resolution[1] > resolution[0] * resolution[1]);
    }

    public boolean isSmaller(Integer[] resolution){
        return (this.resolution[0] * this.resolution[1] < resolution[0] * resolution[1]);
    }

    public boolean isEquals(Integer[] resolution){
        return (this.resolution[0] * this.resolution[1] == resolution[0] * resolution[1]);
    }
}