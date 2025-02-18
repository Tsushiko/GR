import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<String, String> MiniMIB = new HashMap<>();
        popular_MiniMIB(MiniMIB);
        interface_completa interface1 = new interface_completa(MiniMIB);
    }

    public static void popular_MiniMIB(HashMap<String, String> miniMIB) {
        // Estrutura "device" (Grupo)
        miniMIB.put("1.1", "S");     // device.id
        miniMIB.put("1.2", "S");     // device.type
        miniMIB.put("1.3", "I");     // device.beaconRate
        miniMIB.put("1.4", "I");     // device.nSensors
        miniMIB.put("1.5", "I");     // device.nActuators
        miniMIB.put("1.6", "T");     // device.dateAndTime
        miniMIB.put("1.7", "T");     // device.upTime
        miniMIB.put("1.8", "T");     // device.lastTimeUpdated
        miniMIB.put("1.9", "I");     // device.operationalStatus
        miniMIB.put("1.10", "I");    // device.reset

        // Estrutura "sensors" (Tabela)
        miniMIB.put("2.1", "S");      // sensors.id
        miniMIB.put("2.2", "S");      // sensors.type
        miniMIB.put("2.3", "I");     // sensors.status
        miniMIB.put("2.4", "I");    // sensors.minValue
        miniMIB.put("2.5", "I");    // sensors.maxValue
        miniMIB.put("2.6", "T");    // sensors.lastSamplingTime

        // Estrutura "actuators" (Tabela)
        miniMIB.put("3.1", "S");        // actuators.id
        miniMIB.put("3.2", "S");        // actuators.type
        miniMIB.put("3.3", "I");       // actuators.status
        miniMIB.put("3.4", "I");       // actuators.minValue
        miniMIB.put("3.5", "I");       // actuators.maxValue
        miniMIB.put("3.6", "T");     // actuators.lastControlTime
    }
}