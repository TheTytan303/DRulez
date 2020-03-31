package pl.edu.ciesla.drulez.core.Board;

import java.util.ArrayList;

import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell2D;
import pl.edu.ciesla.drulez.core.rule.Rule;

public class Board2D {
    ArrayList<Cell> cells;
    int width, height;

    public Board2D(int width, int height, boolean periodic, Rule rule){
        cells = new ArrayList<>();
        this.width = width;
        this.height = height;

        for(int i = 0; i<height;i++){
            for(int j = 0; j< width; j++){
                cells.add(new Cell2D(rule));
            }
        }
        if(periodic){
            //i && j != 0 && width/height
            //only "middle" cells
            for(int i = 1; i<height -1;i++){
                for(int j = 1; j< width -1; j++){
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
            //top border -> i=y=0
            //bottom border -> i=y=height
            for(int j = 1; j< width -1; j++){
                height--;
                Cell[] tmp = {
                        getCellAt(j+1,1),
                        getCellAt(j+1,0),
                        getCellAt(j+1,height),
                        getCellAt(j,1),
                        getCellAt(j,height),
                        getCellAt(j-1,1),
                        getCellAt(j-1,0),
                        getCellAt(j-1,height)
                };
                getCellAt(j,0).setNeighbours(tmp);
                Cell[] tmp2 = {
                        getCellAt(j+1,height),
                        getCellAt(j+1,0),
                        getCellAt(j+1,height-1),
                        getCellAt(j,0),
                        getCellAt(j,height-1),
                        getCellAt(j-1,height),
                        getCellAt(j-1,0),
                        getCellAt(j-1,height-1)
                };
                getCellAt(j,height).setNeighbours(tmp2);
                height++;
            }
            //left border -> j=x=0
            //right border -> j=x=width
            for(int i = 1; i<height -1;i++){
                width --;
                Cell[] tmp = {
                        getCellAt(1,i+1),
                        getCellAt(0,i+1),
                        getCellAt(width,i+1),
                        getCellAt(1,i),
                        getCellAt(width,i),
                        getCellAt(width,i-1),
                        getCellAt(0,i-1),
                        getCellAt(1,i-1)
                };
                getCellAt(0,i).setNeighbours(tmp);
                Cell[] tmp2 = {
                        getCellAt(width, i+1),
                        getCellAt(0, i+1),
                        getCellAt(width-1, i+1),
                        getCellAt(0, i),
                        getCellAt(width-1, i),
                        getCellAt(width-1, i-1),
                        getCellAt(width, i-1),
                        getCellAt(0, i-1)
                };
                getCellAt(width,i).setNeighbours(tmp2);
                width ++;
            }
            width--;
            height--;
            //left top
            getCellAt(0,0).setNeighbours(new Cell[] {
                    getCellAt(width,height),
                    getCellAt(width,0),
                    getCellAt(width,1),
                    getCellAt(0,height),
                    getCellAt(0,1),
                    getCellAt(1,height),
                    getCellAt(1,0),
                    getCellAt(1,1)
            });
            //right top
            getCellAt(width,0).setNeighbours(new Cell[] {
                    getCellAt(0,height),
                    getCellAt(0,0),
                    getCellAt(0,1),
                    getCellAt(width,height),
                    getCellAt(width,1),
                    getCellAt(width-1,height),
                    getCellAt(width-1,0),
                    getCellAt(width-1,1)
            });
            //left bottom
            getCellAt(0,height).setNeighbours(new Cell[] {
                    getCellAt(1,0),
                    getCellAt(1,height),
                    getCellAt(1,height-1),
                    getCellAt(0,0),
                    getCellAt(0,height-1),
                    getCellAt(width,0),
                    getCellAt(width,height),
                    getCellAt(width,height-1)
            });
            Cell[] tmp = {
                    getCellAt(width-1, height-1),
                    getCellAt(width-1, height),
                    getCellAt(width-1, 0),
                    getCellAt(width, height-1),
                    getCellAt(width, 0),
                    getCellAt(0, 0),
                    getCellAt(0, height),
                    getCellAt(0, height-1)
            };
            getCellAt(width,height).setNeighbours(tmp);
            width++;
            height++;
        }
    }

    /*

    int[3][4]:
    1 1 0 0
    1 0 0 1
    1 1 1 0

    */
    public void setCells(int x, int y, int[][] state){
        for(int i=0; i<state.length;i++){
            for(int j=0; j<state[0].length;j++){
                getCellAt(x+j, y+i).setState(state[i][j]);
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
    private Cell getCellAt(int x, int y){
        return cells.get((y*width) + x);
    };
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
