package ChatApp;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;

import javax.swing.*;
import java.awt.*;
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

public class Client extends GenericWindow {

    public Client() {

        add(new PanelitoCliente());
        setTitle("Client");
        setVisible(true);

        serverConectionStarter();

    }

    void serverConectionStarter() {

        try {
            Socket socket = new Socket("192.168.100.4", 9090);
            PackageDataSend data = new PackageDataSend();

            data.setMessage(" online");

            ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());

            flujoSalida.writeObject(data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

class PanelitoCliente extends JPanel implements Runnable {

    private JTextField message;
    private JComboBox ip;
    private JLabel nick;

    private JTextArea textArea;

    private JButton sendButton;

    private JLabel cliente;

    PanelitoCliente() {

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
                    Socket socket = new Socket("192.168.100.4", 9090);

                    PackageDataSend data = new PackageDataSend();

                    data.setNick(nick.getText());
                    data.setIp(ip.getSelectedItem().toString());
                    data.setMessage(message.getText());

                    ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());

                    flujoSalida.writeObject(data);

                    socket.close();

                    /* Creación del outPutStream
                    DataOutputStream flujoSalida = new DataOutputStream(socket.getOutputStream());
                    flujoSalida.writeUTF(textField.getText());
                    flujoSalida.close();
                     */



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
            ServerSocket clientListener = new ServerSocket(9090);

            Socket cliente;

            PackageDataSend recievedData;

            for(;;){

              cliente = clientListener.accept();

              ObjectInputStream inputStream = new ObjectInputStream(cliente.getInputStream());

              recievedData = (PackageDataSend) inputStream.readObject();

              if(recievedData.getMessage().equals(" online")) {
                  // textArea.append("\n" + recievedData.getConnectedClients());

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
