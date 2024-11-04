import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.net.Socket;

//Não têm de ser processos separados? Temos de criar uma thread para cada Cliente não?
public class Connection_manager implements Runnable{
    static DatagramSocket socket;
    static DatagramPacket packet;
    //int start;

    public Connection_manager(DatagramSocket s,DatagramPacket p){//,int t){
    this.socket = s;
    this.packet = p;
    //this.start=t;
    }

    public void run() {

        try {
            InetAddress clientAddress = this.packet.getAddress();
            int clientPort = this.packet.getPort();
            System.out.println("Porta do Cliente: " + clientPort);
            System.out.println("IP do Cliente: " + clientAddress);
            // Extrair a mensagem do pacote
            String message = new String(this.packet.getData(), 0, this.packet.getLength());
            //System.out.println("Mensagem Recebida: " + message);
            System.out.println("\nMensagem Recebida:");
            // Parsing da mensagem (provavelmente noutra class?, e nessa poderia ter tbm os métodos das cenas das mibs? gets e assim de temperatura???)
            String[] resposta = message.split("\0");

            for (String t : resposta) {
                //Testar concorrência bloqueando no segundo pedido de um Gestor
                //if (t.equals("2")){
                //    while (true){}
                //}
                System.out.print(t + " | ");
            }

            //Resposta para o Gestor que enviou o pacote (Acho que aqui uma classe para as respostas, "Response_builder")
            String responseMessage = message + "\nIp do Gestor:" + clientAddress + " Porta do Gestor :" + clientPort + "\n";
            byte[] responseBuffer = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);

            // Enviar a resposta
            socket.send(responsePacket);
            System.out.println("\nResposta enviada ao Cliente :" + responseMessage);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
    //public void turnoff_server(int off){
    //    this.start =0;
    //}
}

