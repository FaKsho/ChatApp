import client.Client;
import server.Server;
import uiRes.GenericFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppLauncher extends GenericFrame {

    public static void main(String[] args) { AppLauncher appLauncher = new AppLauncher(); }

    // FRAME CONFIG
    AppLauncher() {

        setTitle("Starter");
        setSize(470,280);
        setLocationRelativeTo(null);

        add(new LauncherPanel());

        setVisible(true);
    }

    // PANEL CONFIG
    class LauncherPanel extends JPanel implements ActionListener {

        JButton server, client;

        LauncherPanel() {

            server = new JButton("Server");
            server.addActionListener(this);

            client = new JButton("Client");
            client.addActionListener(this);

            add(server);
            add(client);
        }

        public void actionPerformed(ActionEvent e){

            dispose();

            if(e.getActionCommand().equals("Client")){

                // TODO provisional
                String serverIP = JOptionPane.showInputDialog("Ingrese la IP del servidor");
                int serverPort = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto del servidor"));

                new Client(serverIP, serverPort);
            } else {

                new Server();
            }
        }
    }
}
