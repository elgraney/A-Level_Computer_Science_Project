package sample;

/**
 * Created by ma.crane on 07/02/2017.
 */

//ColourRange exists to facilitate the Modal analysis method
//It is a way of storing an RGB value with an associated frequency.
//In the Modal analysis method, the instance of colourRange with the highest frequency becomes the modal colour.
public class ColourRange {

    //frequency can be any real value (due to non-integer modifiers) so must be double.
    private double frequency;
    private int[] RGB = new int[3];

    //colourRange is initialised with an passed RGB value and a frequency of 1
    public ColourRange(int[] RGB){
        this.RGB = RGB;
        frequency = 1;
    }
    //the following methods allow the contents of colourRange to be viewed and edited from other classes.
    public int getRGB(int colour){
        return RGB[colour];
    }
    public  void incrementFrequency(double modifiers){
        frequency+=modifiers;
    }
    public double getFrequency(){
        return frequency;
    }

}
