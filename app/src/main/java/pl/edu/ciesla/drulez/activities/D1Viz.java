package pl.edu.ciesla.drulez.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.View.TouchImageView;
import pl.edu.ciesla.drulez.core.Board.Board1D;
import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.cell.Cell1D;
import pl.edu.ciesla.drulez.core.rule.Rule1D;

public class D1Viz extends AppCompatActivity {
    DrawTask drawTask;
    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d1_viz);
        ((Switch)findViewById(R.id.ma_cond_sw)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ((Switch)findViewById(R.id.ma_cond_sw)).setText("Periodic");
                }else {
                    ((Switch)findViewById(R.id.ma_cond_sw)).setText("Static");
                }
            }
        });
        final View view= findViewById(R.id.ma_main_image_holder);
        view.post(new Runnable() {
            @Override
            public void run() {
                EditText et ;
                et = findViewById(R.id.ma_time_et);
                int tmp = view.getHeight();
                et.setText(tmp+"", TextView.BufferType.EDITABLE);
                tmp = view.getWidth();
                et = findViewById(R.id.ma_size_et);
                et.setText(tmp+"", TextView.BufferType.EDITABLE);
                }
            });
        }

    public void go(View view){
        if(drawTask != null){
            drawTask.cancel(true);
        }
//get params
        EditText et = findViewById(R.id.ma_rule_et);
        int rule = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.ma_time_et);
        int time = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.ma_size_et);
        int size = Integer.parseInt(et.getText().toString());
        if(size <10 || time <10){
            Toast.makeText(getApplicationContext(),"time and size must be more than 10", Toast.LENGTH_LONG).show();
            return;
        }
//hide keyboard
        InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//set params
        TouchImageView iv = findViewById(R.id.ma_main_image_iv);
        iv.setImageBitmap(bmp);
        iv.setMaxZoom(10);
        iv.setZoom(1);
        Rule1D.getInstance().setSpecyficRule(rule);
        Board1D board = new Board1D(size);
        if(!((Switch)findViewById(R.id.ma_cond_sw)).isChecked()){
            Cell1D tmp = new Cell1D(Rule1D.getInstance());
            tmp.setState(0);
            List<Cell> cells = board.getCells();
            cells.get(0).setNeighbours(new Cell[]{tmp, cells.get(1)});
            cells.get(size-1).setNeighbours(new Cell[]{cells.get(size-2), tmp});
        }
        board.getCells().get(size/2).setState(1);
        drawTask = new DrawTask(bmp,board,iv, time, size);
        drawTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class DrawTask extends AsyncTask<Void, Bitmap,Bitmap> {

        Bitmap bmp;
        TouchImageView iv;
        Board1D board;
        int time, size;
        public DrawTask(Bitmap bmp, Board1D board, TouchImageView iv, int time, int size){
            this.iv = iv;
            this.bmp = bmp;
            this.board = board;
            this.time = time;
            this.size = size;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            iv.setImageBitmap(values[0]);
            super.onProgressUpdate(values);
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            bmp = Bitmap.createBitmap(size,time, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmp);
            for(int i =0 ;i <time;i++){
                if(i%1000 == 0){
                    if(isCancelled()){
                        return null;
                    }
                }
                for(int j=0; j<size;j++){
                    int val = board.getCells().get(j).getState();
                    if(val !=0){
                        canvas.drawPoint(j,i,p);
                    }
                }
                board.nextTimeStep();
                publishProgress(bmp);
            }
            return bmp;
        }
    }
}
