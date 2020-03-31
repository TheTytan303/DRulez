package pl.edu.ciesla.drulez.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
                defaultX = defaultX/25;
                EditText et = findViewById(R.id.agolm_x_et);
                et.setText(defaultX+"");
                et = findViewById(R.id.agolm_y_et);
                et.setText(defaultX+"");
                size = view.getWidth();
            }
        });
    }

    public void goOn(View view){
        int x,y, rule;
        EditText et = findViewById(R.id.agolm_x_et);
        x = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.agolm_y_et);
        y = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.agolm_rule_et);
        rule = Integer.parseInt(et.getText().toString());

        size = size/x;
        Intent contin = new Intent(this, GameOfLife.class);
        contin.putExtra("x", x);
        contin.putExtra("y", y);
        contin.putExtra("rule", rule);
        contin.putExtra("size", size);
        startActivity(contin);
    }
}
