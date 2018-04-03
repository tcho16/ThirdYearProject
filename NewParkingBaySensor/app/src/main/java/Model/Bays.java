package Model;


public class Bays implements Comparable<Bays> {

    int hour;
    int status;
    int distant = -1;

    public Bays(){}

    public Bays(int hour, int status) {
        this.hour = hour;
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    public void setDistant(int d){
        this.distant = d;
    }

    public int getHour(){
        return this.hour;
    }

    public Bays(int[] data) {
        data[0] = hour;
        data[1] = status;
    }

    @Override
    public int compareTo(Bays t) {

        int comparingDistant = ((Bays) t).distant;
        return this.distant - comparingDistant;
    }

}
