public class MibObjects {
    private String iid; // necessário?
    private String name;
    private String type; // Tipo, String, etc...
    private String access; // read-only, read-write, etc..
    private String description; // descrição
    private String value; // valor em si

    public MibObjects(String iid,String name,String type,String access,String description,String value){//Integer value){
        this.iid = iid;
        this.name = name;
        this.type = type;
        this.access = access;
        this.description = description;
        this.value = value.toString();
    }
    //Ver qual formato será o melhor
    public String to_String(){
        return this.name + ":" +this.iid+" "+this.type+" "+this.access+" "+this.value+"\n" +this.description+"\n";
    }
}
