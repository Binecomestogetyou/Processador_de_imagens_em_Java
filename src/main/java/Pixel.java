import java.util.List;

public class Pixel {
    int[] array;

    /***********************************************************
     **********************************************************/

    Pixel(int numBands) {
        array = new int[numBands];
    }

    /***********************************************************
     **********************************************************/

    Pixel(int[] array){
        this.array = new int[array.length];

        System.arraycopy(array, 0, this.array, 0, array.length);
    }

    /***********************************************************
     **********************************************************/

    int getRed(){
        return array[0];
    }

    /***********************************************************
     **********************************************************/

    int getGreen(){
        return array[1];
    }

    /***********************************************************
     **********************************************************/

    public int getBlue(){
        return array[2];
    }

    /***********************************************************
     **********************************************************/

    public int compareRed(Pixel comparer){

        return Integer.compare(this.array[0], comparer.array[0]);
    }

    /***********************************************************
     **********************************************************/

    int findPosition(List<Pixel> list){
        int toReturn;
        if(!list.contains(this)) toReturn = list.indexOf(this);
        else{
            toReturn = this.findPosition(list, 0, list.size() - 1);
        }

        return toReturn;
    }

    int findPosition(List<Pixel> list, int left, int right){
        int middle = (right - left)/2;
        /* Find Red */

        switch(this.compareRed(list.get(middle))){
            case -1:
                this.findPosition(list, left, middle);
                break;

            case 0:
                break;

            case 1:
                this.findPosition(list, middle, right);
        }

    }
}
