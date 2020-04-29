package pl.edu.ciesla.drulez.View.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.View.TouchImageView;
import pl.edu.ciesla.drulez.core.Board.Board2D;
import pl.edu.ciesla.drulez.core.rule.CARule;
import pl.edu.ciesla.drulez.core.rule.MonteCarloRule;

public class CellarAutomata extends AppCompatActivity {


    Board2D board;
    TouchImageView tiv;
    Map<Integer, Paint> colors;
    Bitmap bmp;
    DrawTask task;
    long timeMilis;
    int size = 10;
    int iterations = 0;
    boolean update;
    boolean isEnergy;
    MonteCarloRule mcRule;
    MonteCarloTask mcTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_automata);
        Board2D.Nucleation nucleation = (Board2D.Nucleation) getIntent().getSerializableExtra("nucleation");
        Board2D.GrainGrowth gg = (Board2D.GrainGrowth) getIntent().getSerializableExtra("grainGrowth");
        int values = getIntent().getIntExtra("values",0);
        float lValue = getIntent().getFloatExtra("lValue",3.5f);
        int rValue = getIntent().getIntExtra("rValue",5);
        int width = getIntent().getIntExtra("width",5);
        int height = getIntent().getIntExtra("height",5);
        float radius =  getIntent().getFloatExtra("growthRadius",1.5f);
        boolean isPeriodic = getIntent().getBooleanExtra("periodic",true);

        board = new Board2D(width,height,isPeriodic, CARule.getInstance(),nucleation,lValue,rValue,values,gg,radius);

        int[] X;
        int[] Y;
        if(nucleation == Board2D.Nucleation.banned){

            X = getIntent().getIntArrayExtra("X");
            Y = getIntent().getIntArrayExtra("Y");
            Random random = new Random(System.currentTimeMillis());
            List<Integer> tmp = new LinkedList<>();
            if(values == 0) values = X.length;
            for(int i=1;i<values+1;i++){
                tmp.add(i);
            }

            for(int i =0; i<X.length;i++){
                int state;
                if(tmp.size()!=0){
                    state = tmp.remove(random.nextInt(tmp.size()));
                }else {
                    state=random.nextInt(values);
                }
                board.getCellAt(X[i]/10, Y[i]/10).setState(state);
                board.activeAll();
            }
        }
        //Set paints
        colors = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for(int i=1;i<board.getValues()+1;i++){
            Paint tmp = new Paint();
            tmp.setColor(Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256)));
            tmp.setStyle(Paint.Style.FILL);
            colors.put(i,tmp);
        }

        tiv = findViewById(R.id.aca_main_image);
        tiv.setImageBitmap(bmp);
        tiv.setMinZoom(0.5f);
        tiv.setMaxZoom(10);
        tiv.setZoom(1);
        board.nextTimeStep(false);
        timeMilis = System.currentTimeMillis();
        simulate();
        update = true;
        updateImageView();

        mcRule = new MonteCarloRule(1, 0.6f);
        board.setMonteCarloRule(mcRule);
        this.isEnergy = false;
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(task!=null)
            task.cancel(true);
        board.setActive(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.montecarlo_menu, menu);
        menu.getItem(2).setCheckable(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mc_energy_sw:
                energyShow(item);
                break;
            case R.id.mc_iterate:
                board.monteCarloIteration();
                if(isEnergy){
                    showEnergy();
                }else {
                    showGrains();
                }
                break;
            case R.id.mc_simulate:
                setUpMCSimulation();
                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }

    private void setUpMCSimulation(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MonteCarlo setup");
        LayoutInflater inflater = this.getLayoutInflater();
        final View v  = inflater.inflate(R.layout.monte_carlo_setup,null);
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float radius = 1.5f;
                try{
                    EditText et = v.findViewById(R.id.mc_setup_iterations_et);
                    iterations = Integer.parseInt(et.getText().toString());
                    et = v.findViewById(R.id.mc_setup_radius_et);
                    radius = Float.parseFloat(et.getText().toString());
                }catch (Exception ignore){};
                Switch sw = v.findViewById(R.id.mc_setup_periodic_sw);
                board.isPeriodic(sw.isChecked());
                RadioGroup rg = v.findViewById(R.id.mc_setup_neighbours);

                switch (rg.getCheckedRadioButtonId()){
                    case R.id.mc_setup_neighbours_moore:
                        board.setGrowthType(Board2D.GrainGrowth.Moore, 0);
                    break;
                    case R.id.mc_setup_neighbours_vonneumann:
                        board.setGrowthType(Board2D.GrainGrowth.vonNeuman, 0);
                    break;
                    case R.id.mc_setup_neighbours_hex:
                        board.setGrowthType(Board2D.GrainGrowth.hex_random, 0);
                        break;
                    case R.id.mc_setup_neighbours_pent:
                        board.setGrowthType(Board2D.GrainGrowth.pent_random, 0);
                        break;
                    case R.id.mc_setup_neighbours_radius:
                        board.setGrowthType(Board2D.GrainGrowth.radius, radius);
                        break;
                }
                simulateCarlo();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void energyShow(MenuItem item){
        if(item.isChecked()){
            showGrains();
            item.setChecked(false);
            this.isEnergy = false;
        }else{
            showEnergy();
            item.setChecked(true);
            this.isEnergy = true;
        }
    }
    private void showEnergy(){
        if(task!=null)
            task.cancel(true);
        int[][] energy = board.getEnergy();
        Map<Integer, Paint> scale = new HashMap<>(8);
        for(int i = board.getEnergyRange().first; i<= board.getEnergyRange().second; i++){
            scale.put(i,getPaint(board.getEnergyRange().first,board.getEnergyRange().second,i));
        };
        this.task = new DrawTask(energy,tiv,size,scale);
        System.out.println(scale.size());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void showGrains(){
        if(task!=null)
        task.cancel(true);
        this.task = new DrawTask(board.getStates(),tiv,size, colors);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private Paint getPaint(int max, int min, int val){
        Paint returnVale = new Paint();
        val = val - min;
        max = max - min;
        float hue = (float)val / (float) max;
        hue*=120;
        float[] color = {hue, 1f, 1f};
        returnVale.setColor(Color.HSVToColor(color));
        returnVale.setStyle(Paint.Style.FILL);
        return returnVale;
    }

    public void simulateCarlo(){
        if(iterations > 0){
            this.mcTask = new MonteCarloTask(board);
            mcTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            iterations--;
        }
    }

    public void simulate(){
        if(board.isActive()){
            StepTask stepTask = new StepTask(board);
            stepTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            long tmp = System.currentTimeMillis() - timeMilis;
            System.out.println("Czas: " + tmp/1000 + "s");
            updateImageView();
            update = false;
            board.setGrowthType(Board2D.GrainGrowth.Moore,1);
        }
    }
    public void updateImageView(){
        if(!update) return;
        int[][] timeStep;
        int last = board.getHistory().size()-1;
        //System.out.println("Last One: " + last + " || size: " + 10);
        timeStep = board.getHistory().get(last);
        task = new DrawTask(timeStep,tiv,size, colors);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class StepTask extends AsyncTask<Void, Void, int[][]> {

        Board2D board;
        public StepTask( Board2D board){
            this.board = board;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //iv.setImageBitmap(bmp);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(int[][] ints) {
            if(!isCancelled()){
                simulate();
            }
            super.onPostExecute(ints);
        }

        @Override
        protected int[][] doInBackground(Void... voids) {
            int[][] returnVale = board.nextTimeStep(true);
            return returnVale;
        }
    }
    class MonteCarloTask extends AsyncTask<Void, Void, int[][]>{

        Board2D board;
        public MonteCarloTask( Board2D board){
            this.board = board;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //iv.setImageBitmap(bmp);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(int[][] ints) {
            if(!isCancelled()){
                if(isEnergy){
                    showEnergy();
                }else {
                    showGrains();
                }
                simulateCarlo();
            }
            super.onPostExecute(ints);
        }

        @Override
        protected int[][] doInBackground(Void... voids) {
            int[][] returnVale = board.monteCarloIteration();
            return returnVale;
        }

    }
    class DrawTask extends AsyncTask<Void, Bitmap, Bitmap> {

        ImageView iv;
        int[][] board;
        Bitmap bmp;
        int size;
        int sizeX, sizeY;
        Map<Integer, Paint> colors;
        public DrawTask(int[][] board, ImageView iv, int size, Map<Integer, Paint> colors){
            this.iv = iv;
            iv.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
            this.bmp = drawable.getBitmap();
            this.size = size;
            this.board = board;
            this.sizeX = board[0].length*size;
            this.sizeY = board.length*size;
            this.colors = colors;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            //iv.setImageBitmap(bmp);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iv.setImageBitmap(bmp);
            updateImageView();
            //iv.invalidate();
            super.onPostExecute(bmp);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            System.out.println("Draw in background");
            bmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            for(int i=0; i<board.length;i++){
                for(int j=0;j<board[0].length;j++){
                    Rect rect = new Rect(j*size,i*size,(j+1)*size,(i+1)*size);
                    if(board[i][j] != 0){
                        canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom, Objects.requireNonNull(this.colors.get(board[i][j])));
                    }
                }
                if(isCancelled())
                    return bmp;
            }
            return bmp;
        }
    }
}
