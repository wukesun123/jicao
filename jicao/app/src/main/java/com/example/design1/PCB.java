package com.example.design1;
public class PCB implements Comparable<PCB> {
    String name;//进程名
    int arrive;//到达时间
    int needt;//需要运行时间
    int priority;//优先级
    int used;//已用CPU时间
    int needp;//需求资源
    String status;//状态
    boolean alcate;//是否分配资源
    public PCB(String name, int arrive, int needt, int priority,int needp) {
        this.name = name;
        this.arrive = arrive;
        this.needt = needt;
        this.needp = needp;
        this.priority = priority;
        this.used = 0;
        this.status = "R";
        this.alcate = false;
    }
    public boolean isAlcate() {
        return alcate;
    }
    public void setAlcate(boolean alcate) {
        this.alcate = alcate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public void setUsed(int used) {
        this.used = used;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getName() {
        return name;
    }
    public int getArrive() {
        return arrive;
    }
    public int getNeedt() {
        return needt;
    }
    public int getPriority() {
        return priority;
    }
    public int getUsed() {
        return used;
    }
    public int getNeedp() {
        return needp;
    }
    public String getStatus() {
        return status;
    }
    public String toCSV() {
        return name + "," + arrive + "," + needt + "," + priority + "," + needp + "," + status;
    }
    @Override
    public int compareTo(PCB other) {
        if (this.priority ==other.priority)
            return this.arrive-other.arrive;
        else
            return other.priority-this.priority;
    }
}
