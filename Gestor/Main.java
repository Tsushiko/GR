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
            int port = 2;
            String ip = "localhost";
            int idmensagem=0;
            //Para usar a mesma socket para mensagems:
            //InetAddress receiverAddress = InetAddress.getByName(ip);
            //DatagramSocket socket = new DatagramSocket();
            //Connection_manager udp = new Connection_manager(socket,port,ip);
            Scanner myObj = new Scanner(System.in);
            int input = 2;
            System.out.println("\0".length());
            while(input != 0){
                input = myObj.nextInt();
                if (input ==1){
                    //Para usar uma socket DIFERENTE sempre q se manda uma mensagem
                    //InetAddress receiverAddress = InetAddress.getByName(ip);
                    DatagramSocket socket = new DatagramSocket();
                    Connection_manager udp = new Connection_manager(socket,port,ip);

                    //Construção da mensagem;
                    //LocalDate tempo = LocalDate.now();
                    Package_builder packet = new Package_builder(idmensagem);
                    idmensagem+=1;
                    udp.send_package(packet);

                    //Esperar de uma Resposta (secalhar futuramente meter timeout e pedir novamente?)
                    udp.receive_response(socket);
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


    }
}
