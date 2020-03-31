package pl.edu.ciesla.drulez.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.autofill.VisibilitySetterAction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.View.CellViewAdapter;
import pl.edu.ciesla.drulez.View.TouchImageView;
import pl.edu.ciesla.drulez.core.Board.Board1D;
import pl.edu.ciesla.drulez.core.Board.Board2D;
import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.rule.GOLRule;

public class GameOfLife extends AppCompatActivity implements CellViewAdapter.CellViewListener {
    Board2D board;
    int x,y, rule;
    int size;
    //Timer timer;
    Runnable task;
    private RecyclerView mainRecycler;
    RecyclerView.Adapter mainRecyclerAdapter;
    RecyclerView.LayoutManager mainRecyclerLayoutManager;
    //GridLayoutManager mainRecyclerLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_life);
        TouchImageView tiv = findViewById(R.id.agol_main_image);
        tiv.setZoom(0.9f);
        tiv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchImageView tiv = (TouchImageView) v;
                
                return false;
            }
        });
        x = getIntent().getIntExtra("x", 50);
        y = getIntent().getIntExtra("y",50);
        rule = getIntent().getIntExtra("rule",4139);
        size = getIntent().getIntExtra("size",10);
        board = new Board2D(x,y,true,new GOLRule(rule));
        board.setCells(1,1,new int[][]{{1,0,0},{1,1,0},{0,0,1}});
    }

    @Override
    public void onCellsSwiped(Cell[] cells) {

    }

    @SuppressLint("SetTextI18n")
    public void goNext(View v){

        EditText et = findViewById(R.id.agol_times_et);
        int times = 0;
        try{
            times = Integer.parseInt(et.getText().toString());
            et.setText((times-1)+"");
        }catch(Exception ignore){};
        if(times == 0){
            nextStep();
            stop(findViewById(R.id.agol_stop_bt));
            return;
        }

        v.setVisibility(View.GONE);
        v = findViewById(R.id.agol_stop_bt);
        task = new Runnable() {
            @Override
            public void run() {
                ((Button)findViewById(R.id.agol_start_bt)).performClick();
            }
        };
        v.postDelayed(task, 500);
        v.setVisibility(View.VISIBLE);

        nextStep();
    }
    public void stop(View v){
        v.setVisibility(View.GONE);
        v = findViewById(R.id.agol_start_bt);
        v.setVisibility(View.VISIBLE);
        EditText et = findViewById(R.id.agol_times_et);
        et.setText("0");
    }

    private void nextStep(){
        ImageView iv = findViewById(R.id.agol_main_image);
        DrawTask dt = new DrawTask(board,iv,size);
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        board.nextTimeStep();
    }


    private void popUpPaster(){

    }
    class DrawTask extends AsyncTask<Void, Bitmap,Bitmap> {

        Bitmap bmp;
        ImageView iv;
        Board2D board;
        int sizeX, sizeY;
        public DrawTask( Board2D board, ImageView iv,int size){
            this.iv = iv;
            //this.bmp = bmp;
            this.board = board;
            this.sizeX = board.getWidth()*size;
            this.sizeY = board.getHeight()*size;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            iv.setImageBitmap(values[0]);
            super.onProgressUpdate(values);
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Paint black = new Paint();
            black.setColor(Color.BLACK);
            black.setStyle(Paint.Style.FILL);
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            white.setStyle(Paint.Style.FILL);
            bmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmp);
            int i=0;
            int j=0;
            //System.out.println(board);
            for(Cell cell: board.getCells()){
                Rect rect = new Rect(i*size,j*size,(i+1)*size,(j+1)*size);
                i++;
                if(cell.getState() == 0){
                    //System.out.println("W");
                    canvas.drawRect(rect,white);
                }else{
                    canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,black);
                    //System.out.println("i="+i+" | j="+j);
                    //System.out.println("rect: " +rect);
                }
                if(i==board.getWidth()){
                    //System.out.println("size: " +size + " | j: " + j);
                    j++;
                    i=0;
                    publishProgress(bmp);
                }
            }
            return bmp;
        }
    }
}

/*

        mainRecycler = findViewById(R.id.agol_main_recycler);
        mainRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int space = 0;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
                outRect.top = space;
            }
        });


        //ImageView iv = findViewById(R.id.cv_iv);
        mainRecyclerAdapter = new CellViewAdapter(board,this,size);
        mainRecyclerLayoutManager = new GridLayoutManager(this, board.getWidth());

        mainRecycler.setAdapter(mainRecyclerAdapter);
        mainRecycler.setLayoutManager(mainRecyclerLayoutManager);
        mainRecyclerAdapter.notifyDataSetChanged();

 *//*

 for(int i=1; i<=times;i++){
            mainRecyclerAdapter.notifyDataSetChanged();
            board.nextTimeStep();
            try {
                if(i != times)
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */