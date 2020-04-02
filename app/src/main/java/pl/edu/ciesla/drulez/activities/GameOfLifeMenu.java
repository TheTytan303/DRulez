package pl.edu.ciesla.drulez.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import pl.edu.ciesla.drulez.R;

public class GameOfLifeMenu extends AppCompatActivity {
    int size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_life_menu);
        final View view= findViewById(R.id.agolm_continue);
        view.post(new Runnable() {
            @Override
            public void run() {
                int defaultX = view.getWidth();
                View view = findViewById(R.id.agolm_main);
                int defaultY = view.getHeight();
                System.out.println("X=" + defaultX+ " | Y="+defaultY);
                defaultY -= findViewById(R.id.agolm_first_row).getHeight();
                System.out.println("X=" + defaultX+ " | Y="+defaultY);
                System.out.println();
                defaultX = defaultX/25;
                defaultY = defaultY/25;
                EditText et = findViewById(R.id.agolm_x_et);
                et.setText(defaultX+"");
                et = findViewById(R.id.agolm_y_et);
                et.setText(defaultY+"");
                //size = view.getWidth();
            }
        });
    }

    public void goOn(View view){
        size = findViewById(R.id.agolm_main).getWidth();
        int x,y, rule;
        view.setClickable(false);
        EditText et = findViewById(R.id.agolm_x_et);
        x = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.agolm_y_et);
        y = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.agolm_rule_et);
        rule = Integer.parseInt(et.getText().toString());
        Switch sw =findViewById(R.id.agolm_ifRandom);
        boolean isRandom = sw.isChecked();
        size = size/x;
        Intent contin = new Intent(this, GameOfLife.class);
        contin.putExtra("x", x);
        contin.putExtra("y", y);
        contin.putExtra("rule", rule);
        contin.putExtra("size", size);
        contin.putExtra("isRandom", isRandom);
        view.setClickable(true);
        startActivity(contin);
    }
}
