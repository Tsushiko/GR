import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class devices extends Equipment {
    private int Ninstances;
    private List<MibObjects[]> InstanceOfDevices;
   // private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    //id - Identificador
    //type - Tipo
    //beaconRate - Frequencia em segundos para as notificações
    //nSensors - Numero de sensores no device e presente nos Sensors
    //nActuators - Numero de atuadores no device e presentes nos Atuadores
    //dateAndTime - Data com tempo do setup do device
    //upTime - Á quanto tempo o device ta a funcionar desde do ultimo boot/reset
    //lastTimeUpdated - Ultima data desde q levou update
    //operationalStatus -  0 standby , 1 normal , 2 non - operational
    //reset - 0 no reset ,  1 precisa de dar reset

    public devices(){
        this.Ninstances = 0;
        this.InstanceOfDevices = new ArrayList<>();
        MibObjects[] objs = new MibObjects[11];
        this.InstanceOfDevices.add(objs);
    }

    //Função para criar novos devices
    public void device(String id, String type, String beaconRate, String nSensors, String nAtuadores, String dateAndTime, String upTime, String lastTimeUpdated, String operationalStatus, String reset){
        MibObjects[] objs = new MibObjects[11];
        objs[1]= new MibObjects("1","id","String","read-only","Tag...",id);
        objs[2]= new MibObjects("2","type","String","read-only","Tag...",type);
        objs[3]= new MibObjects("3","beaconRate","Integer","read-write","Tag...",beaconRate);
        objs[4]= new MibObjects("4","nSensors","Integer","read-only","Tag...",nSensors);
        objs[5]= new MibObjects("5","nAtuadores","Integer","read-only","Tag...",nAtuadores);
        objs[6]= new MibObjects("6","dateAndTime","Timestamp","read-write","Tag...", dateAndTime);
        objs[7]= new MibObjects("7","upTime","Timestamp","read-only","Tag...", upTime);
        objs[8]= new MibObjects("8","lastTimeUpdated","Timestamp","read-only","Tag...", lastTimeUpdated);
        objs[9]= new MibObjects("9","operationalStatus","Integer","read-only","Tag...", operationalStatus);
        objs[10]= new MibObjects("10","reset","Integer","read-write","Tag...", reset);
        this.InstanceOfDevices.add(objs);
        this.Ninstances +=1;
    }

    public String getValoremular(String instance,String idd){
        return this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getValue();
    }

    public void setValoremular(String instance,String idd,String valor ){
        this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].setValue(valor);
    }

    public String typeconvert(String x) {
        String res = "";
        res+=String.valueOf(x.charAt(0))+"\0";
        if (x.equals("String")||x.equals("Integer")){
            res+="1";
        }
        if (x.equals("Timestamp")){
            res+="5";
        }
        // Data-Type,Length

        return res;
    }
    public String getValue(String instance,String idd){
        //lock de ler
        //lock.readLock().lock();
        String valor ="";
        valor += typeconvert(this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getType())+"\0";
        //try {
        valor += this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getValue();
            // Thread.sleep(1000);
       // } finally {
         //   lock.readLock().unlock();
        //    System.out.println(Thread.currentThread().getName() + " finished reading.");
      //  }
        return valor;
    }
    public String setValue(String instance,String idd,String type,String valor){
        //Lock de escrever
        //idd = objeto
        String erro ="0";
        String tipo = this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getType();

        //Erro de escrever num objeto que só pode ser lido
        if (this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getAccess().equals("read-only")){return "4";}

        //Erro de valor não suportado, colocar um valor de um tipo que não é o dele
        if (!type.equals(String.valueOf(tipo.charAt(0)))){return "7";}

        //Erro caso o valor para ser escrito para o reset é diferente de 0 e 1.
        if(idd.equals("10") && !valor.equals("0") && !valor.equals("1")){
            return "11";
        }


       // lock.writeLock().lock();
        //try {
        this.InstanceOfDevices.get(Integer.parseInt(instance))[Integer.parseInt(idd)].setValue(valor);
        //} finally {
         //   lock.writeLock().unlock();
         //   System.out.println(Thread.currentThread().getName() + " finished writing.");
        //}

        return erro;
    }
    public String getnObjs(){
        System.out.println(String.valueOf(this.InstanceOfDevices.get(1).length-1));

        return String.valueOf(this.InstanceOfDevices.get(1).length-1);
    }
    public String getInstances(){
        System.out.println(this.Ninstances);
        return String.valueOf(this.Ninstances);
    }
}
