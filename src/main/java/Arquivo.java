import java.io.File;

public class Arquivo {
    String caminho;
    File arquivo;

    Arquivo(String path){
        arquivo= new File(path);
        caminho= path;
    }

    File retornarArquivo(){
        return arquivo;
    }
}
