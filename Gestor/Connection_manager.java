import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection_manager {
    static int porta;
    static String IP;
    static DatagramSocket socket;
    public Connection_manager(DatagramSocket lig,int s,String l){
        this.socket= lig;
        this.porta = s;
        this.IP = l;
    }
    public static void send_package(Package_builder pacote) throws IOException {
        // Create a DatagramSocket (para enviar)
        //DatagramSocket socket = new DatagramSocket();

        // The message to be sent (Builder para a package a ser enviada)
        String message = pacote.toString();
        //System.out.println(message);
        byte[] buffer = message.getBytes();

        // Get the receiver's IP address
        InetAddress receiverAddress = InetAddress.getByName(IP);

        // Create a DatagramPacket to send the message
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, porta);

        // Send the packet
        socket.send(packet);

        System.out.println("Message sent: " + message);
    }
    public static void receive_response(DatagramSocket responsesocket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Espera de uma resposta");
        // Receber um pacote pela mesma socket (Cuidado que ele fica preso nesta ação até vir uma )
        responsesocket.receive(packet);

        //Retirar o ip e a porta de onde veio o pacote
        InetAddress agenteAddress = packet.getAddress();
        int agentePort = packet.getPort();
        System.out.println("\nPorta do agente: " + agentePort);
        System.out.println("Ip do Agente: "+agenteAddress);

        // Extrair a mensagem do pacote
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("\nMensagem Recebida : "+message);
    }

}
