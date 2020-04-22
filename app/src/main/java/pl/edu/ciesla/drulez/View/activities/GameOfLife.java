package pl.edu.ciesla.drulez.View.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.View.StructuresAdapter;
import pl.edu.ciesla.drulez.core.Board.Board2D;
import pl.edu.ciesla.drulez.core.cell.Cell;
import pl.edu.ciesla.drulez.core.rule.GOLRule;

public class GameOfLife extends AppCompatActivity implements StructuresAdapter.boardChangeddListener,
        View.OnTouchListener,
        View.OnDragListener,
        View.OnLongClickListener{
    Board2D board;
    int x,y, rule;
    int size;
    int delay = 1000;
    CopyOnWriteArrayList<Bitmap> bmp;
    Map<String, int[][]> tmp;
    //Timer timer;
    Runnable task;
    private RecyclerView mainRecycler;
    Set<Cell> modified;
    RecyclerView.Adapter mainRecyclerAdapter;
    RecyclerView.LayoutManager mainRecyclerLayoutManager;
    //GridLayoutManager mainRecyclerLayoutManager;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_life);
        modified = new HashSet<>();
        x = getIntent().getIntExtra("x", 50);
        y = getIntent().getIntExtra("y",50);
        rule = getIntent().getIntExtra("rule",4139);
        size = getIntent().getIntExtra("size",10);
        board = new Board2D(x,y,true,new GOLRule(rule));
        //board.setCells(1,1,new int[][]{{0,1,0},{0,1,0},{0,1,0}});
        if(getIntent().getBooleanExtra("isRandom", false)){
            Random random = new Random(System.currentTimeMillis());
            for(Cell c: board.getCells()){
                c.setState(random.nextBoolean() ? 1 : 0);
            }
        }
        mainRecycler = findViewById(R.id.agol_side_menu);
        tmp = new HashMap<>();
        tmp.put("Static",new int[][]{{0,1,1,0},{1,0,0,1},{0,1,1,0}});
        tmp.put("Blinker",new int[][]{{0,1,0},{0,1,0},{0,1,0}});
        tmp.put("Toad",new int[][]{{0,1,1,1},{1,0,0,0}});
        tmp.put("Beacon",new int[][]{{1,1,0,0},{1,1,0,0},{0,0,1,1},{0,0,1,1}});

        //Array of states for 'Pulsar' structure. This array may be taken by 'setCells()' Board2D method
        tmp.put("Pulsar",new int[][]{
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0}
        });
        tmp.put("Glider",new int[][]{{0,1,1},{1,1,0},{0,0,1}});
        tmp.put("HWSS",new int[][]{{0,0,1,1,0,0,0},{1,0,0,0,0,1,0},{0,0,0,0,0,0,1},{1,0,0,0,0,0,1},{0,1,1,1,1,1,1}});
        List<Map.Entry<String, int[][]>> samples = new ArrayList<>(tmp.entrySet());


        mainRecyclerAdapter = new StructuresAdapter(samples, board, this);
        mainRecyclerLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mainRecycler.setAdapter(mainRecyclerAdapter);
        mainRecycler.setLayoutManager(mainRecyclerLayoutManager);
        mainRecyclerAdapter.notifyDataSetChanged();

        bmp = new CopyOnWriteArrayList<>();


        ImageView iv = findViewById(R.id.agol_main_image);
        CopyOnWriteArrayList<ImageView> concurrentIv=new CopyOnWriteArrayList<>();
        concurrentIv.add(iv);
        DrawTask dt = new DrawTask(board,concurrentIv,size, bmp);
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        iv.setOnTouchListener(this);
        iv.setOnDragListener(this);

        findViewById(R.id.agol_time_tv).setOnLongClickListener(this);
    }
    @SuppressLint("SetTextI18n")
    public void goNext(View v){
        EditText et = findViewById(R.id.agol_times_et);
        int times = 0;
        try{
            times = Integer.parseInt(et.getText().toString());
            et.setText((times-1)+"");
        }catch(Exception ignore){}
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
                (findViewById(R.id.agol_start_bt)).performClick();
            }
        };
        v.postDelayed(task, delay);
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
        CopyOnWriteArrayList<ImageView> concurrentIv=new CopyOnWriteArrayList<>();
        concurrentIv.add(iv);
        DrawTask dt = new DrawTask(board,concurrentIv,size, bmp);
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        board.nextTimeStep();
    }

    public void showHideSideBar(View view){
        View sideMenu = findViewById(R.id.agol_side_menu);
        int visibility = sideMenu.getVisibility();
        if(visibility == View.GONE){
            sideMenu.setVisibility(View.VISIBLE);
        }else {
            sideMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBoardChanged() {
        ImageView iv = findViewById(R.id.agol_main_image);
        CopyOnWriteArrayList<ImageView> concurrentIv=new CopyOnWriteArrayList<>();
        concurrentIv.add(iv);
        DrawTask dt = new DrawTask(board,concurrentIv,size, bmp);
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (action){
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                modified.clear();
                return true;
            case MotionEvent.ACTION_OUTSIDE:
                return true;

        }
        int boardX,boardY;
        boardX = x/size;
        boardY = y/size;
        //System.out.println(" /|"+size+"|\\ X=" + boardX+ " | Y="+boardY);
        try{
            Cell target = board.getCellAt(boardX,boardY);
            if (modified.add(target)) {
                int state = target.getState();
                if(state == 0){
                    state = 1;
                }else{
                    state = 0;
                }
                board.getCellAt(boardX,boardY).setState(state);
                //event.getAction();
                onBoardChanged();
            }
        } catch (Exception ignored){}

        return true;
    }
    @Override
    public boolean onDrag(View v, DragEvent event){
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                findViewById(R.id.agol_side_menu).setVisibility(View.GONE);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                System.out.println("EXITED");
                break;
            case  DragEvent.ACTION_DRAG_LOCATION:
                break;
            case DragEvent.ACTION_DROP:
                int x = (int)event.getX();
                int y = (int)event.getY();
                int boardX,boardY;
                boardX = x/size;
                boardY = y/size;
                try{
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    item.getText();
                    boardX -= tmp.get(item.getText()).length/2;
                    boardY -=tmp.get(item.getText())[0].length/2;
                    board.setCells(boardX,boardY,tmp.get(item.getText()));
                    onBoardChanged();
                } catch (Exception ignored){};
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("time ");
        final EditText xET = new EditText(v.getContext());
        xET.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout mainLayout = new LinearLayout(v.getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(xET);
        builder.setView(mainLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    delay = Integer.parseInt(xET.getText().toString());
                }catch (Exception ignore){};

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return false;
    }


    class DrawTask extends AsyncTask<Void, Void,CopyOnWriteArrayList<Bitmap>> {

        CopyOnWriteArrayList<Bitmap> bmp;
        CopyOnWriteArrayList<ImageView> iv;
        Board2D board;
        int sizeX, sizeY;
        public DrawTask( Board2D board, CopyOnWriteArrayList<ImageView> iv,int size,CopyOnWriteArrayList<Bitmap> bmp){
            this.iv = iv;
            this.bmp = bmp;
            this.board = board;
            this.sizeX = board.getWidth()*size;
            this.sizeY = board.getHeight()*size;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(CopyOnWriteArrayList<Bitmap> bitmap) {
            iv.get(0).setImageBitmap(bmp.get(0));
            //iv.invalidate();
            super.onPostExecute(bitmap);
        }

        @Override
        protected CopyOnWriteArrayList<Bitmap> doInBackground(Void... voids) {
            Paint black = new Paint();
            black.setColor(Color.BLACK);
            black.setStyle(Paint.Style.FILL);
            Paint white = new Paint();
            white.setColor(0x20FF00FF);
            white.setStyle(Paint.Style.FILL);
            Bitmap tmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
            bmp.clear();
            bmp.add(tmp);
            final Canvas canvas = new Canvas(bmp.get(0));
            int i=0;
            int j=0;
            //System.out.println(board);
            for(Cell cell: board.getCells()){
                Rect rect = new Rect(i*size,j*size,(i+1)*size,(j+1)*size);
                i++;
                if(cell.getState() == 0){
                    //System.out.println("W");
                    canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,white);
                }else{
                    canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,black);
                    //System.out.println("i="+i+" | j="+j);
                    //System.out.println("rect: " +rect);
                }
                if(i==board.getWidth()){
                    //System.out.println("size: " +size + " | j: " + j);
                    publishProgress();
                    j++;
                    i=0;
                }
            }
            return bmp;
        }
    }
}