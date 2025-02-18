import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class interface_completa {
    private int temperature = 0;
    private int luminosidade =100;
    private DatagramSocket socket;
    private Connection_manager connectionManager;
    private JTextArea sentPacketsArea;
    private JTextArea receivedPacketsArea;
    private JTextArea errorsArea;
    private HashMap<String, String> MiniMIB;


    public interface_completa(HashMap<String, String> MiniMIB) {
        try {
            this.socket = new DatagramSocket();
            this.connectionManager = new Connection_manager(socket, 2, "localhost");
            this.MiniMIB = MiniMIB;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Painel de Controlo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new GridLayout(8, 1));


        JPanel lampPanel = new JPanel();
        lampPanel.setLayout(new GridLayout(1, 7));
        JLabel lamp1Label = new JLabel("Lâmpada ");
        JLabel lampnada2Label = new JLabel("Nível da luz:");
        JButton lamp0Button = new JButton("0");
        JButton lamp25Button = new JButton("25");
        JButton lamp50Button = new JButton("50");
        JButton lamp75Button = new JButton("75");
        JButton lamp1O0Button = new JButton("100");
        JButton Atualizar = new JButton("Atualizar");


        lampPanel.add(lamp1Label);
        lampPanel.add(lampnada2Label);
        lampPanel.add(lamp0Button);
        lampPanel.add(lamp25Button);
        lampPanel.add(lamp50Button);
        lampPanel.add(lamp75Button);
        lampPanel.add(lamp1O0Button);
        lampPanel.add(Atualizar);

        JPanel AmbientePanel = new JPanel();
        AmbientePanel.setLayout(new GridLayout(2, 2));
        JLabel AmbienteLumLabel = new JLabel("Luminosidade Ambiente:   "+ luminosidade +" Lumens.");
        JButton Luminosidade10Atualizar = new JButton("Atualizar");
        JLabel AmbienteTempLabel = new JLabel("Temperatura Ambiente:   "+ temperature +" ºC");
        JButton Temperatura10Atualizar = new JButton("Atualizar");

        AmbientePanel.add(AmbienteLumLabel);
        AmbientePanel.add(Luminosidade10Atualizar);
        AmbientePanel.add(AmbienteTempLabel);
        AmbientePanel.add(Temperatura10Atualizar);

        JPanel acPanel = new JPanel();
        acPanel.setLayout(new GridLayout(1, 6));
        JLabel acLabel = new JLabel("Ar Condicionado:  "+ temperature + "°C");
        JLabel espaco = new JLabel("");
        JButton AC_ON_OFF = new JButton("LIGAR AC");
        JButton mais = new JButton("+");
        JButton menos = new JButton("-");
        JButton Atualizar_AC = new JButton("Atualizar");

        acPanel.add(acLabel);
        acPanel.add(espaco);
        acPanel.add(AC_ON_OFF);
        acPanel.add(mais);
        acPanel.add(menos);
        acPanel.add(Atualizar_AC);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 5, 10, 0));

        JTextField oidField = new JTextField();
        String[] options = {"G", "S","TAG_ERRADA","TYPE_ERRADO"};
        JComboBox<String> typeComboBox = new JComboBox<>(options);
        JButton sendButton = new JButton("Enviar");
        JTextField valueField = new JTextField();
        valueField.setVisible(false);

        typeComboBox.addActionListener(e -> {
            if (typeComboBox.getSelectedItem().equals("S")) {
                valueField.setVisible(true);
            } else {
                valueField.setVisible(false);
            }
            inputPanel.revalidate();
            inputPanel.repaint();
        });

        inputPanel.add(new JLabel("OID(s):"));
        inputPanel.add(oidField);
        inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("Value(s)"));
        inputPanel.add(valueField);
        inputPanel.add(sendButton);

        // Text area to display sent packets
        sentPacketsArea = new JTextArea(5, 30);
        sentPacketsArea.setEditable(false);
        JScrollPane sentScrollPane = new JScrollPane(sentPacketsArea);

        JPanel sentPanel = new JPanel(new BorderLayout());
        sentPanel.add(new JLabel("Pacotes Enviados:"), BorderLayout.NORTH);
        sentPanel.add(sentScrollPane, BorderLayout.CENTER);

        // Text area to display received packets
        receivedPacketsArea = new JTextArea(5, 30);
        receivedPacketsArea.setEditable(false);
        JScrollPane receivedScrollPane = new JScrollPane(receivedPacketsArea);

        JPanel receivedPanel = new JPanel(new BorderLayout());
        receivedPanel.add(new JLabel("Pacotes Recebidos:"), BorderLayout.NORTH);
        receivedPanel.add(receivedScrollPane, BorderLayout.CENTER);

        // Text area to display errors
        errorsArea = new JTextArea(5, 30);
        errorsArea.setEditable(false);
        JScrollPane errorsScrollPane = new JScrollPane(errorsArea);

        JPanel errorsPanel = new JPanel(new BorderLayout());
        errorsPanel.add(new JLabel("Erros:"), BorderLayout.NORTH);
        errorsPanel.add(errorsScrollPane, BorderLayout.CENTER);

        JPanel error_list = new JPanel();
        error_list.setLayout(new GridLayout(3, 1));
        JLabel legenda_erros1 = new JLabel("||0|| Sem erros ||2|| Tag ||3|| Tipo da mensagem desconhecido ||4|| Acesso Errado ||5|| OID inválido (GET)");
        JLabel legenda_erros2 = new JLabel("||6|| Tipo de valor desconhecido ||7|| Tipo do valor errado ||8|| Número de IIDS é diferente do número de valores");
        JLabel legenda_erros3 = new JLabel("||9|| Set inválido ||10|| IID ou value vazio ||11|| Valor fora dos limites");

        error_list.add(legenda_erros1);
        error_list.add(legenda_erros2);
        error_list.add(legenda_erros3);

        frame.add(AmbientePanel);
        frame.add(lampPanel);
        frame.add(acPanel);
        frame.add(inputPanel);
        frame.add(sentPanel);
        frame.add(receivedPanel);
        frame.add(errorsPanel);
        frame.add(error_list);


        Luminosidade10Atualizar.addActionListener(e -> handleAtualizarLum("2.3.2", "G", AmbienteLumLabel));
        Temperatura10Atualizar.addActionListener(e -> handleAtualizarTemp("2.3.1", "G", AmbienteTempLabel));

        lamp0Button.addActionListener(e -> handleLampButton("3.3.2", "S", lamp0Button, lamp25Button,lamp50Button,lamp75Button,lamp1O0Button));
        lamp25Button.addActionListener(e -> handleLampButton("3.3.2", "S", lamp25Button, lamp0Button,lamp50Button,lamp75Button,lamp1O0Button));
        lamp50Button.addActionListener(e -> handleLampButton("3.3.2", "S", lamp50Button, lamp25Button,lamp0Button,lamp75Button,lamp1O0Button));
        lamp75Button.addActionListener(e -> handleLampButton("3.3.2", "S", lamp75Button, lamp25Button,lamp50Button,lamp0Button,lamp1O0Button));
        lamp1O0Button.addActionListener(e -> handleLampButton("3.3.2", "S", lamp1O0Button, lamp25Button,lamp50Button,lamp75Button,lamp0Button));
        Atualizar.addActionListener(e -> atualizar_lampada("3.3.2", "G", lamp0Button, lamp25Button,lamp50Button,lamp75Button,lamp1O0Button));

        AC_ON_OFF.addActionListener(e -> ligar_desligar_ac("3.3.1", "S", AC_ON_OFF,acLabel));
        mais.addActionListener(e -> mais("3.3.1", "S", mais,AC_ON_OFF,acLabel));
        menos.addActionListener(e -> menos("3.3.1", "S", menos,AC_ON_OFF,acLabel));
        Atualizar_AC.addActionListener((e->atualizar_ac("3.3.1","G",Atualizar_AC,AC_ON_OFF,acLabel)));
        //tempDownButton.addActionListener(e -> handleTempButton("3.3.3.1", "S", tempLabel, -1));

        sendButton.addActionListener(e -> handleSendButton(oidField.getText(), (String) typeComboBox.getSelectedItem(),valueField.getText()));

        frame.setVisible(true);
    }
    private void handleAtualizarTemp(String oid, String type, JLabel AmbienteTempLabel) {
        String temp = sendPackageToAgent(type, oid,"");
        if(temp.equals("erro")){
            String erro = "Não foi possível recolher a temperatura ambiente com sucesso, tente outra vez";
            errorsArea.append(String.join(erro ) + "\n");
        }else{
            AmbienteTempLabel.setText("Temperatura Ambiente: "+temp+"ºC");
        }
    }
    private void handleAtualizarLum(String oid, String type, JLabel AmbienteTempLabel) {
        String lum = sendPackageToAgent(type, oid,"");
        if(lum.equals("erro")){
            String erro = "Não foi possível recolher a luminosidade ambiente com sucesso, tente outra vez";
            errorsArea.append(String.join(erro) + "\n");
        }else{
            AmbienteTempLabel.setText("Luminosidade Ambiente: "+lum+"ºC");
        }
    }
    private void atualizar_lampada(String oid, String type, JButton button0,JButton button25 ,JButton button50,JButton button75,JButton button100) {
        String lum = sendPackageToAgent(type, oid,"");
        if(lum.equals("erro")){
            String erro = "Não foi possível recolher a luminosidade ambiente com sucesso, tente outra vez";
            errorsArea.append(String.join(erro) + "\n");
        }else{
            if (lum.equals("0")) {
                button0.setBackground(Color.RED);
                button25.setBackground(Color.gray);
                button50.setBackground(Color.gray);
                button75.setBackground(Color.gray);
                button100.setBackground(Color.gray);

            }else if (lum.equals("25")) {

                button0.setBackground(Color.gray);
                button25.setBackground(Color.GREEN);
                button50.setBackground(Color.gray);
                button75.setBackground(Color.gray);
                button100.setBackground(Color.gray);

            }else if (lum.equals("50")) {

                button0.setBackground(Color.gray);
                button25.setBackground(Color.gray);
                button50.setBackground(Color.GREEN);
                button75.setBackground(Color.gray);
                button100.setBackground(Color.gray);

            }else if (lum.equals("75")) {

                button0.setBackground(Color.gray);
                button25.setBackground(Color.gray);
                button50.setBackground(Color.gray);
                button75.setBackground(Color.GREEN);
                button100.setBackground(Color.gray);

            }else if (lum.equals("100")) {

                button0.setBackground(Color.gray);
                button25.setBackground(Color.gray);
                button50.setBackground(Color.gray);
                button75.setBackground(Color.gray);
                button100.setBackground(Color.GREEN);

            }else{

                button0.setBackground(Color.GREEN);
                button25.setBackground(Color.gray);
                button50.setBackground(Color.gray);
                button75.setBackground(Color.gray);
                button100.setBackground(Color.gray);
            }
        }
    }

    private void handleLampButton(String oid, String type, JButton activeButton, JButton inactiveButton1,JButton inactiveButton2,JButton inactiveButton3,JButton inactiveButton4) {
        String value="";
        if (activeButton.getText().equals("0")) {
            value = "0";
        }else if (activeButton.getText().equals("25")) {
            value = "25";
        }else if (activeButton.getText().equals("50")) {
            value = "50";
        }else if (activeButton.getText().equals("75")) {
            value = "75";
        }else if (activeButton.getText().equals("100")) {
            value = "100";
        }else{
            value = "0";
        }
        String funcionou = sendPackageToAgent(type, oid, value);
        if(funcionou.equals("0")){
            if(activeButton.getText().equals("0")){
                activeButton.setBackground(Color.RED);
                inactiveButton1.setBackground(Color.gray);
                inactiveButton2.setBackground(Color.gray);
                inactiveButton3.setBackground(Color.gray);
                inactiveButton4.setBackground(Color.gray);
            }else {
                activeButton.setBackground(Color.GREEN);
                inactiveButton1.setBackground(Color.gray);
                inactiveButton2.setBackground(Color.gray);
                inactiveButton3.setBackground(Color.gray);
                inactiveButton4.setBackground(Color.gray);
            }
        }else{
            String erro = "Não foi possível alterar a luminosidade do ambiente com sucesso, tente outra vez";
            errorsArea.append(String.join(erro) + "\n");
        }
    }

    private void ligar_desligar_ac(String oid, String type, JButton tempLabel,JLabel ac_label) {
        if(tempLabel.getText().equals("LIGAR AC")){
            String funciona = sendPackageToAgent(type, oid, "15");
            if(funciona.equals("0")){
                tempLabel.setText("DESLIGAR AC");
                tempLabel.setBackground(Color.GREEN);
                ac_label.setText("Ar Condicionado:  "+  "15 " + "°C");

            }
        }else if(tempLabel.getText().equals("DESLIGAR AC")){
            String funciona = sendPackageToAgent(type, oid, "0");
            if(funciona.equals("0")){
                tempLabel.setText("LIGAR AC");
                tempLabel.setBackground(Color.orange);
                ac_label.setText("Ar Condicionado:  "+  "0 " + "°C");
            }
        }else {
            String funciona = sendPackageToAgent(type, oid, "0");
            if(funciona.equals("0")) {
                tempLabel.setText("LIGAR AC");
            }else{
                String erro = "Não foi possível desligar/ligar o AC, tente outra vez";
                errorsArea.append(String.join(erro) + "\n");
            }
        }
    }
    private void mais(String oid, String type, JButton tempLabel,JButton on_of,JLabel ac_label) {
        if (on_of.getText().equals("DESLIGAR AC")) {
            String valor = sendPackageToAgent("G", oid, "");
            System.out.print("valorrr:" + Integer.parseInt(valor) + " " + Integer.parseInt(valor) + 1);
            String funciona = sendPackageToAgent(type, oid, String.valueOf(Integer.parseInt(valor) + 1));
            if (funciona.equals("0"))
                ac_label.setText("Ar Condicionado:" + String.valueOf(Integer.parseInt(valor) + 1) + "°C");
        } else {
            String erro = "O AC encontra-se desligado.";
            errorsArea.append(String.join(erro) + "\n");
        }
    }

    private void menos(String oid, String type, JButton tempLabel,JButton on_of,JLabel ac_label) {
        if (on_of.getText().equals("DESLIGAR AC")) {
            String valor = sendPackageToAgent("G", oid, "");
            String funciona = sendPackageToAgent(type, oid, String.valueOf(Integer.parseInt(valor) - 1));
            if (funciona.equals("0"))
                ac_label.setText("Ar Condicionado: " + String.valueOf(Integer.parseInt(valor) - 1) + "°C");
        } else {
            String erro = "O AC encontra-se desligado.";
            errorsArea.append(String.join(erro) + "\n");
        }
    }
    private void atualizar_ac(String oid, String type, JButton tempLabel,JButton on_of,JLabel ac_label) {
            String funciona = sendPackageToAgent(type, oid, "");
            if (Integer.parseInt(funciona) == 0) {
                ac_label.setText("Ar Condicionado: " + funciona + "°C");
                on_of.setText("LIGAR AC");
                on_of.setBackground(Color.orange);
            } else {
                ac_label.setText("Ar Condicionado: " + funciona + "°C");
                on_of.setText("DESLIGAR AC");
                on_of.setBackground(Color.green);
        }
    }



    private void handleSendButton(String oid, String type,String value) {
        sendPackageToAgent(type, oid, value);
    }

    public HashMap<String, String> getMiniMIB() {
        return MiniMIB;
    }

    private String sendPackageToAgent(String type, String oids, String values) {
        String valor_final="erro";
        try {
            Instant now = Instant.now();
            ZonedDateTime dateTime = now.atZone(ZoneId.systemDefault());
            String ano = String.valueOf(dateTime.getYear());
            String mes = String.valueOf(dateTime.getMonthValue()); // Obtém o mês como número (1-12)
            String dia = String.valueOf(dateTime.getDayOfMonth());
            String horas = String.valueOf(dateTime.getHour());
            String minutos = String.valueOf(dateTime.getMinute());
            String segundos = String.valueOf(dateTime.getSecond());
            String milissegundos = String.valueOf(dateTime.getNano() / 1_000_000);
            ; // Convertendo nanos para milissegundos
            String soma = ano + mes + dia + horas + minutos + segundos + milissegundos;

            String ID = String.valueOf(soma);
            System.out.println("oids" + oids + "values." + values);
            String packetContent = "Tipo: " + type + " ID: " + ID + ", OIDs: " + oids + ", Valores: " + values;
            sentPacketsArea.append(packetContent + "\n");
            System.out.println("type" + type);
            Package_builder packet = new Package_builder(type, ID, oids, values, this.getMiniMIB());
            // Simulate sending and receiving
            connectionManager.send_package(packet);
            String response = connectionManager.receive_response(socket);

            //vamos averiguar os erros e escrever todos os erros que surgiram para cada oid (se existirem claro)
            String[] posicao_na_trama = response.split("\0");
            String n_elements_oids = posicao_na_trama[4];
            String n_elements_values = "";
            String n_elements_erros = "";
            int posicao_numero_elementes_errros = 0;
            if (type.equals("S")) {
                int posicao_n_values=4+(Integer.parseInt(n_elements_oids)) * 3 + 1;
                System.out.println("Posicao n_elment_value: "+posicao_n_values +" = "+4+"+"+(Integer.parseInt(n_elements_oids))+"*"+ 3 +"+" + 1 );
                n_elements_values = posicao_na_trama[posicao_n_values];
                System.out.println("valor do número de values: "+ n_elements_values);
                posicao_numero_elementes_errros = posicao_n_values+2;
                System.out.println("posição do número de erros: "+ posicao_numero_elementes_errros);
                n_elements_erros = posicao_na_trama[posicao_n_values+2];
                System.out.println("valor do número de erros: "+ n_elements_erros);
            } else {
                int posicao_n_values=4+(Integer.parseInt(n_elements_oids)) * 3 + 1;
                System.out.println("Posicao n_elment_value: "+posicao_n_values +" = "+4+"+"+(Integer.parseInt(n_elements_oids))+"*"+ 3 +"+" + 1 );
                n_elements_values = posicao_na_trama[posicao_n_values];
                System.out.println("valor do número de values: "+ n_elements_values);
                posicao_numero_elementes_errros = posicao_n_values+ Integer.parseInt(n_elements_values)*3 +1;
                System.out.println("posição do número de erros: "+ posicao_numero_elementes_errros);
                n_elements_erros = posicao_na_trama[posicao_numero_elementes_errros];
                System.out.println("valor do número de erros: "+ n_elements_erros);
            }
            String erro = "";
            int tamanho_da_lista_erros = (posicao_na_trama.length -  posicao_numero_elementes_errros);
            System.out.println("tamanho da resposta: "+posicao_na_trama.length +" - "+ posicao_numero_elementes_errros+" Tamanho da lista de erros =" +tamanho_da_lista_erros);
            String[] erros = new String[tamanho_da_lista_erros];

            int contador =0;
            if(posicao_na_trama[posicao_numero_elementes_errros].equals("")){
                contador = 1;
            }
            for(int i=posicao_numero_elementes_errros; i+1<posicao_na_trama.length;i++) {
                    erros[contador] = posicao_na_trama[i + 1];
                    erro = erro + " " + erros[contador];
                    contador++;

            }
            String[] numero_erros = erro.split(" ");
            String todos_erros = "O pacote com ID: " + ID + " teve os seguintes " +posicao_na_trama[posicao_numero_elementes_errros]+ " erros:" + erro;
            errorsArea.append(String.join(", ",todos_erros ) + "\n");

            //estudamos os casos da interface;
            System.out.println(posicao_na_trama[posicao_na_trama.length-1]);
            String resposta = String.join(" ", posicao_na_trama);
            receivedPacketsArea.append(resposta+"\n");

            if(posicao_na_trama[7].equals("2.3.1") && posicao_na_trama.length>11) {
                valor_final = posicao_na_trama[11];
            } else if (posicao_na_trama[7].equals("2.3.2")){
                valor_final = posicao_na_trama[11];
            }else if (posicao_na_trama[7].equals("3.3.2") && posicao_na_trama[8].equals("0")){
                valor_final = posicao_na_trama[posicao_na_trama.length-1];
            } else if (posicao_na_trama[7].equals("3.3.2") && posicao_na_trama[8].equals("1")){
                valor_final = posicao_na_trama[11];
            }else if(posicao_na_trama[7].equals("3.3.1") && posicao_na_trama[8].equals("1")){
                valor_final = posicao_na_trama[11];
            }else if (posicao_na_trama[7].equals("3.3.1") && posicao_na_trama[8].equals("0")){
                valor_final = posicao_na_trama[posicao_na_trama.length-1];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return valor_final;
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
            String message = pacote.SNMP_Protocol(pacote.getType(),pacote.getIIDList(), pacote.getValueList());
            System.out.println("Pacote" + message);
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(IP);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, porta);
            socket.send(packet);
        }

        public String receive_response(DatagramSocket responsesocket) throws IOException {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            responsesocket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        }
    }

}
