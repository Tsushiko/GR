import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class interface_gráfica {
    private static char type;
    private static String OID;
    private static int temperature = 0;
    private static int idmensagem = 0; // Added to keep track of message IDs

    public static void main(String[] args) {
        JFrame frame = new JFrame("Painel de Controlo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));
        JPanel lampPanel = new JPanel();
        lampPanel.setLayout(new GridLayout(2, 3));
        JLabel lamp1Label = new JLabel("Lâmpada 1");
        JButton lamp1OnButton = new JButton("ON");
        JButton lamp1OffButton = new JButton("OFF");
        JLabel lamp2Label = new JLabel("Lâmpada 2");
        JButton lamp2OnButton = new JButton("ON");
        JButton lamp2OffButton = new JButton("OFF");

        lampPanel.add(lamp1Label);
        lampPanel.add(lamp1OnButton);
        lampPanel.add(lamp1OffButton);
        lampPanel.add(lamp2Label);
        lampPanel.add(lamp2OnButton);
        lampPanel.add(lamp2OffButton);

        // Painel do ar condicionado
        JPanel acPanel = new JPanel();
        acPanel.setLayout(new GridLayout(1, 4));
        JLabel acLabel = new JLabel("Ar Condicionado: ");
        JLabel tempLabel = new JLabel(temperature + "°C");

        JButton tempUpButton = new JButton("+");
        JButton tempDownButton = new JButton("-");

        acPanel.add(acLabel);
        acPanel.add(tempLabel);
        acPanel.add(tempUpButton);
        acPanel.add(tempDownButton);

        frame.add(lampPanel);
        frame.add(acPanel);

        // Ações dos botões
        lamp1OnButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp1OnButton, lamp1OffButton));
        lamp1OffButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp1OffButton, lamp1OnButton));
        lamp2OnButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp2OnButton, lamp2OffButton));
        lamp2OffButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp2OffButton, lamp2OnButton));

        tempUpButton.addActionListener(e -> handleTempButton("3.3.3.1", 'S', tempLabel, 1));
        tempDownButton.addActionListener(e -> handleTempButton("3.3.3.1", 'S', tempLabel, -1));

        frame.setVisible(true);
    }

    private static void handleLampButton(String oid, char type, JButton activeButton, JButton inactiveButton) {
        OID = oid;
        interface_gráfica.type = type;
        activeButton.setBackground(Color.GREEN);
        inactiveButton.setBackground(null);
        sendPackageToAgent();
    }

    private static void handleTempButton(String oid, char type, JLabel tempLabel, int change) {
        OID = oid;
        interface_gráfica.type = type;
        temperature += change;
        tempLabel.setText(temperature + "°C");
        sendPackageToAgent();
    }

    private static void sendPackageToAgent() {
        try {// verificar se o oidtem acess de escrita // quando recebe do agente filtra a informação ( erros, gets e afins)
            DatagramSocket socket = new DatagramSocket();
            Connection_manager udp = new Connection_manager(socket, 2, "localhost");
            Package_builder packet = new Package_builder(type, getIdmensagem(), OID, getCurrentDateTime());
            udp.send_package(packet);
            udp.receive_response(socket);
            //if(parse_infromações){
            //atua-se na aplicação
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy:HH:mm:ss:SSS");
        return now.format(formatter);
    }

    private static int getIdmensagem() {
        return idmensagem++;
    }
}

