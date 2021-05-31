public class Server {
    private int core;
    private int mem;
    private int disk;
    private String type;
    private int limit;
    private int isRunning;
    private int typeCount;

    public Server (String type, int typeCount , int core, int mem, int disk, int isRunning) {
        setServerCore(core);
        setServerMem(mem);
        setServerDisk(disk);
        setServerLimit(limit);
        setServerType(type);
        this.isRunning = isRunning;
        this.typeCount = typeCount;
    }

    public void setServerCore(int _core){
        this.core = _core;
    }
    public void setServerMem(int _mem){
        this.mem = _mem;
    }
    public void setServerDisk(int _disk){
        this.disk = _disk;
    }
    public void setServerType(String _type){
        this.type = _type;
    }
    public void setServerLimit(int _limit){
     
        this.limit = _limit;
    }
    public int getTypeCount(){
        return this.typeCount;
    }
    public int getServerCore(){
        return this.core;
    }
    public int getServerMem(){
        return this.mem;
    }
    public int getServerDisk(){
        return this.disk;
    }
    public String getServerType(){
        return this.type;
    }
    public int getServerLimit(){
        return this.limit;
    }
    public int getIsRunning(){
        return this.isRunning;
    }
}