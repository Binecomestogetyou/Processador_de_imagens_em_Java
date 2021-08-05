import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OperadordeImagens {

    /***********************************************************************************************************
     ***********************************************************************************************************/

    private static File arquivo = new File("/media/cicero/D/Programação/codigos_programas/java/filtro_de_imagens/src/banco_de_pixels");

    /***********************************************************************************************************
     ***********************************************************************************************************/

    private static class ImageToolkit {
        BufferedImage destino;
        WritableRaster fnt_raster;
        WritableRaster dst_raster;

        ImageToolkit(BufferedImage fonte){
            destino = new BufferedImage(fonte.getWidth(), fonte.getHeight(), fonte.getType());
            fnt_raster = fonte.getRaster();
            dst_raster = destino.getRaster();
        }

        void writeDown(){
            try {
                ImageIO.write(destino, "JPEG", new File("/media/pan/13d6a39b-d40a-45dd-aea4-56393048d009/Programação/codigos_programas/java/filtro_de_imagens/out/nova.jpeg"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("writDown() couldnt'n write anything");
            }
        }
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    // This method is supposed to create an image in greyscale
    public static void cinza (BufferedImage fonte){

        ImageToolkit cinzaToolkit = new ImageToolkit(fonte);

        int largura=cinzaToolkit.fnt_raster.getWidth();
        int altura=cinzaToolkit.fnt_raster.getHeight();
        int num_bandas=cinzaToolkit.fnt_raster.getNumBands();
        int media;

        for(int coluna=0; coluna<largura; coluna++){
            for(int linha=0; linha<altura; linha++){
                media = (cinzaToolkit.fnt_raster.getSample(coluna, linha, 0)+cinzaToolkit.fnt_raster.getSample(coluna, linha, 1)+cinzaToolkit.fnt_raster.getSample(coluna, linha, 2))/3;
                for(int banda=0; banda<num_bandas; banda++) cinzaToolkit.dst_raster.setSample(coluna, linha, banda, media);
            }
        }

        cinzaToolkit.writeDown();
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    // turns the image to redscale
    public static void vermelho (BufferedImage fonte){
        ImageToolkit redToolkit = new ImageToolkit(fonte);

        int largura=redToolkit.fnt_raster.getWidth();
        int altura=redToolkit.fnt_raster.getHeight();

        for(int coluna=0; coluna<largura; coluna++) {
            for (int linha = 0; linha < altura; linha++) {
                redToolkit.dst_raster.setSample(coluna, linha, 0, redToolkit.fnt_raster.getSample(coluna, linha, 0));
                redToolkit.dst_raster.setSample(coluna, linha, 1, 0);
                redToolkit.dst_raster.setSample(coluna, linha, 2, 0);
            }
        }

        redToolkit.writeDown();
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    public static void compare (BufferedImage imagem1, BufferedImage imagem2){
        WritableRaster fnt_raster=imagem1.getRaster();
        WritableRaster dst_raster=imagem2.getRaster();

        int largura=fnt_raster.getWidth();
        int altura=fnt_raster.getHeight();

        for(int coluna=0; coluna<largura; coluna++) {
            for (int linha = 0; linha < altura; linha++) {
                for (int banda=0; banda<3; banda++){
                    if(fnt_raster.getSample(coluna, linha, banda)!=dst_raster.getSample(coluna, linha, banda)){
                        System.out.println("As duas imagens são diferentes, a diferença ocorreu no pixel "+coluna+","+linha);
                        System.exit(0);
                    }
                }
            }
        }

        System.out.println("As duas imagens são iguais");
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    // O parametro fator indica o nivel de pixelagem d a imagem. Quanto mais baixo o fator,
    // mais próxima a imagem final ficará da real
    static void pixelador (BufferedImage origem, int fator)  {
        ImageToolkit pixeladorToolkit = new ImageToolkit(origem);
        List<int[]> allFrequencies = new ArrayList<>();
        BufferedImage nova_imagem = new BufferedImage(origem.getWidth(), origem.getHeight(), origem.getType());
        WritableRaster fonte = origem.getRaster();
        WritableRaster destino = nova_imagem.getRaster();
        int destino_larg = destino.getWidth();
        int destino_alt = destino.getHeight();
        List<int[][]> replacements = new ArrayList<>();
        long tempo;
        List<List<int[]>> partition = new ArrayList<>();


        // Now we're gonna get the frequencies of each color
        getFrequencies(pixeladorToolkit.fnt_raster, allFrequencies);



      /*  for(int i = 0; i < 20 ; i++){
            int[] a= allFrequencies.get(i);
            System.out.println("Color "+a[0]+" "+a[1]+" "+a[2]+" appears "+ a[3]+" times\n");
        }*/


        System.out.println("Finished saving frequencies");

        tempo = System.nanoTime();

        for(List<int[]> p : partition){
            OperadordeImagens.sort(p);
        }

        tempo = System.nanoTime() - tempo;
        tempo = (long) (tempo / 1E9);

        System.out.println("Finished sorting all frequencies\nIt took " + tempo / 60 + "min e " + tempo % 60 + "seg to sort all lists");

        allFrequencies.clear();

        for(List p : partition){
            allFrequencies.addAll(p);
        }

        tempo = System.nanoTime();

            // Now we find replacements
        {
            int[] mostFrequentColor = new int[4];
            int[][] carrier = new int[2][];
            int frequenceCap = 0;
            int lastFrequenceCap = 0;

            for ( int[] a : allFrequencies) {
                if(lastFrequenceCap == 0) {
                    if (frequenceCap < a[3]) {
                        mostFrequentColor = a;
                        frequenceCap = a[3];
                    }
                }
                else {
                    if (frequenceCap < a[3] && a[3] <= lastFrequenceCap) {
                        mostFrequentColor = a;
                        frequenceCap = a[3];
                    }
                }
            }

            lastFrequenceCap = frequenceCap;

            {
                int control = allFrequencies.size();
                for (int i = 0; i < control; i++) {
                    try {
                        carrier[0] = allFrequencies.get(i);
                        carrier[1] = mostFrequentColor;

                        if (isInRange(carrier[0], carrier[1], fator)) {
                            replacements.add(carrier);
                            allFrequencies.remove(allFrequencies.get(i));
                        }
                    }catch (IndexOutOfBoundsException e){ break; }
                }
            }
        }

        for(int[][] r : replacements){
            try {
                arquivo.write("Color ("+r[0][0]+", "+r[0][1]+", "+r[0][2]+") will be replaced by ("+r[1][0]+", "+r[1][1]+", "+r[1][2]+")\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tempo = System.nanoTime()-tempo;

        tempo = (long) (tempo/1E9);

        System.out.println("Finished finding replacements\nIt took "+tempo/60+"min e "+tempo%60+"seg");

        tempo = System.nanoTime();
        int count_replacement = 1;

        // now we replace
        for(int[][] a : allPixels){
            for(int[] b : a){
                for(int[][] t : replacements){
                    if(t[0][3] < 1) break;
                    if(b[0] == t[0][0] && b[1] == t[0][1] && b[2] == t[0][2]){
                        b[0] = t[1][0];
                        b[1] = t[1][1];
                        b[2] = t[1][2];
                        t[0][3]--;
                    }
                }
            }
        }

        tempo = System.nanoTime() - tempo;
        tempo = (long)(tempo/1E9);

        System.out.println("Finished replacing\nIt took "+tempo/60+"min and "+tempo%60+" seg");

        System.out.println("Writing raster");

        tempo = System.nanoTime();

        try {
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < destino_larg; j++) {
                    for (int k = 0; k < 3; k++) {
                        destino.setSample(j, i, k, allPixels[i][j][k]);
                    }
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("");
            e.printStackTrace();
        }



        tempo = System.nanoTime() - tempo;
        tempo = (long)(tempo/1E9);

        System.out.println("Finished writing\nIt took "+tempo/60+"min and "+tempo%60+" seg");


        String string = Long.toString(System.nanoTime());
        FileOutputStream novo = new FileOutputStream(new File("/media/pan/13d6a39b-d40a-45dd-aea4-56393048d009/Programação/codigos_programas/java/filtro_de_imagens/pixelada"));


        try {
            ImageIO.write( nova_imagem, "JPEG", novo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    private void getFrequencies(WritableRaster raster, List<int[]> lista) {

        int altura = raster.getHeight();
        int largura = raster.getWidth();
        int bandas = raster.getNumBands();
        int[] array_aux = new int[bandas];

        for(int linha = 0; linha < altura; linha++){
            for(int pixel = 0; pixel < largura; largura++){
                raster.getPixel(pixel, altura, array_aux);
                addPixelOrderedly(array_aux, lista);

            }
        }
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    // Adds *pixel* to *lista* in an ordered way
    private void addPixelOrderedly(int[] pixel, List<int[]> lista){
        lista.add(findRightPosition(pixel, lista), pixel);
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/

    private int findRightPosition(int[] pixel, List<int[]> lista) {
        int position;
        int pivot = (int)(lista.size()/2);
        int[] pixelFromLista = lista.get(pivot);

        if(pixelFromLista[0] == pixel[0]) position = pivot;
        else if(pixelFromLista[0] > pixel[0]) {
            if(lista.isEmpty())
            position = pivot + findRightPosition(pixel, lista.subList(pivot, lista.size()-1));
        }
        else position = findRightPosition(pixel, lista.subList(0, pivot-1));

        return position;
    }

    /***********************************************************************************************************
     ***********************************************************************************************************/


    private static boolean findArray(int[] array, List<List<int[]>> list) {
        int pivot = array[0]/16;
        boolean found = false;

        for (int[] l : list.get(pivot)) {
            if (list.get(pivot).isEmpty()) break;
            if (array[0] == l[0] && array[1] == l[1] && array[2] == l[2]) {
                l[3]++;
                found = true;
            }
        }


        return found;
    }

    public static boolean isInRange(int[] toBeTested, int[] reference, int rangeSize){
        int inf, sup = rangeSize/2;

        inf = rangeSize%2 == 0 ? sup - 1 : sup;

        return (reference[0] - inf < toBeTested[0] && toBeTested[0] < reference[0] + sup) && (reference[1] - inf < toBeTested[1] && toBeTested[1] < reference[1] + sup) && (reference[2] - inf < toBeTested[2] && toBeTested[2] < reference[2] + sup);
    }


    static void divisor_vertical(BufferedImage origem) throws FileNotFoundException{
        BufferedImage destino_1, destino_2;

        int origemWidth = origem.getWidth();
        int origemHeight = origem.getHeight();

        // Construímos os Buffered Image das imagens resultantes
        if(origemWidth%2 == 0){
            destino_1=new BufferedImage(origemWidth/2, origemHeight, origem.getType());
        }
        else{
            destino_1=new BufferedImage(origemWidth/2+1, origemHeight, origem.getType());
        }
        destino_2=new BufferedImage(origemWidth/2, origemHeight, origem.getType());

        // Criamos os writable raster da imagem de origem e das imagens de destino
        WritableRaster raster_1=destino_1.getRaster();
        WritableRaster raster_2=destino_2.getRaster();
        WritableRaster raster_origem=origem.getRaster();

        int bands=raster_origem.getNumBands();
        int sample;

        for(int i = 0; i < origemWidth; i++) {
            for (int j = 0; j < origemHeight; j++) {
                for (int k = 0; k < bands; k++) {
                        if (i % 2 == 1) {
                            sample = raster_origem.getSample(i, j, k);
                            raster_1.setSample(i / 2, j, k, sample);
                        } else {
                            sample = raster_origem.getSample(i, j, k);
                            raster_2.setSample(i / 2, j, k, sample);
                        }
                }
            }
        }

        FileOutputStream nova1= new FileOutputStream("/media/pan/13d6a39b-d40a-45dd-aea4-56393048d009/Programação/codigos_programas/java/filtro_de_imagens/out/Vertical_1.jpeg");
        FileOutputStream nova2=new FileOutputStream("/media/pan/13d6a39b-d40a-45dd-aea4-56393048d009/Programação/codigos_programas/java/filtro_de_imagens/out/Vertical_2.jpeg");

        try{
            ImageIO.write(destino_1, "JPEG", nova1);
            ImageIO.write(destino_2, "JPEG", nova2);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

 /*   public static void permission(){
        char p = 0;
        try {
            p = (char) System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(p == '\n') {
            try {
                p = (char) System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(p != 'c') System.exit(0);

        return;
    }*/
}