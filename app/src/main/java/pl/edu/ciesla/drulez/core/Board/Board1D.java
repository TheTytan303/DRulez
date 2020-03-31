package pl.edu.ciesla.drulez.core.Board;


import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell1D;
import pl.edu.ciesla.drulez.core.rule.Rule1D;

import java.util.ArrayList;

public class Board1D {
    ArrayList<Cell> cells;
    public Board1D(int size){
        cells = new ArrayList<>();
        for(int i=0; i<size ; i++){
            cells.add(new Cell1D(Rule1D.getInstance()));
        }
        cells.get(0).setNeighbours(new Cell[]{cells.get(size-1),cells.get(1)});
        cells.get(size-1).setNeighbours(new Cell[]{cells.get(size-2),cells.get(0)});
        for(int i=1; i<size-1 ; i++){
            cells.get(i).setNeighbours(new Cell[]{cells.get(i-1),cells.get(i+1)});
        }
    }
    public void nextTimeStep(){

        for(Cell c: cells){
            c.updateState();
        }
        for(Cell c: cells){
            c.nextState();
        }
    }
    public ArrayList<Cell> getCells(){
        return this.cells;
    }


    @Override
    public String toString(){
        String returnVale="";
        for(Cell c: this.cells){
            if(c.getState() == 0){
                returnVale= returnVale.concat(" ");
            }else{
                returnVale= returnVale.concat("X");
            }
        }
        return returnVale;
    }
}
