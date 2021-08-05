import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class principal {

    public static void main(String[] Args) {

        BufferedImage origem = newBufferedImage();

      //  OperadordeImagens.pixelador(origem, 9);
    }

    static BufferedImage newBufferedImage(){
        BufferedImage newBufferedImage = null;
        try {
            newBufferedImage = ImageIO.read(new FileInputStream("/media/pan/13d6a39b-d40a-45dd-aea4-56393048d009/Programação/codigos_programas/java/filtro_de_imagens/src/main/java/imagens/image"));
        }
        catch (IOException e){
            System.out.println("Failed to open file\n");
            e.printStackTrace();
            System.exit(1);
        }

        return newBufferedImage;
    }


}