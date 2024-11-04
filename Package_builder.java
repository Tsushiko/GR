import java.time.LocalDate;

public class Package_builder {
    //Classe para a costrução da trama, não sei se tem de ser feita em separado ou se isto é sequer util?
    //Formato da Trama.
    // Tag | Type | Time-Stamp | Message-Identifier | IID-List | Value-List | Error-List
    private String Tag;
    private char Type;
    //private LocalDate TimeStamp;
    private String TimeStamp;
    private int MessageIdentifier;
    private String IIDList;
    private String ValueList;
    // private String ErrorList; //No enunciado diz q o gestor tem de mandar uma lista vazia, nada i guess

    // Existe o  Formatter para o tempo
    //LocalDate localDate = LocalDate.now(); // Para termos o tempo de agora
    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("day:month:year:hours:mins:secs:ms");
    //String formattedString = localDate.format(formatter);

    // Em principio o construtor vai receber variáveis da main, variáveis q vêem do gestor colocar como input,
    // Mas ainda percebo bem a trama por isso vai ficar assim com uma predefinida, para testar apenas.

    public Package_builder(int m){//String ta, char ty, String time,String m, String iid, String value, String error){

        // Acho q dá para restringir uma variável para so ter cenas especificas, tipo Type só puder ser G,S,N ou R
        this.Tag = "kdk847ufh84jg87g";
        this.Type = 'G';
        //Stor na teorica disse q podia ser random ou apenas irmos incrementar com cada mensagem
        //Acho q incrementar a cada mensagem ajuda
        this.TimeStamp="day";
        this.MessageIdentifier = m;
        this.IIDList="1.2.1.1";
        this.ValueList="Valores";
        //this.ErrorList=;
    }
    public String toString(){
        //Se o TimeStamp for Local time então podemos usar o formater.
        return Tag + "\0" + Type + "\0" + TimeStamp + "\0" + Integer.toString(MessageIdentifier) +"\0" + IIDList +"\0"
                + ValueList + "\0"; //ErrorList;
    }
}
