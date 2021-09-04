import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class Frequency {
    List<List<List<List<Pixel>>>> pixel = new ArrayList<>();
    List<List<List<List<Integer>>>> frequency = new ArrayList<>();

    /***********************************************
     **********************************************/


    Frequency(){
        /* Building pixel list */
        // Adds red lists
        for(int i = 0; i < 16; i++) pixel.add(new ArrayList<>());

        // Adds green lists
        for(List<List<List<Pixel>>> a : pixel){
            for(int i = 0; i < 16; i++) a.add(new ArrayList<>());
        }

        // Adds blue lists
        for(List<List<List<Pixel>>> a : pixel){
            for(List<List<Pixel>> b : a){
                for(int i = 0; i < 16; i++) b.add(new ArrayList<>());
            }
        }

        //////////////////////////////////////

        /* Building frequency lists */
        // Adds red lists
        for(int i = 0; i < 16; i++) frequency.add(new ArrayList<>());

        // Adds green lists
        for(List<List<List<Integer>>> a : frequency){
            for(int i = 0; i < 16; i++) a.add(new ArrayList<>());
        }

        // Adds blue lists
        for(List<List<List<Integer>>> a : frequency){
            for(List<List<Integer>> b : a){
                for(int i = 0; i < 16; i++) b.add(new ArrayList<>());
            }
        }
    }

    /***********************************************
     **********************************************/

    static void getFrequencies(WritableRaster raster, Frequency lista) {

        int altura = raster.getHeight();
        int largura = raster.getWidth();
        Pixel pixel_aux = new Pixel(raster.getNumBands());

        for(int linha = 0; linha < altura; linha++){
            for(int coluna = 0; coluna < largura; largura++){
                raster.getPixel(coluna, linha, pixel_aux.array);

                Frequency.incrementFrequenciesList(pixel_aux, lista);
            }
        }
    }

    /***********************************************
     **********************************************/

    public static void incrementFrequenciesList(Pixel pixel, Frequency frequency){

        List<Pixel> pixelList = frequency.pixel.get(pixel.getRed()%16).get(pixel.getGreen()%16).get(pixel.getBlue()%16);
        List<Integer> frequencyList = frequency.frequency.get(pixel.getRed()%16).get(pixel.getGreen()%16).get(pixel.getBlue()%16);

        int position = pixelList.indexOf(pixel);

        if(position != -1){
            int aux = frequencyList.get(position) + 1;

            frequencyList.remove(position);

            try{
                frequencyList.add(position, aux);
            }
            catch (IndexOutOfBoundsException e){
                frequencyList.add(aux);
            }
        }
        else {
            int aux = pixel.findPosition(pixelList);
            pixelList.add(pixel);
            frequencyList.add(1);
        }
    }
}
