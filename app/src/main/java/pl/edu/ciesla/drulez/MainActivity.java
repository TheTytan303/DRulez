package pl.edu.ciesla.drulez;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pl.edu.ciesla.drulez.View.TouchImageView;
import pl.edu.ciesla.drulez.activities.D1Viz;
import pl.edu.ciesla.drulez.activities.GameOfLife;
import pl.edu.ciesla.drulez.activities.GameOfLifeMenu;
import pl.edu.ciesla.drulez.core.Board.Board1D;
import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell1D;
import pl.edu.ciesla.drulez.core.rule.GOLRule;
import pl.edu.ciesla.drulez.core.rule.Rule1D;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void d1viz(View view){
        Intent contin = new Intent(this, D1Viz.class);
        startActivity(contin);
    }
    public void gol(View v){
        Intent contin = new Intent(this, GameOfLifeMenu.class);
        startActivity(contin);

    }
}
