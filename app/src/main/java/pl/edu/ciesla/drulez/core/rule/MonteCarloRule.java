package pl.edu.ciesla.drulez.core.rule;

import java.util.ArrayList;
import java.util.Random;

import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell2D;

import static java.lang.Math.exp;

public class MonteCarloRule implements Rule {
    private Random random;
    private float grainBorderEnergy;
    private float kT;
    public MonteCarloRule(float grainBorderEnergy, float kT){
        this.grainBorderEnergy = grainBorderEnergy;
        this.kT = kT;
        this.random = new Random(System.currentTimeMillis());
    }

    public float getEnergyOf(Cell target){
        return getEnergyOf(target,target.getState());
    }
    public float getEnergyOf(Cell target, int state){
        float returnVale =1;
        for(Cell neighbour : target.getNeighbours()){
            if(neighbour.getState() == 0){
                continue;
            }
            if(neighbour.getState() != state)
                returnVale+=1.0f;
        }
        returnVale*=grainBorderEnergy;
        return returnVale;
    }
    public int ifChange(Cell2D target){
        float energyBefore = getEnergyOf(target);
        int state = getRandomNeighbourState(target);
        if(state == target.getState())
            return target.getState();
        float energyAfter = getEnergyOf(target,state);
        float deltaEnergy = energyAfter - energyBefore;
        if(deltaEnergy < 0)
            return state;
        double eq = -(deltaEnergy / kT);
        double p = Math.exp(eq);
        if(random.nextDouble()<= p)
            return state;
        return target.getState();
    }

    private int getRandomNeighbourState(Cell target){
        ArrayList<Integer> neighbourStates = new ArrayList<>(8);
        for(Cell neighbour : target.getNeighbours()){
            if(neighbour.getState()!=target.getState())
                neighbourStates.add(neighbour.getState());
        }
        if(neighbourStates.size() == 0){
            return target.getState();
        }
        return neighbourStates.get(random.nextInt(neighbourStates.size()));
    }


    @Override
    public int getNextState(Cell target) {
        return 0;
    }
}
