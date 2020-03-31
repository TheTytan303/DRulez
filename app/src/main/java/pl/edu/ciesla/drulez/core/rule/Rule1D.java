package pl.edu.ciesla.drulez.core.rule;


import pl.edu.ciesla.drulez.core.cell.Cell;

public class Rule1D implements Rule{
    private int specyficRule;
    private Boolean[] truthTable;


    private static Rule1D instance = new Rule1D();
    private Rule1D(){
        this.specyficRule = 0;
    };



    public static Rule1D getInstance() {
        return instance;
    }
    public void setSpecyficRule(int rule){
        this.specyficRule = rule;
        this.truthTable = decode(rule);
    }

    @Override
    public int getNextState(Cell target){
        Boolean returnVale;
        if(target.getState() == 0){
            returnVale = this.targetOff(target.getNeighbours());
        }else{
            returnVale = this.targetOn(target.getNeighbours());
        }
        return returnVale ? 1:0;
    }
    private Boolean targetOn(Cell... cell){
        if(cell[0].getState() == 0 && cell[1].getState() == 0){
            return this.truthTable[2];
        }else if(cell[0].getState() == 0 && cell[1].getState() == 1){
            return this.truthTable[3];
        } else if(cell[0].getState() == 1 && cell[1].getState() == 1){
            return this.truthTable[7];
        }else {
            return this.truthTable[6];
        }
    }
    private Boolean targetOff(Cell... cell){
        if(cell[0].getState() == 0 && cell[1].getState() == 0){
            return this.truthTable[0];
        }else if(cell[0].getState() == 0 && cell[1].getState() == 1){
            return this.truthTable[1];
        } else if(cell[0].getState() == 1 && cell[1].getState() == 1){
            return this.truthTable[5];
        }else {
            return this.truthTable[4];
        }
    }
    private Boolean[] decode(int n){
        Boolean[] returnVale = new Boolean[8];
        for(int i=7;i>=0;i--){
            int tmp =(int) Math.pow(2,i);
            if(n>=tmp){
                returnVale[i] = true;
                n = n - tmp;
            }
            else {
                returnVale[i] = false;
            }
        }
        return returnVale;
    }
}
