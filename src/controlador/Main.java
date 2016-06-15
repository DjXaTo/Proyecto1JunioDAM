package controlador;

// @author Otaku
import controlador.Controlador;
import vista.VistaPrincipal;

public class Main {

    public static void main(String[] args) {
        new Controlador(new VistaPrincipal()).iniciar();
    }

}
