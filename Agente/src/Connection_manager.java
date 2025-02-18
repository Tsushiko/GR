import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.Arrays;

public class Connection_manager implements Runnable{
    static DatagramSocket socket;
    static DatagramPacket packet;
    static MIB mib;

    //int start;

    public Connection_manager(DatagramSocket s,DatagramPacket p, MIB mib1){//,int t){
    this.socket = s;
    this.packet = p;
    this.mib = mib1;
    //this.start=t;
    }
    public Tuple<String, Integer> getMibValue(String oid) {
        // Grupo Objeto Instância
        // type do Device 1.2.0
        // 1 -> Device , 2 -> type, 0 -> Instance (0 é o first device , 1 é o segundo, etc)
        String valor = "";
        int nvalors = 0;

        String[] ids = oid.split("\\.");
        // if para a length se for 4
        //                               //Se structure< 1          //Se structure > 3                  //Se Object> nObjects                                                                  //Se Object < 0
        if (ids.length>4 || ids.length<2 || Integer.parseInt(ids[0])<1 || Integer.parseInt(ids[0])>3 || Integer.parseInt(this.mib.getMib().get(ids[0]).getnObjs())<Integer.parseInt(ids[1]) || Integer.parseInt(ids[1])<0){
            valor = "oidinvalido";
            //  Caso Object é 0
        }else if(ids[1].equals("0")){
            valor+="I"+"\0"+"1"+"\0"+this.mib.getMib().get(ids[0]).getnObjs()+"\0";
            nvalors+=1;
            // Caso não tiver indice e object superior a 0
        }else if(ids.length==2){
            valor+=this.mib.getMib().get(ids[0]).getValue("1",ids[1])+"\0";
            nvalors+=1;
            // Caso tiver o primeiro indice e este for 0
        }else if(ids.length==3 && ids[2].equals("0")){
            valor+="I"+"\0"+"1"+"\0"+this.mib.getMib().get(ids[0]).getInstances()+"\0";
            nvalors+=1;
            // Caso tiver o primeiro indice e este for <N e diferente de 0
        }else if(ids.length==3 && Integer.parseInt(ids[2])<=Integer.parseInt(this.mib.getMib().get(ids[0]).getInstances())){
            valor+=this.mib.getMib().get(ids[0]).getValue(ids[2],ids[1])+"\0";
            nvalors+=1;
            //Caso tiver dois indices e forem ambos iguais a 0
        }else if(ids.length==4 && ids[2].equals("0") && ids[3].equals("0")){
            for(int i =1;i<=Integer.parseInt(this.mib.getMib().get(ids[0]).getInstances());i++){
                valor+=this.mib.getMib().get(ids[0]).getValue(String.valueOf(i),ids[1])+"\0";
                nvalors+=1;
            }
            //Caso tiver dois indices, o primeiro for menor ou igual que o segundo e ambos menores que N (se forem iguais ele só faz uma vez)
        }else if(ids.length==4 &&Integer.parseInt(ids[3])<=Integer.parseInt(this.mib.getMib().get(ids[0]).getInstances())&&Integer.parseInt(ids[2])<=Integer.parseInt(this.mib.getMib().get(ids[0]).getInstances()) &&Integer.parseInt(ids[3])>=Integer.parseInt(ids[2]) && !ids[2].equals("0") && !ids[3].equals("0")){
            for(int i =Integer.parseInt(ids[2]);i<=Integer.parseInt(ids[3]);i++){
                System.out.println(valor);
                valor+=this.mib.getMib().get(ids[0]).getValue(String.valueOf(i),ids[1])+"\0";
                nvalors+=1;
            }
            // Se tiver 2 indices, se o primeiro for maior q o segundo, se apenas um deles for 0 ou se algum for maior que Ninstâncias
        }else{
            valor = "oidinvalido";
        }
        //System.out.println(valor);
        return new Tuple<>(valor,nvalors);
    }

    public String setMibValue(String oid,String type,String value){
        //Erro ao escrever algo q n pode
        // Grupo Objeto Instância
        // type do Device 1.2.0
        String valor="";
        String[] ids = oid.split("\\.");
        //                                                                                                                                      //Se structure< 1             //Se structure > 3            //Se Object> nObjects                                                                  //Se Object < 0
        if(ids.length==3 && !ids[2].equals("0")&& Integer.parseInt(ids[2])<=Integer.parseInt(this.mib.getMib().get(ids[0]).getInstances()) && !(Integer.parseInt(ids[0])<1 || Integer.parseInt(ids[0])>3 || Integer.parseInt(this.mib.getMib().get(ids[0]).getnObjs())<Integer.parseInt(ids[1]) || Integer.parseInt(ids[1])<0)){
            valor+=this.mib.getMib().get(ids[0]).setValue(ids[2], ids[1],type,value);
        }else{
            // Erro de oid inválido para sets.
            valor+="9";
        }
        return valor;
    }

    public Tuple<String, Integer> Checkerror(String[] erro){
        String errorList = "";
        int nerros=0;
        String[] Tipo = {"G","S"};

        //Erro na Tag
        if (!erro[0].equals("kdk847ufh84jg87g")){
            //return "2";
            errorList +="2"+"\0";
            nerros+=1;
        }
        //Tipo Desconhecido
        if(!Arrays.asList(Tipo).contains(erro[1])){
            //return "3";
            errorList +="3"+"\0";
            nerros+=1;
        }
        // Caso n tenha oid
        System.out.println(erro.length);
        if(erro.length<=7){
            errorList+="10"+"\0";
            nerros+=1;
            new Tuple<>(errorList,nerros);
        }
        // Caso tenha oid e n tenha values
        //System.out.println(erro[4+Integer.parseInt(erro[4])*3+1]);
        //System.out.println(4+Integer.parseInt(erro[4])*3+1+3);
        if(erro[1].equals("S") && erro[4+Integer.parseInt(erro[4])*3+1].equals("1") && 4+Integer.parseInt(erro[4])*3+1+3>=erro.length){
            errorList+="10"+"\0"+"3"+"\0";
            nerros+=1;
            new Tuple<>(errorList,nerros);
        }

        // Lista de valores n corresponde á lista de IID (ver tamanhos) (8)
        //                                                                                     Se ja tive 3 quer dizer q existe oid e n há values
        if(erro[1].equals("S") && !erro[4+(Integer.parseInt(erro[4])*3)+1].equals(erro[4]) && !errorList.contains("3")) {
            errorList +="8"+"\0";
            nerros+=1;
        }

        // Apenas não temos os das mensagems duplicadas e dos descodificação
        System.out.println(errorList);
        return new Tuple<>(errorList,nerros);
    }
    public String Parse (String[] mensagem){
        String valor ="";
        String error ="";
        Tuple<String, Integer> res;
        Tuple<String, Integer> erros;
        String[] Tipo = {"I","S","T"};
        int nvalues=0;

        //Tratamento de erros
        erros = Checkerror(mensagem);
        error = erros.first;
        int nerros= erros.second;
        if(error.contains("10")){
            return "0"+"\0"+"\0"+nerros+"\0"+error;
        }
        //mensagem[5] N oids
        //mensagem[6-N] oids
        //Se ocorrer algum outro erro, os  0's que aparecem na error list equivalem aos oids que estão válidos e que respeitam o get, mas não é devolvido qualquer valor.
        if (mensagem[1].equals("G")) {
            // +3 para Saltar de oid em oid
            for (int i = 0; i < Integer.parseInt(mensagem[4])*3; i+=3) {
                //meter tbm a devolver o type
                System.out.println(mensagem[7+i]);
                res=getMibValue(mensagem[7+i]);
                //Se esse oid for inválido
                if (res.first.equals("oidinvalido")) {
                    error += "5" + "\0";
                    nerros += 1;
                }else{
                    error += "0" + "\0";
                    nerros += 1;
                }
                valor+=res.first;
                nvalues+=res.second;

            }
        }
        // Se noids =/= nvalues , apenas os oids que um value correspondente levam set, os restantes nada acontece.
        // Os 0's que aparecem na error list equivale aos oids que levaram set (se houver erro previamente, se não houver aparece apenas 0 na error list)
        if (mensagem[1].equals("S")) {
                              // Numero de Oids                   // Para caso o noids>nvalues
            for (int i = 0; i < Integer.parseInt(mensagem[4])*3; i+=3) {
                //Caso tenham oids sem um valor, ele acrescenta o erro 9 para cada.
                if(7+i+(Integer.parseInt(mensagem[4])*3)+1-2<mensagem.length) {
                    System.out.println(7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1 - 2 < mensagem.length);
                    //7+i = posdooid + Noids*3 (Para saltar sobre os oids) + 1 (Por causa do nvalues)
                    // Exemplo 2 oids:  Valor do primeiro oid = 7+0+(2*3)+1 =11  Valor do segundo oid = 7+3+(2*3)+1 = 16
                    System.out.println("Oid: " + mensagem[7 + i] + " Type: " + mensagem[7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1 - 2] + " Valor :" + mensagem[7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1]);

                    // Tipo de Valor desconhecido (6)
                    // Se o Tipo do valor for I , S , T
                    if(Arrays.asList(Tipo).contains(mensagem[7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1 - 2])) {
                        //oid          //type
                        error += setMibValue(mensagem[7 + i], mensagem[7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1 - 2], mensagem[7 + i + (Integer.parseInt(mensagem[4]) * 3) + 1]);
                        if (!error.equals("")) {
                            error += "\0";
                            nerros += 1;
                        }
                    }else{
                        error+="6"+"\0";
                        nerros+=1;
                    }
                }else{
                    error += "9"+"\0";
                    nerros+=1;
                }

            }
        }
        System.out.println(valor);
        System.out.println(error);
        //Se houver erros ele diz os erros, se houver varios ids errados ele repete o erro 5 , ou seja o nº de 5's
        //Corresponde ao número de oids errados

        // Se der set com sucesso
        if(valor.equals("") && !error.contains("9") && error.contains("0") && !error.matches(".*[1-9].*")){
            //     Len    Values
            return "0"+"\0"+"\0"+"0"+"\0";
        }
        // Se der get com sucesso
        if(error.contains("0") && !error.contains("5") && !error.matches(".*[1-9].*")){
            return nvalues +"\0"+valor+"0"+"\0";
        }

        // Se tiver erros para além dos gets e sets
        return "0"+"\0"+"\0"+nerros+"\0"+error;
    }

    public DatagramPacket Deparse(String[]message,String ValueError,InetAddress clientAddress, int clientPort){
        //tag,type,timestamp,identifier,IDD,lenidds,idds,Values,error
        String resposta ="";
        resposta += message[0]+"\0"; //tag
        resposta += "R"+"\0"; //Type
        Instant now = Instant.now();
        System.out.println("now"+ now);
        String now_string= now.toString();
        System.out.println(now_string);
        String[] tempo = now_string.split("T");
        System.out.println(tempo[0]+tempo[1]);
        String[] data = tempo[0].split("-");
        String[] time = tempo[1].split("Z");
        String[] time_final = time[0].split("\\.");
        String timestamp= String.join(":",data[2]+":"+time_final[0]+":"+time_final[1]);
        System.out.println("timestampt"+timestamp);
        resposta +=timestamp+"\0"; //timestamp
        resposta += message[3]+"\0"; //identifier
        // Se tiver o erro de falta de oids ou de falta de values, não escreve o campo de oids
        if(!ValueError.matches("0" + '\0' + '\0' + "\\d" + '\0' + "10" + '\0')) {
            resposta += message[4] + "\0"; // lenidds
            resposta += "D" + "\0"; // Type IDDS
            // *3 porque cada IDD tem 3 campos ,-1 porque o ja metemos o D
            for (int i = 0; i < Integer.parseInt(message[4]) * 3 - 1; i++) {
                //System.out.println(message[6+i]);
                resposta += message[6 + i] + "\0";
            }//IDDS
        }//else{
        //    //        len     oids
        //    resposta+="0"+"\0"+"\0";
        //}
        resposta+= ValueError;
        byte[] responseBuffer = resposta.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
        System.out.println("\nResposta enviada ao Cliente :" + resposta);
        return responsePacket;
    }

    public void run() {
        try {
            InetAddress clientAddress = this.packet.getAddress();
            int clientPort = this.packet.getPort();
            //System.out.println("Porta do Cliente: " + clientPort);
            //System.out.println("IP do Cliente: " + clientAddress);
            // Extrair a mensagem do pacote
            String message = new String(this.packet.getData(), 0, this.packet.getLength());
            System.out.println("\nResposta recebida:" +  message);

            //Erro de n meter nenhum oid ou nenhum valor , podia ser aqui
            //Parse
            String[] messagear = message.split("\0");
            String ValueeError=Parse(messagear);

            //Deparse
            DatagramPacket responsePacket = Deparse(messagear,ValueeError,clientAddress,clientPort);

            // Enviar a resposta
            socket.send(responsePacket);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
    //public void turnoff_server(int off){
    //    this.start =0;
    //}
}

