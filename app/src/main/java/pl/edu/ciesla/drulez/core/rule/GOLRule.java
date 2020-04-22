package pl.edu.ciesla.drulez.core.rule;

import pl.edu.ciesla.drulez.core.cell.Cell;

public class GOLRule implements Rule {
    int[] truthTable;

    public GOLRule( int pureRule){
        /*
        truthTable[0] = alive -> need alive to die of overpopulation;
        truthTable[1] = alive -> need alive to die of loneliness;
        truthTable[2] = dead -> need min alive for birth;
        truthTable[3] = dead -> need max alive for birth;
        */
            truthTable = new int[4];
            truthTable[0] = pureRule/1000;
            pureRule = pureRule%1000;
            truthTable[1] = pureRule/100;
            pureRule = pureRule%100;
            truthTable[2] = pureRule/10;
            pureRule = pureRule%10;
            truthTable[3] = pureRule;
        }



    public GOLRule(){
        /*
        default new GOLRule(4,1,3,3)
        */
            this(4133);
        }
    @Override
    public int getNextState(Cell target) {
            int alive = 0;
            for(Cell c:target.getNeighbours()){
                if(c == null){
                    continue;
                }
                if(c.getState() != 0){
                    alive++;
                }
            }
            if(target.getState() == 0){
                if(alive >= truthTable[2] && alive <= truthTable[3]){
                    return 1;
                }else{
                    return 0;
                }
            }else{
                if(alive >= truthTable[0] || alive <= truthTable[1]){
                    return 0;
                }else{
                    return 1;
                }
            }
    }
}
