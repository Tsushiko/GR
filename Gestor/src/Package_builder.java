import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Package_builder {
    //Classe para a costrução da trama, não sei se tem de ser feita em separado ou se isto é sequer util?
    //Formato da Trama.
    // Tag | Type | Time-Stamp | Message-Identifier | IID-List | Value-List | Error-List
    private String Tag;
    private String Type;
    //private LocalDate TimeStamp;
    private String TimeStamp;
    private String MessageIdentifier;
    private String IIDList;
    private String ValueList;
    private String ErrorList;
    private HashMap<String, String> MiniMIB;

    //No enunciado diz q o gestor tem de mandar uma lista vazia, nada i guess
    // Existe o  Formatter para o tempo
    //LocalDate localDate = LocalDate.now(); // Para termos o tempo de agora
    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("day:month:year:hours:mins:secs:ms");
    //String formattedString = localDate.format(formatter);

    // Em principio o construtor vai receber variáveis da main, variáveis q vêem do gestor colocar como input,
    // Mas ainda percebo bem a trama por isso vai ficar assim com uma predefinida, para testar apenas.
    public String getTag() {
        return Tag;
    }

    public String getType() {
        return Type;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public String getMessageIdentifier() {
        return MessageIdentifier;
    }

    public String getIIDList() {
        return IIDList;
    }

    public String getValueList() {
        return ValueList;
    }

    public String getErrorList() {
        return ErrorList;
    }


    public Package_builder(String type,String IID, String oid, String values, HashMap<String,String> MiniMIB){//String ta, char ty, String time,String m, String iid, String value, String error){

        // Acho q dá para restringir uma variável para so ter cenas especificas, tipo Type só puder ser G,S,N ou R
        this.Tag = "kdk847ufh84jg87g";
        this.Type = type; //'G';
        //Stor na teorica disse q podia ser random ou apenas irmos incrementar com cada mensagem
        //Acho q incrementar a cada mensagem ajuda
        this.TimeStamp = String.valueOf(DateTimeFormatter.ofPattern("dd:MM:yyyy:HH:mm:ss:SSS"));
        this.MessageIdentifier = IID;
        this.IIDList=oid;
        this.ValueList= values;
        this.ErrorList= "";
        this.MiniMIB = MiniMIB;
    }

    public String SNMP_Protocol(String type, String oid, String values) {
        String message_identifier = this.getMessageIdentifier();
        System.out.println("Message Identifier: " + message_identifier);
        String message = "";
        String message_oids = "";
        String message_values = "";
        String type_values = "";

        String[] oids_separados = oid.split(",");
        String n_elements_oids = String.valueOf(oids_separados.length);

        String[] values_separados = values.split(",");
        String n_elements_values = String.valueOf(values_separados.length);

        String oid_aux_list = "";

        if (type.equals("G") || type.equals("TAG_ERRADA") ||type.equals("TYPE_ERRADO")) {
            for (int aux = 0; aux < oids_separados.length; aux++) {
                String[] oid_aux = oids_separados[aux].split("\\.");
                String oid_aux_tamanho = String.valueOf(oid_aux.length);
                String length_oid_aux = "";
                oid_aux_list = oid_aux_list + "D" + "\0" + oid_aux_tamanho + "\0" + oids_separados[aux] + "\0";
            }
            if (type.equals("TAG_ERRADA")) {
                message = "tag_errada" + "\0" + type + "\0" + Instant.now().toString() + "\0" + this.getMessageIdentifier() + "\0" + n_elements_oids + "\0" + oid_aux_list  + "" + "\0" + "" + "\0";
            } else if (type.equals("TYPE_ERRADO")) {
                message = this.getTag() + "\0" + "S" + "\0" + Instant.now().toString() + "\0" + this.getMessageIdentifier() + "\0" + n_elements_oids + "\0" + oid_aux_list + "1" + "\0" + "TYPE_ERRADO" + "\0" + "3" + "\0" + "8" + "\0" + "" + "\0";
            }else{
                message = this.getTag() + "\0" + type + "\0" + Instant.now().toString() + "\0" + this.getMessageIdentifier() + "\0" + n_elements_oids + "\0" + oid_aux_list  + "" + "\0" + "" + "\0";
            }
        } else {
            String oid_value_list = "";
            for (int aux2 = 0; aux2 < oids_separados.length; aux2++) {
                String[] oid_aux2 = oids_separados[aux2].split("\\.");
                String oid_aux_tamanho = String.valueOf(oid_aux2.length);
                oid_aux_list = oid_aux_list + "D" + "\0" + oid_aux_tamanho + "\0" + oids_separados[aux2] + "\0";
            }
            for (int aux3 = 0; aux3 < values_separados.length; aux3++) {
                if(aux3 >= oids_separados.length){
                    oid_value_list = oid_value_list + "" + "\0" + "" + "\0" + values_separados[aux3] + "\0";
                }else{
                String[] oid_aux3 = oids_separados[aux3].split("\\.");
                String value_aux_type = MiniMIB.get(oid_aux3[0] + "." + oid_aux3[1]);
                String length_value_aux2 = "";

                if (value_aux_type.equals("I") || value_aux_type.equals("S")) {
                    length_value_aux2 = "1";
                } else if (value_aux_type.equals("T") || value_aux_type.equals("S")) {
                    length_value_aux2 = "7";
                }
                oid_value_list = oid_value_list + value_aux_type + "\0" + length_value_aux2 + "\0" + values_separados[aux3] + "\0";
            }
                }
            message = this.getTag() + "\0" + type + "\0" + Instant.now().toString() + "\0" + this.getMessageIdentifier()+ "\0"  + n_elements_oids + "\0" + oid_aux_list + n_elements_values + "\0" + oid_value_list + "" + "\0";
        }
        return message;
    }

    public String toString () {
                //Se o TimeStamp for Local time então podemos usar o formater.
                return Tag + "\0" + Type + "\0" + TimeStamp + "\0" + MessageIdentifier + "\0" + IIDList.toString() + "\0"
                        + ValueList.toString() + "\0" + ErrorList.toString();
            }
}
