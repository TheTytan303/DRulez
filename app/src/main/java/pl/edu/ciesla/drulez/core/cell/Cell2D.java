package pl.edu.ciesla.drulez.core.cell;

import pl.edu.ciesla.drulez.core.rule.Rule;

public class Cell2D implements Cell{
    int state;
    int nextState;
    Rule rule;
    Cell[] neighbours;
    public Cell2D(Rule rule){
        this.rule = rule;
        this.state = 0;
        this.nextState = state;
    }

    @Override
    public Cell[] getNeighbours() {
        return neighbours;
    }

    @Override
    public void setRule(Rule rule) {
        this.rule = rule;
    }

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
    public void nextState() {
        this.nextState = rule.getNextState(this);
    }

    @Override
    public void updateState() {
        this.state = nextState;
    }

    @Override
    public void setNeighbours(Cell[] neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString(){
        return this.state+"";
    }
}
