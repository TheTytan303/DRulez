package pl.edu.ciesla.drulez.core.cell;

import pl.edu.ciesla.drulez.core.rule.MonteCarloRule;
import pl.edu.ciesla.drulez.core.rule.Rule;

public class Cell2D implements Cell{
    private int state;
    private int nextState;
    private Rule rule;
    private Cell[] neighbours;
    private float x, y;
    private float energy;
    public Cell2D(Rule rule){
        this.rule = rule;
        this.state = 0;
        this.nextState = state;
    }

    public Cell2D(Rule rule, float x, float y){
        this.x =x;
        this.y =y;
        this.rule = rule;
        this.state = 0;
        this.nextState = state;
        //System.out.println("X: " + x + " | Y: " + y);
    }

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public int getNextState() {
        return this.nextState;
    }

    public float getEnergy() {
        return energy;
    }
    public void setEnergy(float energy) {
        this.energy = energy;
    }
    public float setEnergy(MonteCarloRule rule){
        this.energy = rule.getEnergyOf(this);
        return this.energy;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
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
