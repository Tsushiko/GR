import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.time.LocalDate;
public class Main {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(2);
            //int start = 0;
            //Connection_manager udp = new Connection_manager(socket);//,start);
            try {
                while(true) {

                    // Buffer para  mensagem recebida
                    byte[] buffer = new byte[1024];

                    // Packet para a receção da mensagem
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    System.out.println("Espera de mensagem...");

                    // Receber um pacote (Cuidado que ele fica preso nesta ação até vir um)
                    socket.receive(packet);
                    new Thread(new Connection_manager(socket,packet)).start();
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            //udp.start_server();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
