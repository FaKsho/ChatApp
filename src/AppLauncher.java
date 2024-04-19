import client.Client;
import server.Server;
import uiRes.GenericFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppLauncher extends GenericFrame {

    public static void main(String[] args) { AppLauncher appLauncher = new AppLauncher(); }

    AppLauncher() {

        setTitle("Starter");

        add(new StarterPanel());

        setVisible(true);
    }

    class StarterPanel extends JPanel {

        JButton server, client;

        StarterPanel() {

            server = new JButton("Server");
            server.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    dispose();

                    new Server();
                }
            });

            client = new JButton("Client");
            client.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    dispose();

                    // TODO provisional
                    String serverIP = JOptionPane.showInputDialog("Ingrese la IP del servidor");
                    int serverPort = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto del servidor"));

                    new Client(serverIP, serverPort);
                }
            });

            add(server);
            add(client);
        }
    }
}
