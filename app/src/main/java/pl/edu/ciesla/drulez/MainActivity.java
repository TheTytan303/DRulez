package pl.edu.ciesla.drulez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pl.edu.ciesla.drulez.View.activities.CellarAutomataSetup;
import pl.edu.ciesla.drulez.View.activities.D1Viz;
import pl.edu.ciesla.drulez.View.activities.GameOfLifeMenu;
import pl.edu.ciesla.drulez.core.Board.Board2D;

public class MainActivity extends AppCompatActivity {
    Board2D board2D;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //board2D = new Board2D(45,45,true, CARule.getInstance(), Board2D.Nucleation.radius,3.5f,10,5, Board2D.GrainGrowth.Moore);
    }

    public void d1viz(View view){
        Intent contin = new Intent(this, D1Viz.class);
        startActivity(contin);
    }
    public void gol(View v){
        Intent contin = new Intent(this, GameOfLifeMenu.class);
        startActivity(contin);
    }
    public void CASetup(View v){
        Intent contin = new Intent(this, CellarAutomataSetup.class);
        startActivity(contin);
    }
}
