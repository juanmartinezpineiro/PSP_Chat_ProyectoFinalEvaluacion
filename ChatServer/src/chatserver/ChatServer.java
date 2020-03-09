package chatserver;

import static chatserver.ChatServer.client;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Juan Martínez
 */
public class ChatServer {

    static ArrayList<Cliente> client = new ArrayList();

    public static void main(String[] args) {

        int port = Integer.parseInt(JOptionPane.showInputDialog("Indica puerto:"));

        try {
            //Crea el socket
            System.out.println("Crea el socket");
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
            //Se mantiene a la espera de conexiones
                System.out.println("A la espera");
                Socket newSocket = serverSocket.accept();
                InputStream is = newSocket.getInputStream();
                OutputStream os = newSocket.getOutputStream();
                if (client.size() <= 1) {
                    Cliente nclient = new Cliente(newSocket, os, is);
                    nclient.start();

                    for (int i = 0; i < client.size(); i++) {
                        client.get(i).enviaMsj("Se ha unido un usuario");
                    }

                    client.add(nclient);

                } else {
                   
                    //Printea esto cuando está el servidor completo
                    System.out.println("Server completo");
                    String msg="Server completo";
                     os.write(msg.getBytes());
                }

            }

        } catch (IOException ex) {
            System.out.println("Error en la conexion");
        }
    }
}

class Cliente extends Thread {

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public Cliente(Socket sckt, OutputStream os, InputStream is) throws IOException {
        this.socket = sckt;
        this.os = os;
        this.is = is;
        System.out.println("Conx. recibida");
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] msjRc = new byte[120];
                is.read(msjRc);
                System.out.println(new String(msjRc));
                //Splitea string para mostrar los textos
                String[] cdn = new String(msjRc).split(": ");
                //Descsonectar el usuario
                if (cdn[1].contains("/disconnect")) {
                    System.out.println("User desconectado");
                    ChatServer.client.remove(this);
                    System.out.println("Quedan users conectados: " + ChatServer.client.size());

                    for (int i = 0; i < client.size(); i++) {
                        client.get(i).enviaMsj("Se ha desconectado un usuario");
                    }
                    if (ChatServer.client.isEmpty()) {
                        System.out.println("Nadie en linea");
                    }
                    os.close();
                    is.close();
                    stop();
                }

                String msjenv = cdn[0] + ": " + cdn[1];
                //Contador cantidad
                for (int i = 0; i < client.size(); i++) {
                    client.get(i).enviaMsj(msjenv);
                }
                System.out.println(msjenv);
                System.out.println("Enviado");

            } catch (IOException ex) {
                //System.out.println("Error connx.");
            }
        }
    }

    public void enviaMsj(String msj) {
        try {
            os.write(msj.getBytes());
        } catch (IOException ex) {
            System.out.println("Error al enviar");
            try {
                os.close();
            } catch (IOException ex1) {
                System.out.println("Error: Envío desactivado");
            }
        }
    }
}

