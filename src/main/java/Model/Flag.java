package Model;

public class Flag {

    private boolean flag;

    public synchronized void set() {
        flag = true;
    }

    public synchronized boolean isSet() {
        return flag;
    }
}
