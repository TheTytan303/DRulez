package pl.edu.ciesla.drulez.core.rule;


import pl.edu.ciesla.drulez.core.cell.Cell;

public interface Rule {
    int getNextState(Cell target);
}
