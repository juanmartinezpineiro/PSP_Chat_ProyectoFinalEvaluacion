package chatcliente;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Juan Martínez
 */
public class ChatCliente {

    
    public static void main(String[] args) {
        IGrafica ventana = new IGrafica();
        ventana.setVisible(true);
    }
}
