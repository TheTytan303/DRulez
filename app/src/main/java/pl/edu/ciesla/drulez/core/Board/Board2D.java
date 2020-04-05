package pl.edu.ciesla.drulez.core.Board;

import java.util.ArrayList;

import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell2D;
import pl.edu.ciesla.drulez.core.rule.Rule;

public class Board2D {
    ArrayList<Cell> cells;
    int width, height;
    boolean isPeriodic;

    public Board2D(int width, int height, boolean periodic, Rule rule){
        cells = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.isPeriodic = periodic;
        for(int i = 0; i<height;i++){
            for(int j = 0; j< width; j++){
                cells.add(new Cell2D(rule));
            }
        }
        for(int i = 0; i<height ;i++){
            for(int j = 0; j< width ; j++){
                Cell[] tmp = {
                        getCellAt(j+1,i+1),
                        getCellAt(j+1,i),
                        getCellAt(j+1,i-1),
                        getCellAt(j,i+1),
                        getCellAt(j,i-1),
                        getCellAt(j-1,i+1),
                        getCellAt(j-1,i),
                        getCellAt(j-1,i-1)
                };
                getCellAt(j,i).setNeighbours(tmp);
            }
        }
    }

    public void setCells(int x, int y, int[][] state){
        for(int i=0; i<state[0].length;i++){
            for(int j=0; j<state.length;j++){
                getCellAt(x+j, y+i).setState(state[j][i]);
            }
        }
    }
    public void nextTimeStep(){
        for(Cell c: cells){
            c.nextState();
        }
        for(Cell c: cells){
            c.updateState();
        }
    }
    public ArrayList<Cell> getCells(){
        return this.cells;
    }
    public Cell getCellAt(int x, int y){
        while(x<0 || y<0){
            x+=width;
            y+=height;
        }
        x=x%width;
        y=y%height;
        return cells.get((y*width) + x);
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    @Override
    public String toString(){
        String returnVale="";
        int i =0;
        for(Cell c: this.cells){
            if(c.getState() == 0){
                returnVale= returnVale.concat(" ");
            }else{
                returnVale= returnVale.concat("X");
            }
            i++;
            if(i %width == 0){
                returnVale= returnVale.concat("\n");
            }
        }
        return returnVale;
    }
}