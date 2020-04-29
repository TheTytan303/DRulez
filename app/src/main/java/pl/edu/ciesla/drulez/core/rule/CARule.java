package pl.edu.ciesla.drulez.core.rule;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

import pl.edu.ciesla.drulez.core.Board.Board2D;
import pl.edu.ciesla.drulez.core.cell.Cell;

public class CARule implements Rule {
    Board2D.GrainGrowth growthType;
    private static CARule instance = new CARule();
    private CARule(){};
    public static CARule getInstance(){return instance;}

    /*public void setGrowthType(Board2D.GrainGrowth growthType) {
        this.growthType = growthType;
    }*/

    @Override
    public int getNextState(Cell target) {
        if(target.getState() != 0){
            return target.getState();
        }
        Map<Integer, Integer> states = new HashMap<>(); //mapa id_ziarna -> ilość sąsiadów z tego ziarna
        Pair<Integer, Integer> returnVale = new Pair<>(0,0);
        Cell[] neighbours = target.getNeighbours();
        if(neighbours == null) return 0;
        for(Cell c: neighbours){
            Integer count = states.get(c.getState());
            if(count==null){
                count = 0;
            }
            if(c.getState()!=0) //jeżeli jest już w mapie
            states.put(c.getState(), (++count) );
            if (count >= returnVale.second){
                returnVale = new Pair<>(c.getState(),count);
            }
        }
        return returnVale.first;

    }
}
