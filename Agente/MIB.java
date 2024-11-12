import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MIB {

    private Map<String, Equipment> Mib;
    // Porque n ter private Map<String, List<Equipment>> Mib;  assim dá para ter mais q uma instância do device ,dos sensors etc
    //n fica é por ordem

    // 1 - devices . id, beacon rate ...
    // 2 - sensors .
    // 3 - actuators .
    public MIB (){
        // podia ler um ficheiro e fazer já um povoamento com a info desse ficheiro
        // t

        int Nintance = 1;
        devices device = new devices();
        sensors sensor = new sensors();
        actuators actuator = new actuators();
        Mib.put("1",device);
        Mib.put("2",sensor);
        Mib.put("3",actuator);
    }
    public void fileMibbuild(){

    }
    public void fileMibPovoamento(){

    }
}
