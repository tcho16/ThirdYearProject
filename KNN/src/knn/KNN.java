
package knn;

import java.util.ArrayList;
import java.util.Collections;

public class KNN {

    static ArrayList<Bays> bays = new ArrayList<>();
    final static int HOURTOPREDICT = 20;
    final static int GETNEARESTK = 2;

    public static void main(String[] args) {
        initData();
        calculateDistance();
    }

    //Method to calculate at point
    static void calculateDistance() {
        for(Bays bay : bays) {
            int distance = (int) Math.sqrt( (HOURTOPREDICT-bay.hour)*(HOURTOPREDICT-bay.hour)    );            
            bay.distant = distance;            
        }
        
        //sort out bays based on distance
        Collections.sort(bays);
        
        getKNeighbours(GETNEARESTK);
        
    }

    private static void initData() {
        bays.add(new Bays(1, 0));
        bays.add(new Bays(2, 0));
        bays.add(new Bays(3, 0));
        bays.add(new Bays(4, 0));
        bays.add(new Bays(5, 0));
        bays.add(new Bays(6, 0));
        bays.add(new Bays(7, 0));
        bays.add(new Bays(8, 0));
        bays.add(new Bays(9, 1));
        bays.add(new Bays(10, 1));
        bays.add(new Bays(11, 1));
        bays.add(new Bays(12, 1));
        bays.add(new Bays(13, 1));
        bays.add(new Bays(14, 1));
        bays.add(new Bays(15, 1));
        bays.add(new Bays(16, 1));
        bays.add(new Bays(17, 1));
        bays.add(new Bays(18, 0));
        bays.add(new Bays(19, 0));
        bays.add(new Bays(20, 0));
        bays.add(new Bays(21, 0));
        bays.add(new Bays(22, 0));
        bays.add(new Bays(23, 0));
        bays.add(new Bays(24, 0));

    }

    private static void getKNeighbours(int ii) {    
        int numberOfOccupiedCounts = 0;
        for(int i = 0; i < ii ; i++){
            if(bays.get(i).status == 1){
            numberOfOccupiedCounts++;}
        }
        if(numberOfOccupiedCounts >= ii/2 ){
            System.out.println("Occupied");
        }else{
            System.out.println("Vacant");
        }
    }

}
