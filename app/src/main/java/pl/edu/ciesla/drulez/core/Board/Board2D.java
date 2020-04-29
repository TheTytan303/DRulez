package pl.edu.ciesla.drulez.core.Board;


import android.graphics.PointF;
import android.util.Pair;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell2D;
import pl.edu.ciesla.drulez.core.rule.MonteCarloRule;
import pl.edu.ciesla.drulez.core.rule.Rule;

public class Board2D {


    private static final int CELLS_PER_TASK = 250;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private List<int[][]> history;
    private List<Cell> cells;
    private HashSet<Cell> activeCells;
    private Cell derp;
    private int width, height;
    private boolean isPeriodic;
    private int values;
    private GrainGrowth growthType;
    private Pair<Integer, Integer> energyRange;

    private int[][] energy;
    private MonteCarloRule mcRule;

    public enum Nucleation{
        homogeneous,
        radius,
        random,
        banned
    }
    public enum GrainGrowth{
        Moore,
        vonNeuman,
        radius,
        hex_left,
        hex_right,
        hex_random,
        pent_top,
        pent_bottom,
        pent_left,
        pent_right,
        pent_random;


        private static final Random RANDOM = new Random(System.currentTimeMillis());

        public static GrainGrowth randomPent(){
            List<GrainGrowth> pents = new ArrayList<>();
            pents.add(pent_top);
            pents.add(pent_left);
            pents.add(pent_bottom);
            pents.add(pent_right);
            return pents.get(RANDOM.nextInt(pents.size()));
        }
        public static GrainGrowth randomHex(){
            List<GrainGrowth> hexes = new ArrayList<>();
            hexes.add(hex_right);
            hexes.add(hex_left);
            return hexes.get(RANDOM.nextInt(hexes.size()));
        }
        public static boolean randomBool(){
            return RANDOM.nextBoolean();
        }

    }

    public Board2D(int width, int height, boolean fullNeighbours, Rule rule){
        cells = new Vector<>(width*height);
        this.width = width;
        this.height = height;
        for(int i = 0; i<height;i++){
            for(int j = 0; j< width; j++){
                cells.add(new Cell2D(rule, (float)j + RANDOM.nextFloat(), (float)i + RANDOM.nextFloat()));
            }
        }
        if(fullNeighbours)
            mooreGrowth();
    }
    public Board2D(int width, int height, boolean periodic, Rule rule, Nucleation nucleation, float lVal, int rVal, int values, GrainGrowth grainGrowth){
        this(width,height,periodic,rule,nucleation,lVal,rVal,values,grainGrowth,0.0f);
    }
    public Board2D(int width, int height, boolean periodic, Rule rule, Nucleation nucleation, float lVal, int rVal, int values, GrainGrowth grainGrowth,float radius){
        this(width,height,false,rule);
        //this.radiusNucleation = radius;
        this.isPeriodic = periodic;

        derp = new Cell2D(rule,0,0);
        derp.setState(0);

        this.values = values;
        this.growthType = grainGrowth;
        activeCells = new HashSet<>();
        setGrowthType(growthType,radius);
        switch(nucleation){
            case homogeneous:
                homogeneousNucleation((int)lVal, rVal);
                break;
            case radius:
                radiusNucleation(lVal, rVal);
                break;
            case banned:
                bannedNucleation();
                break;
            case random:
            default:
                randomNucleation((int)lVal);
        }
        history = new ArrayList<>();
        activeCells.remove(derp);


    }

    public void setMonteCarloRule(MonteCarloRule mcRule){
        this.mcRule = mcRule;
    }

    public int[][] updateEnergy(){
        int minE=1, maxE=1;
        int[][] returnVale = new int[height][width];
        for(int i = 0; i<height;i++){
            for (int j=0;j<width;j++){
                returnVale[i][j] = (int)((Cell2D)cells.get(i*width+j)).setEnergy(mcRule);//TODO int cast
                if(returnVale[i][j] < minE){
                    minE = returnVale[i][j];
                }else {
                    if(returnVale[i][j] > maxE)
                        maxE = returnVale[i][j];
                }
            }
        }
        energyRange = new Pair<>(minE, maxE);
        return returnVale;
    }

    public int[][] monteCarloIteration(){
        ArrayList<Cell> copyList = new ArrayList<>(cells);
        Collections.shuffle(copyList);
        for(Cell c: copyList){
            if(growthType == GrainGrowth.pent_random){
                int x= (int)((Cell2D)c).getX();
                int y= (int)((Cell2D)c).getY();
                c.setNeighbours(getPentGrowthNeighbours(x,y,GrainGrowth.randomPent()));
            }else{
                if(growthType == GrainGrowth.hex_random) {
                    int x = (int) ((Cell2D) c).getX();
                    int y = (int) ((Cell2D) c).getY();
                    c.setNeighbours(getHexGrowthNeighbours(x, y, GrainGrowth.randomBool()));
                }
            }
            ((Cell2D)c).setNextState(mcRule.ifChange((Cell2D)c));
            c.updateState();
        }
        updateStates();
        energy = updateEnergy();
        return energy;
    }
    public int[][] monteCarloIteration2(){
        ArrayList<Cell> copyList = new ArrayList<>(cells);
        Random random = new Random(System.currentTimeMillis());
        while(copyList.size()!=0){
            Cell2D c = (Cell2D)copyList.remove(random.nextInt(copyList.size()));
            c.setNextState(mcRule.ifChange(c));
            c.updateState();
        }
        //for (Cell c: cells){
        //}
        updateStates();
        energy = updateEnergy();
        return energy;
    }
    public int[][] monteCarloIteration3(){
        for(Cell c: cells){
            ((Cell2D)c).setNextState(mcRule.ifChange((Cell2D)c));
            c.updateState();
        }
        //for (Cell c: cells){
        //}
        updateStates();
        energy = updateEnergy();
        return energy;
    }
    public int[][] getEnergy() {
        if(energy == null)
            energy = updateEnergy();
        return energy;
    }

    public Pair<Integer, Integer> getEnergyRange() {
        return energyRange;
    }

    public int[][] getStates() {
        return history.get(history.size()-1);
    }
    public int[][] updateStates() {
        int[][] returnVale = new int[height][width];
        for(int i = 0; i<height;i++){
            for (int j=0;j<width;j++){
                returnVale[i][j] = cells.get(i*width+j).getState();
            }
        }
        if(history.size() > 5){
            history.clear();
        }
        history.add(returnVale);
        return returnVale;
    }


    public void setGrowthType(GrainGrowth growthType, float radius) {
        this.growthType = growthType;
        switch (growthType){
            case hex_left:
                hexGrowth(true);
                break;
            case hex_right:
                hexGrowth(false);
                break;
            case hex_random:
            case pent_random:
                break;
            case Moore:
                mooreGrowth();
                break;
            case radius:
                radiusGrowth(radius);
                break;
            case vonNeuman:
                vonNeumanGrowth();
                break;
            default:
                pentGrowth(growthType);
        }
    }

    public void setCells(int x, int y, int[][] state){
        for(int i=0; i<state[0].length;i++){
            for(int j=0; j<state.length;j++){
                getCellAt(x+j, y+i).setState(state[j][i]);
            }
        }
    }
    public void isPeriodic(boolean isPeriodic){
        this.isPeriodic = isPeriodic;
    }
    public void nextTimeStep(){

        /*
        if(growthType != GrainGrowth.hex_random && growthType != GrainGrowth.pent_random){
            for(Cell c: cells){
                c.nextState();
            }
        }else{
            if(growthType == GrainGrowth.hex_random){
                for(Cell c: cells){
                    int[] position = {
                            (int)((Cell2D)c).getX(),
                            (int)((Cell2D)c).getY()
                    };
                    c.setNeighbours(getHexGrowthNeighbours(position[0],position[1],RANDOM.nextBoolean()));
                }
            }else{
                for(Cell c: cells){
                    int[] position = {
                            (int)((Cell2D)c).getX(),
                            (int)((Cell2D)c).getY()
                    };
                    c.setNeighbours(getPentGrowthNeighbours(position[0],position[1],GrainGrowth.randomPent()));
                }
            }
        }
        for(Cell c: cells){
            c.updateState();
        }
        activeCells.remove(derp);*/
        for(Cell c: cells){
            c.nextState();
        }
        for(Cell c: cells){
            c.updateState();
        }
    }
    public List<int[][]> getHistory(){
        return history;
    }
    public int[][] nextTimeStep(boolean modern){
        if(modern){
            return asyncUpdate();
        }
        int[][] returnVale = new int[height][width];
        for(int i = 0; i<height;i++){
            for (int j=0;j<width;j++){
                returnVale[i][j] = cells.get(i*width+j).getState();
            }
        }
        Set<Cell> tmp = activeCells;
        activeCells = new HashSet<>();

        if (growthType == GrainGrowth.hex_random || growthType == GrainGrowth.pent_random) {
            if(growthType == GrainGrowth.hex_random){
                for(Cell c: cells){
                    int[] position = {
                            (int)((Cell2D)c).getX(),
                            (int)((Cell2D)c).getY()
                    };
                    c.setNeighbours(getHexGrowthNeighbours(position[0],position[1],RANDOM.nextBoolean()));
                }
            }else{
                for(Cell c: cells){
                    int[] position = {
                            (int)((Cell2D)c).getX(),
                            (int)((Cell2D)c).getY()
                    };
                    c.setNeighbours(getPentGrowthNeighbours(position[0],position[1],GrainGrowth.randomPent()));
                }
            }
        }
        for(Cell c: tmp){
            c.nextState();
            for(Cell n: c.getNeighbours()){
                if(n.getState() == 0){
                    activeCells.add(n);
                }
            }
        }
        activeCells.remove(derp);

        for(Cell c: tmp){
            c.updateState();
        }
        if(history.size() > 5){
            history.clear();
        }
        history.add(returnVale);
        return null;
    }
    private int[][] asyncUpdate(){

        if(activeCells.size() == 0){
            return null;
        }
        int tasks = activeCells.size() / CELLS_PER_TASK;
        if(activeCells.size() % CELLS_PER_TASK != 0){
            tasks++;
        }
        List<Cell> activeCellList = new ArrayList<>(activeCells);
        List<FutureTask<Set<Cell>>> taskList = new ArrayList<>(tasks);
        for(int i = 0; i<tasks; i++){
            CellCalculator tmp;
            //if((i+1)*CELLS_PER_TASK > activeCellList.size()){
            if(i== (tasks-1)){
                tmp = new CellCalculator(activeCellList.subList(i*CELLS_PER_TASK, activeCellList.size()));
            }else{
                tmp = new CellCalculator(activeCellList.subList(i*CELLS_PER_TASK, (i+1)*CELLS_PER_TASK));
            }
            FutureTask<Set<Cell>> task = new FutureTask<>(tmp);
            task.run();
            taskList.add(task);
        }

        int[][] returnVale = new int[height][width];
        for(int i = 0; i<height;i++){
            for (int j=0;j<width;j++){
                returnVale[i][j] = cells.get(i*width+j).getState();
            }
        }
        if(history.size() > 5){
            history.clear();
        }
        history.add(returnVale);




        activeCells = new HashSet<>();
        for(FutureTask<Set<Cell>> task: taskList){
            try {
                activeCells.addAll(task.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        activeCells.remove(derp);


        for(Cell c: activeCellList){
            c.updateState();
        }

        return returnVale;
    }
    public List<Cell> getCells(){
        return this.cells;
    }

    public Cell getCellAt(int x, int y){
        if(!isPeriodic){
            if(x >= width || x<0 || y>=height || y <0)
                return derp;
        }

        while(x<0 || y<0){
            x+=width;
            y+=height;
        }
        x=x%width;
        y=y%height;
        return cells.get((y*width) + x);
    }

    public int[] getPositionOf(Cell c){
        int[] returnVale = new int[2];
        int n = cells.indexOf(c);
        returnVale[0] = n%width;
        returnVale[1] = n/width;
        return returnVale;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public int getValues(){return values;}
    public boolean isActive(){
        return activeCells.size() != 0;
    }
    public void setActive(boolean active){
        if(!active){
            activeCells = new HashSet<>();
        }
    }
    @Override
    public String toString(){
        String returnVale="";
        int i =0;
        for(Cell c: this.cells){
            if(c.getState() == 0){
                returnVale= returnVale.concat(" ");
            }else{
                returnVale= returnVale.concat(c.getState()+"");
            }
            i++;
            if(i %width == 0){
                returnVale= returnVale.concat("\n");
            }
        }
        return returnVale;
    }

    //-----------------------------------------------GrainGrowth - neighbours
    private void mooreGrowth(){
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
    };
    private List<Cell> getMooreNeighbours(int i, int j){
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
        return Arrays.asList(tmp);
    }
    private void vonNeumanGrowth(){
        for(int i = 0; i<height ;i++){
            for(int j = 0; j< width ; j++){
                Cell[] tmp = {
                        getCellAt(j+1,i),
                        getCellAt(j,i+1),
                        getCellAt(j,i-1),
                        getCellAt(j-1,i)
                };
                getCellAt(j,i).setNeighbours(tmp);
            }
        }
    };
    private void hexGrowth(boolean left){
        for(int i = 0; i<height ;i++){
            for(int j = 0; j< width ; j++){
                getCellAt(j,i).setNeighbours(getHexGrowthNeighbours(j,i,left));
            }
        }
    };
    private void pentGrowth(GrainGrowth g){
        for(int i = 0; i<height ;i++){
            for(int j = 0; j< width ; j++){
                getCellAt(j,i).setNeighbours(getPentGrowthNeighbours(j,i,g));
            }
        }
    };
    private void radiusGrowth(float radius){
        int intRadius = (int)radius+1;
        for(int i = 0; i<height ;i++){
            for(int j = 0; j< width ; j++){
                List<Cell> neighbours = new LinkedList<>();
                Cell2D target = (Cell2D)getCellAt(j,i);
                for(int k =i-intRadius; k<i+intRadius;k++){
                    for(int l =j-intRadius; l<j+intRadius;l++){
                        Cell2D potentialNeighbour = (Cell2D)getCellAt(l,k);
                        float potentialX = potentialNeighbour.getX();
                        float potentialY = potentialNeighbour.getY();
                        if(isPeriodic){
                            if(l < 0) potentialX -=width;
                            if(l >= width) potentialX +=width;
                            if(k < 0)  potentialY -=height;
                            if(k >= height) potentialY +=height;
                        }
                        if( getDistance(target.getX(), target.getY(),potentialX , potentialY ) <= radius){
                            neighbours.add(potentialNeighbour);
                        }
                    }
                }
                Cell[] tmp = new Cell[neighbours.size()];
                neighbours.toArray(tmp);
                target.setNeighbours(tmp);
            }
        }
    }

    //DONE radius periodic
    public void activeAll(){
        this.activeCells.addAll(cells);
    }
    private Cell[] getHexGrowthNeighbours(int j, int i, boolean left){
        if (left){
            return new Cell[]{
                getCellAt(j+1,i),
                getCellAt(j+1,i-1),
                getCellAt(j,i+1),
                getCellAt(j,i-1),
                getCellAt(j-1,i+1),
                getCellAt(j-1,i)
            };
        }
        else{
            return new Cell[]{
                    getCellAt(j+1,i),
                    getCellAt(j+1,i+1),
                    getCellAt(j,i+1),
                    getCellAt(j,i-1),
                    getCellAt(j-1,i),
                    getCellAt(j-1,i-1)
            };
        }
    }
    private Cell[] getPentGrowthNeighbours(int j, int i, GrainGrowth g){
        switch (g) {
            case pent_right:
                return new Cell[]{
                        getCellAt(j + 1, i + 1),
                        getCellAt(j + 1, i),
                        getCellAt(j + 1, i - 1),
                        getCellAt(j, i + 1),
                        getCellAt(j, i - 1)
                };
            case pent_left:
                return new Cell[]{
                        getCellAt(j - 1, i + 1),
                        getCellAt(j - 1, i),
                        getCellAt(j - 1, i - 1),
                        getCellAt(j, i + 1),
                        getCellAt(j, i - 1)
                };
            case pent_bottom:
                return new Cell[]{
                        getCellAt(j + 1, i + 1),
                        getCellAt(j + 1, i),
                        getCellAt(j, i + 1),
                        getCellAt(j - 1, i + 1),
                        getCellAt(j - 1, i)
                };
            case pent_top:
                default:
                return new Cell[]{
                        getCellAt(j + 1, i - 1),
                        getCellAt(j + 1, i),
                        getCellAt(j, i - 1),
                        getCellAt(j - 1, i - 1),
                        getCellAt(j - 1, i)
                };
        }
    };


    //-----------------------------------------------Nucleation
    private void homogeneousNucleation(int x,int y){
        int gapX = width/x;
        int gapY = height/y;
        List<Integer> tmp = new LinkedList<>();
        if(values == 0) values = x*y;
        for(int i=1;i<values+1;i++){
            tmp.add(i);
        }
        Random random = new Random(System.currentTimeMillis());
        for(int i =0; i<x;i++){
            for(int j=0; j<y;j++){
                int state;
                if(tmp.size()!=0){
                    state = tmp.remove(random.nextInt(tmp.size()));
                }else {
                    state=random.nextInt(values);
                }
                Cell2D target =  (Cell2D)getCellAt(i*gapX,j*gapY);
                target.setState(state);
                switch(growthType){
                    case pent_random:
                    case pent_bottom:
                    case pent_left:
                    case pent_right:
                    case pent_top:
                        //getCellAt((int)x,(int)y).setNeighbours(getPentGrowthNeighbours((int)x,(int)y, growthType));
                        //break;
                    case hex_random:
                        //getCellAt((int)x,(int)y).setNeighbours(getHexGrowthNeighbours((int)x,(int)y,random.nextBoolean()));
                        activeCells.addAll(getMooreNeighbours((int)target.getY(),(int)target.getX()));
                        break;
                    default:
                        activeCells.addAll(Arrays.asList(getCellAt((int)target.getX(),(int)target.getY()).getNeighbours()));
                }
            }
        }
    }
    private void radiusNucleation(float radius, int number){
        Random random = new Random(System.currentTimeMillis());
        List<Integer> tmp = new LinkedList<>();
        List<PointF> randed = new ArrayList<>();
        if(values == 0) values = number;
        for(int i=1;i<values+1;i++){
            tmp.add(i);
        }
        outerFor:
        for(int i=0;i<number;i++){
            float x = random.nextFloat()*width;
            float y = random.nextFloat()*height;
            for(PointF p: randed){
                if(getDistance(p.x,p.y,x,y)<=radius){
                    i--;
                    continue outerFor;
                }
            }
            int state;
            if(tmp.size()!=0){
                state = tmp.remove(random.nextInt(tmp.size()));
            }else {
                state=random.nextInt(values);
            }
            getCellAt((int)x,(int)y).setState(state);
            switch(growthType){
                case pent_random:
                case pent_bottom:
                case pent_left:
                case pent_right:
                case pent_top:
                case hex_random:
                    activeCells.addAll(getMooreNeighbours((int)y,(int)x));
                    break;
                default:
                    activeCells.addAll(Arrays.asList(getCellAt((int)x,(int)y).getNeighbours()));
            }
            //activeCells.addAll(Arrays.asList(getCellAt((int)x,(int)y).getNeighbours()));
            randed.add(new PointF(x,y));
        }
    }
    private void randomNucleation(int number){
        Random random = new Random(System.currentTimeMillis());
        List<Integer> tmp = new LinkedList<>();
        if(values == 0) values = number;
        for(int i=1;i<values+1;i++){
            tmp.add(i);
        }
        for(int i=0;i<number;i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if(getCellAt(x,y).getState() == 0){
                int state;
                if(tmp.size()!=0){
                    state = tmp.remove(random.nextInt(tmp.size()));
                }else {
                    state=random.nextInt(values);
                }
                getCellAt(x,y).setState(state); switch(growthType){
                    case pent_random:
                    case pent_bottom:
                    case pent_left:
                    case pent_right:
                    case pent_top:
                        //getCellAt((int)x,(int)y).setNeighbours(getPentGrowthNeighbours((int)x,(int)y, growthType));
                        //break;
                    case hex_random:
                        //getCellAt((int)x,(int)y).setNeighbours(getHexGrowthNeighbours((int)x,(int)y,random.nextBoolean()));
                        activeCells.addAll(getMooreNeighbours((int)y,(int)x));
                        break;
                    default:
                        activeCells.addAll(Arrays.asList(getCellAt((int)x,(int)y).getNeighbours()));
                }
            }else{
                i--;
            }
        }
    }
    private void bannedNucleation(){

    }

    private float getDistance(float x1, float y1, float x2, float y2){
        return (float)Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
    }
    private class CellCalculator implements Callable<Set<Cell>> {
        List<Cell> myCells;


        CellCalculator(List<Cell> cells){
            this.myCells = cells;
        }

        @Override
        public Set<Cell> call() throws Exception {
            Set<Cell> activedCells= new HashSet<>();

            if (growthType == GrainGrowth.hex_random || growthType == GrainGrowth.pent_random) {
                if(growthType == GrainGrowth.hex_random){
                    for(Cell c: myCells){
                        int[] position = {
                                (int)((Cell2D)c).getX(),
                                (int)((Cell2D)c).getY()
                        };
                        c.setNeighbours(getHexGrowthNeighbours(position[0],position[1],RANDOM.nextBoolean()));
                    }
                }else{
                    for(Cell c: myCells){
                        int[] position = {
                                (int)((Cell2D)c).getX(),
                                (int)((Cell2D)c).getY()
                        };
                        c.setNeighbours(getPentGrowthNeighbours(position[0],position[1],GrainGrowth.randomPent()));
                    }
                }
            }
            for(Cell c: myCells){
                c.nextState();
                 if(((Cell2D)c).getNextState() == 0) continue;
                switch (growthType){
                    case pent_top:
                    case pent_bottom:
                    case pent_left:
                    case pent_right:
                    case pent_random:
                    case hex_left:
                    case hex_right:
                    case hex_random:
                    int x = (int)((Cell2D)c).getX();
                    int y = (int)((Cell2D)c).getY();
                    for(Cell n: getMooreNeighbours(y, x)){
                        if(n.getState() == 0){
                            activedCells.add(n);
                        }
                    }
                    break;
                    default:
                    for(Cell n: c.getNeighbours()){
                        if(n.getState() == 0){
                            activedCells.add(n);
                        }
                    }
                }

            }
            return activedCells;
        }
    }
}