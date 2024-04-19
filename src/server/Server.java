package server;

import uiRes.GenericFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server extends GenericFrame implements Runnable {

    JTextArea textArea;


    public Server() {

        Thread thread = new Thread(this);
        thread.start();

        setTitle("Server");

        add(new PanelitoServer());
        setVisible(true);
    }

    @Override
    public void run() {

        ArrayList<String> listaIpConectadas = new ArrayList<>();

        try {
            ServerSocket miServidor = new ServerSocket(9090); // Puerto abierto

            String nick, ip, mensaje;

            PackageDataSend datosRecibidos;

            for(;;) {

                // ACEPTAR TODAS LAS CONEXIONES
                Socket socket = miServidor.accept();

                // ENTRADA DE DATOS
                ObjectInputStream flujoEntrada = new ObjectInputStream(socket.getInputStream());

                datosRecibidos = (PackageDataSend) flujoEntrada.readObject();

                // IMPRIMIR DATOS RECIBIDOS DE LOS CLIENTES
                /* !! PROBLEMA !!
                 * A mí no me salta un error directamente porque no estoy pidiéndole a los gets
                 * los datos de forma directa, sino que solo llamo a .toString() que me devuelve
                 * todos los datos juntos. Entonces se crea un montón de texto
                 *
                 * Código afectado:
                 * >> textArea.append(datosRecibidos.toString() + "\n"); <<
                 *
                 * Solución aplicada:
                 * >> Se crea un condicional invertido, para que escriba los datos de forma normal
                 * en el caso de que el contenido del mensaje NO sea " online".
                 */

                if(!datosRecibidos.getMessage().equals(" online")) {

                    textArea.append(datosRecibidos.toString() + "\n");

                    // REENVÍO A DESTINATARIO
                    /* TODO!! Problema a solucionar!!
                     * Si al enviar el PackageDataSend el campo de Ip está vacío, el programa
                     * queda en bucle, llenando el log del dato de prueba.
                     *
                     * Posibles soluciones:
                     * > Obligar al cliente a rellenar con cualquier cosa el campo de IP.
                     */
                    Socket outputSocket = new Socket(datosRecibidos.getIp(), 9090);
                    ObjectOutputStream outputData = new ObjectOutputStream(outputSocket.getOutputStream());
                    outputData.writeObject(datosRecibidos);

                    outputSocket.close();

                    socket.close();

                } else {

                    // DETECCIÓN DE PRIMERA CONEXIÓN

                    InetAddress clientsIp = socket.getInetAddress();
                    String onlineClientIp = clientsIp.getHostAddress();

                    //System.out.println(onlineClientIp);

                    listaIpConectadas.add(onlineClientIp);

                    datosRecibidos.setConnectedClients(listaIpConectadas);

                    for(String ipL: listaIpConectadas){

                        System.out.println("ArrayList" + ipL);

                        Socket outputSocket = new Socket(ipL, 9090);
                        ObjectOutputStream outputData = new ObjectOutputStream(outputSocket.getOutputStream());

                        outputData.writeObject(datosRecibidos);
                        outputSocket.close();
                        socket.close();
                    }
                }

            }
        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);
        }


    }

    class PanelitoServer extends JPanel {

        PanelitoServer() {

            setLayout(new BorderLayout());

            textArea = new JTextArea();

            try {
                textArea.append(InetAddress.getLocalHost() + "\n");
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            add(textArea, BorderLayout.CENTER);
        }
    }


}



