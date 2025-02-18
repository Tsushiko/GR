import java.time.Instant;
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
        this.Mib = new HashMap<>();
        //int Nintance = 1;
        devices devices = new devices();
        sensors sensors = new sensors();
        actuators actuators = new actuators();
        Mib.put("1",devices);
        Mib.put("2",sensors);
        Mib.put("3",actuators);
        String timestamp=temponow();
        ((devices) Mib.get("1")).device("1","Temperatura e A/C, Luminosidade e LED","20","2","2",timestamp,timestamp,timestamp,"1","0");
        ((sensors) Mib.get("2")).sensor("1", "Temperatura","10","0","30",timestamp);
        ((actuators) Mib.get("3")).actuator("1", "A/C","10","0","30",timestamp);
        ((sensors) Mib.get("2")).sensor("2", "Luminosidade","10","0","100",timestamp);
        ((actuators) Mib.get("3")).actuator("2", "Lampada","50","0","100",timestamp);
        emulacao();

    }
    public Map<String, Equipment> getMib(){
        return this.Mib;
    }

    public String temponow(){
        Instant now = Instant.now();
        System.out.println("now"+ now);
        String now_string= now.toString();
        System.out.println(now_string);
        String[] tempo = now_string.split("T");
        System.out.println(tempo[0]+tempo[1]);
        String[] data = tempo[0].split("-");
        String[] time = tempo[1].split("Z");
        String[] time_final = time[0].split("\\.");
        return String.join(":",data[2]+":"+time_final[0]+":"+time_final[1]);
    }
    public void emulacao(){
        new Thread(() -> {
            while(true) {
                try {
                    //Tempo =  Beaconrate * 1000 (milisegundos)
                    //Thread.sleep(20000);
                    Thread.sleep(Integer.parseInt(((devices)Mib.get("1")).getValoremular("1","3"))*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Update nos sensores: " + System.currentTimeMillis());
                new Thread(() -> {
                    update();
                }).start();
            }
        }).start();
    }
    public void update(){
        //Se os atuadores estiverem Desligados
                                    //  A/C   status
       // System.out.println(Mib.get("3").getValue("1","3"));
        //if (Mib.get("3").getValue("1","3").equals("I"+"\0"+"1"+"\0"+"0")){
        //    System.out.println("Atuador A/C está desligado");
                                         //Sensor Temperatura   Objeto status    Valor
        //    ((sensors) Mib.get("2")).setValoremular("1","3","10");
        //}
        //if (Mib.get("3").getValue("2","3").equals("I"+"\0"+"1"+"\0"+"0")){
         //   System.out.println("Atuador Lampada está desligado");
         //                               //Sensor Temperatura   Objeto status    Valor
         //   ((sensors) Mib.get("2")).setValoremular("2","3","10");
        //}

        //Se os atuadores estiverem Ligados
        //Valor do atuador
        int atuador1=Integer.parseInt(((actuators)Mib.get("3")).getValoremular("1","3"));
        int sensor1=Integer.parseInt(((sensors)Mib.get("2")).getValoremular("1","3"));
        if (atuador1>sensor1){
            //Sensor Temperatura   Objeto status    Valor
            if(!(atuador1==0)) {
                System.out.println("Atuador A/C está ligado e o sensor vai aumentar.");
                ((sensors) Mib.get("2")).setValoremular("1", "3", String.valueOf(sensor1 + 1));
            }
        }else if (atuador1<sensor1) {
            //Sensor Temperatura   Objeto status    Valor
            if(!(atuador1==0)) {
                System.out.println("Atuador A/C está ligado e o sensor vai diminuir.");
                ((sensors) Mib.get("2")).setValoremular("1", "3", String.valueOf(sensor1 - 1));
            }
        }

        int atuador2=Integer.parseInt(((actuators)Mib.get("3")).getValoremular("2","3"));
        int sensor2=Integer.parseInt(((sensors)Mib.get("2")).getValoremular("2","3"));
        if (atuador2>sensor2){
            //Sensor Luminosidade   Objeto status    Valor
            if(!(atuador2==0)) {
                System.out.println("Atuador Lampada está ligado e o sensor vai aumentar.");
                ((sensors) Mib.get("2")).setValoremular("2", "3", String.valueOf(sensor2 + 1));
            }
        }else if (atuador2<sensor2) {
            //Sensor Luminosidade   Objeto status    Valor
            if(!(atuador2==0)) {
                System.out.println("Atuador Lampada está ligado e o sensor vai diminuir.");
                ((sensors) Mib.get("2")).setValoremular("2", "3", String.valueOf(sensor2 - 1));
            }
        }
        int reset=Integer.parseInt(((devices)Mib.get("1")).getValoremular("1","10"));
        if (reset==1){
            // Alterar o valor de reset para 0
            ((devices) Mib.get("1")).setValoremular("1","10","0");
            String timestamp = temponow();
            // Alterar o uptime para o tempo atual
            ((devices) Mib.get("1")).setValoremular("1","7",String.valueOf(timestamp));
            // Alterar o lastTimeUpdated para o tempo atual
            ((devices) Mib.get("1")).setValoremular("1","8",String.valueOf(timestamp));
            System.out.println("O device levou reset.");
        }

        System.out.println(Thread.currentThread().getName() + " finished updating.");
    }

    public void fileMibbuild(){

    }

    public void fileMibPovoamento(){

    }
}
