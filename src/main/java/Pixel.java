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

    public int compareRed(Pixel compared){

        return Integer.compare(this.array[0], compared.array[0]);
    }

    /***********************************************************
     **********************************************************/

    public int compareBlue(Pixel compared){

        return Integer.compare(this.array[2], compared.array[2]);
    }

    /***********************************************************
     **********************************************************/

    public int compareGreen(Pixel compared){

        return Integer.compare(this.array[1], compared.array[1]);
    }

    /***********************************************************
     **********************************************************/

    int findPosition(List<Pixel> list, int left, int right){
        int position = 0;

        if(left == right){
            switch (this.compareRed(list.get(left))) {
                case -1 -> position = left - 1;
                case 0 -> position = switch (this.compareGreen(list.get(left))) {
                    case -1 -> left - 1;
                    case 0 -> switch (this.compareBlue(list.get(left))) {
                        case -1 -> left - 1;
                        case 0 -> -left;
                        case 1 -> left + 1;
                        default -> position;
                    };
                    case 1 -> left + 1;
                    default -> position;
                };
                case 1 -> position = left + 1;
            }
        }
        else {

            int middle = (right - left) / 2;

            position = switch (this.compareRed(list.get(middle))) {
                case -1 -> this.findPosition(list, left, middle);
                case 0 -> switch (this.compareGreen(list.get(middle))) {
                    case -1 -> this.findPosition(list, left, middle);
                    case 0 -> switch (this.compareBlue(list.get(middle))) {
                        case -1 -> this.findPosition(list, left, middle);
                        case 0 -> -middle;
                        case 1 -> this.findPosition(list, middle, right);
                        default -> position;
                    };
                    case 1 -> this.findPosition(list, middle, right);
                    default -> position;
                };
                case 1 -> this.findPosition(list, middle, right);
                default -> position;
            };
        }

        return position;
    }

    public String toString(){
        return "(" + this.getRed() + ", " + this.getGreen() + ", " + this.getBlue() + ")";
    }
}
