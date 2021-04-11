package net.noobsters.kern.paper.punishments;

public class Timestamp<O> {
    Long updatetime = System.currentTimeMillis();
    O object;

    public Timestamp(O o) {
        this.object = o;
    }

    public O getObject() {
        return object;
    }

    /**
     * Method to update the object and the timestampt at once.
     * 
     * @param o new object
     */
    public void setObject(O o) {
        this.updatetime = System.currentTimeMillis();
        this.object = o;
    }

    public long age(){
        return System.currentTimeMillis() - updatetime;
    }

}
