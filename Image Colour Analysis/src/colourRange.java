
public class colourRange {

    private double frequency;
    private int[] RGB = new int[3];

    public colourRange(int[] RGB){
        this.RGB = RGB;
        frequency = 1;
    }
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
