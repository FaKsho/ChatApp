package client;

import server.PackageDataSend;
import uiRes.GenericFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends GenericFrame {

    private final String serverIp;
    private final int serverPort;

    public Client(String serverIp, int serverPort) {

        // Window Setup
        add(new ClientPanel());
        setTitle("Client");
        setVisible(true);

        //
        this.serverIp = serverIp;
        this.serverPort = serverPort;


        sentOnlineSignal();
    }

    void sentOnlineSignal() {

        try {
            // TODO
            //  Hacer que la dirección y puerto del servidor se tenga que especificar con anterioridad.
            Socket socket = new Socket(serverIp, serverPort);
            PackageDataSend data = new PackageDataSend();

            data.setMessage(" online");

            ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());

            flujoSalida.writeObject(data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    class ClientPanel extends JPanel implements Runnable {

        private JTextField message;
        private JComboBox ip;
        private JLabel nick;
        private JTextArea textArea;
        private JButton sendButton;
        private JLabel cliente;

        ClientPanel() {

            //setLayout(new BorderLayout());

            // ELEMENTOS TOP
            nick = new JLabel();
            nick.setText(JOptionPane.showInputDialog("Ingresa tu nick"));

            add(nick);

            cliente = new JLabel(" Online ->");
            add(cliente);

            ip = new JComboBox();

            add(ip);

            // MENSAJES
            textArea = new JTextArea(14,24);
            try {
                textArea.append("YOUR IP:" + InetAddress.getLocalHost() + "\n");
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            textArea.setEditable(false);
            add(textArea);

            // ENVÍO
            message = new JTextField(20);
            add(message);

            sendButton = new JButton("Enviar");

            // SALIDA DE DATOS
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if(!message.getText().equals(" online")) textArea.append("You >> " + message.getText() + "\n");


                    try {
                        // Creación de socket
                        Socket socket = new Socket(serverIp, serverPort);

                        PackageDataSend data = new PackageDataSend();

                        data.setNick(nick.getText());
                        data.setIp(ip.getSelectedItem().toString());
                        data.setMessage(message.getText());

                        ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());

                        flujoSalida.writeObject(data);

                        // TODO escribir en el chat el mensaje enviado por el propio cliente
                        textArea.append(message.getText());

                        socket.close();

                    } catch (IOException ex) {

                        throw new RuntimeException(ex);
                    } finally {


                        message.setText("");
                    }

                }
            });

            add(sendButton);

            Thread mihilo = new Thread(this);

            mihilo.start();
        }

        @Override
        public void run() {

            try {

                ServerSocket clientListener = new ServerSocket(serverPort);

                Socket cliente;

                PackageDataSend recievedData;

                for(;;){

                    cliente = clientListener.accept();

                    ObjectInputStream inputStream = new ObjectInputStream(cliente.getInputStream());

                    recievedData = (PackageDataSend) inputStream.readObject();

                    if(recievedData.getMessage().equals(" online")) {

                        // TODO Hacer que las direcciones ip recibidas filtren la propia ip del cliente.
                        //  Y además, muestre el nick de los demás clientes, en vez de solo la ip.
                        ArrayList<String> connectedClients = recievedData.getConnectedClients();

                        ip.removeAllItems();

                        for(String ips: connectedClients) {
                            ip.addItem(ips);
                        }

                    } else {

                        textArea.append("\n" + recievedData.getNick() + " << " + recievedData.getMessage());
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

