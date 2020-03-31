package pl.edu.ciesla.drulez.core.cell;


import pl.edu.ciesla.drulez.core.rule.Rule;

public class Cell1D implements Cell {
    Rule rule;
    int state;
    int nextState;
    Cell[] neighbours;

    public Cell1D(Rule rule){
        this.rule = rule;
        this.state = 0;
        this.nextState = 0;
    }
    @Override
    public void setNeighbours(Cell[] neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public Cell[] getNeighbours() {
        return this.neighbours;
    }

    @Override
    public void setRule(Rule rule) {
        this.rule = rule;
    }
    @Override
    public void updateState(){
        this.state = nextState;
    }
    @Override
    public void nextState(){
        this.nextState = rule.getNextState(this);
    };


    @Override
    public int getState() {
        return this.state;
    }
    @Override
    public void setState(int state) {
        this.state = state;
        this.nextState = state;
    }

    @Override
    public String toString(){
        String returnVale="";
        returnVale = returnVale.concat(this.state + "->" + this.nextState);
        return returnVale;
    }
}
