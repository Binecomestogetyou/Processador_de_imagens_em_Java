public class Pixel {
    int red, green, blue, alpha;
    int[] array;

    Pixel() {}

    Pixel(int... channels){
        red = channels[0];
        green = channels[1];
        blue = channels[2];

        if(channels.length == 4) {
            alpha = channels[3];
            array = new int[]{channels[0], channels[1], channels[2], channels[3]};
        }
        else array = new int[]{channels[0], channels[1], channels[2]};
    }

    public int compareRed(Pixel comparer){
        int result;

        if(this.red > comparer.red) result = 1;
        else if(this.red == comparer.red) result = 0;
        else result = -1;

        return result;
    }

    public void build() {
        red = array[0];
        green = array[1];
        blue = array[2];

        try{
            alpha = array[3];
        }
        catch (IndexOutOfBoundsException e){}
    }
}
