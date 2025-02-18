import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class actuators extends Equipment {
   // private List<Row> actuator;
    private int Ninstances;
    private List<MibObjects[]> InstanceOfActuators;
    //private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public actuators(){
        this.Ninstances=0;
        this.InstanceOfActuators = new ArrayList<>();
        MibObjects[] objs = new MibObjects[7];
        this.InstanceOfActuators.add(objs);
    }
    public void actuator(String id,String type, String status, String minValue, String maxValue, String lastControlTime){
        MibObjects[] objs = new MibObjects[7];
        objs[1]= new MibObjects("1","id","String","read-only","Tag...",id);
        objs[2]= new MibObjects("2","type","String","read-only","Tag...",type);
        objs[3]= new MibObjects("3","status","Integer","read-write","Tag...",status);
        objs[4]= new MibObjects("4","minValue","Integer","read-only","Tag...",minValue);
        objs[5]= new MibObjects("5","maxValue","Integer","read-only","Tag...",maxValue);
        objs[6]= new MibObjects("6","lastControlTime","Timestamp","read-only","Tag...",lastControlTime);
        this.InstanceOfActuators.add(objs);
        this.Ninstances +=1;
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
        valor += typeconvert(this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getType())+"\0";
        //try {
        valor += this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getValue();
            // Thread.sleep(1000);
        //} finally {
         //   lock.readLock().unlock();
        //    System.out.println(Thread.currentThread().getName() + " finished reading.");
        //}
        return valor;
    }
    public String setValue(String instance,String idd,String type,String valor){
        //Lock de escrever
        //idd = objeto
        String erro ="0";
        String tipo = this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getType();

        //Erro de escrever num objeto que só pode ser lido
        if (this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getAccess().equals("read-only")){return "4";}

        //Erro de valor não suportado, colocar um valor de um tipo que não é o dele
        if (!type.equals(String.valueOf(tipo.charAt(0)))){return "7";}

        //Erro de escrever valor de status fora do min(4) e max(5)
        if(idd.equals("3") && (Integer.parseInt(valor)<Integer.parseInt(this.InstanceOfActuators.get(Integer.parseInt(instance))[4].getValue())|| Integer.parseInt(valor)>Integer.parseInt(this.InstanceOfActuators.get(Integer.parseInt(instance))[5].getValue()))){
            return "11";
        }

        //lock.writeLock().lock();
        //try {
        this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].setValue(valor);
        //} finally {
        //    lock.writeLock().unlock();
        //    System.out.println(Thread.currentThread().getName() + " finished writing.");
        //}

        return erro;
    }
    public String getValoremular(String instance,String idd){
        return this.InstanceOfActuators.get(Integer.parseInt(instance))[Integer.parseInt(idd)].getValue();
    }
    public String getnObjs(){
        System.out.println(String.valueOf(this.InstanceOfActuators.get(1).length-1));

        return String.valueOf(this.InstanceOfActuators.get(1).length-1);
    }
    public String getInstances(){
        System.out.println(this.Ninstances);
        return String.valueOf(this.Ninstances);
    }

}
