package pl.edu.ciesla.drulez.core.cell;


import pl.edu.ciesla.drulez.core.rule.Rule;

public interface Cell {
    Cell[] getNeighbours();
    void setRule(Rule rule);
    int getState();
    void setState(int state);
    void nextState();
    void updateState();
    public void setNeighbours(Cell[] neighbours);
}
