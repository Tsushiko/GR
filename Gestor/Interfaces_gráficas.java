import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Interfaces_gráficas {
    private char type;
    private String OID;
    private int temperature = 0;
    private int idmensagem = 0;
    private DatagramSocket socket;
    private Connection_manager connectionManager;
    private ArrayList<String> OID_WRT= new ArrayList<>(); //para guardar os valores dos oids que podem ser escritos na MIB

    public Interfaces_gráficas() {
        try {
            this.socket = new DatagramSocket();
            this.connectionManager = new Connection_manager(socket, 2, "localhost");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Painel de Controlo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(4, 1));

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

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 5, 10, 0)); // 10 pixels de espaço entre as caixas de texto

        JTextField oidField = new JTextField();
        String[] options = {"SET", "GET"};
        JComboBox<String> typeComboBox = new JComboBox<>(options);
        JTextField rangeField = new JTextField();
        JButton sendButton = new JButton("Enviar");
        JTextField valueField = new JTextField();
        valueField.setVisible(false); // Initially hidden

        inputPanel.add(new JLabel("OID:"));
        inputPanel.add(oidField);
        inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("Range (opcional):"));
        inputPanel.add(rangeField);
        inputPanel.add(new JLabel("Value:"));
        inputPanel.add(valueField);
        inputPanel.add(sendButton);

        typeComboBox.addActionListener(e -> {
            if (typeComboBox.getSelectedItem().equals("SET")) {
                valueField.setVisible(true);
            } else {
                valueField.setVisible(false);
            }
            inputPanel.revalidate();
            inputPanel.repaint();
        });

        frame.add(lampPanel);
        frame.add(acPanel);
        frame.add(inputPanel);

        lamp1OnButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp1OnButton, lamp1OffButton));
        lamp1OffButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp1OffButton, lamp1OnButton));
        lamp2OnButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp2OnButton, lamp2OffButton));
        lamp2OffButton.addActionListener(e -> handleLampButton("1.3.3.2", 'S', lamp2OffButton, lamp2OnButton));

        tempUpButton.addActionListener(e -> handleTempButton("3.3.3.1", 'S', tempLabel, 1));
        tempDownButton.addActionListener(e -> handleTempButton("3.3.3.1", 'S', tempLabel, -1));

        sendButton.addActionListener(e -> handleSendButton(oidField.getText(), (String) typeComboBox.getSelectedItem(), rangeField.getText(),valueField));

        frame.setVisible(true);
    }

    private void handleLampButton(String oid, char type, JButton activeButton, JButton inactiveButton) {
        OID = oid;
        this.type = type;
        activeButton.setBackground(Color.GREEN);
        inactiveButton.setBackground(null);
        sendPackageToAgent("");
    }

    private void handleTempButton(String oid, char type, JLabel tempLabel, int change) {
        OID = oid;
        this.type = type;
        temperature += change;
        tempLabel.setText(temperature + "°C");
        sendPackageToAgent("");
    }

    private void sendPackageToAgent(String value) {
        try {
            Package_builder packet = new Package_builder(type, getIdmensagem(), OID, getCurrentDateTime(),value);
            connectionManager.send_package(packet);
            connectionManager.receive_response(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy:HH:mm:ss:SSS");
        return now.format(formatter);
    }

    private int getIdmensagem() {
        return idmensagem++;
    }

    public static class Connection_manager {
        private int porta;
        private String IP;
        private DatagramSocket socket;

        public Connection_manager(DatagramSocket lig, int s, String l) {
            this.socket = lig;
            this.porta = s;
            this.IP = l;
        }

        public void send_package(Package_builder pacote) throws IOException {
            String message = pacote.toString();
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(IP);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, porta);
            socket.send(packet);
            System.out.println("Message sent: " + message);
        }

        public void receive_response(DatagramSocket responsesocket) throws IOException {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Espera de uma resposta");
            responsesocket.receive(packet);
            InetAddress agenteAddress = packet.getAddress();
            int agentePort = packet.getPort();
            System.out.println("\nPorta do agente: " + agentePort);
            System.out.println("Ip do Agente: " + agenteAddress);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("\nMensagem Recebida : " + message);
        }
    }
    private void handleSendButton(String oid, String type, String range, JTextField valueField) {
        OID = range.isEmpty() ? oid : "RANGE";
        this.type = type.equals("SET") ? 'S' : 'G';
        String value = type.equals("SET") ? valueField.getText() : "";
        if (!range.isEmpty()) {
            this.type = 'G';
        }
        sendPackageToAgent(value);
    }
    //private void handleSendButton(String oid, String type, String range, JTextField valueField) {
    //    if (range.isEmpty()) {
    //        OID = oid;
    //    } else {
    //        OID = "RANGE";
    //        this.type = 'G';
    //    }
    //
    //    if (type.equals("SET")) {
    //        this.type = 'S';
    //    } else {
    //        this.type = 'G';
    //    }
    //
    //    String value = type.equals("SET") ? valueField.getText() : "";
    //    sendPackageToAgent(value);
    //}
}