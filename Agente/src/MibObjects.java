import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MibObjects {
    private String iid; // necessário?
    private String name;
    private String type; // Tipo, String, etc...
    private String access; // read-only, read-write, etc..
    private String description; // descrição
    private String value; // valor em si
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public MibObjects(String iid,String name,String type,String access,String description,String value){//Integer value){
        this.iid = iid;
        this.name = name;
        this.type = type;
        this.access = access;
        this.description = description;
        this.value = value.toString();
    }

    public String getValue(){
        String valor="";
        lock.readLock().lock();
        try {
            valor += this.value;
            // Thread.sleep(1000);
        } finally {
            lock.readLock().unlock();
            System.out.println(Thread.currentThread().getName() + " finished reading.");
        }
        return valor;
    }

    public String getType(){return this.type;}

    public String getAccess(){return this.access; }

    public void setValue(String valor){
        lock.writeLock().lock();
        try {
            this.value=valor;
            /*Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();*/
        } finally {
            lock.writeLock().unlock();
            System.out.println(Thread.currentThread().getName() + " finished writing.");
        }

    }
    //Ver qual formato será o melhor
    public String to_String(){
        return this.name + ":" +this.iid+" "+this.type+" "+this.access+" "+this.value+"\n" +this.description+"\n";
    }

}
