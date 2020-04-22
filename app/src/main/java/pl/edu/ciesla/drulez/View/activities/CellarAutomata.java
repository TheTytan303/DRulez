package pl.edu.ciesla.drulez.View.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.HashMap;
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

public class CellarAutomata extends AppCompatActivity {


    Board2D board;
    TouchImageView tiv;
    Map<Integer, Paint> colors;
    Bitmap bmp;
    DrawTask task;
    long timeMilis;
    int size = 10;
    boolean update;
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
        System.out.println("grainGrowth: " + gg);

        board = new Board2D(width,height,isPeriodic, CARule.getInstance(),nucleation,lValue,rValue,values,gg,radius);

        //Set paints
        colors = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for(int i=1;i<board.getValues()+1;i++){
            Paint tmp = new Paint();
            tmp.setColor(Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256)));
            //tmp.setColor(Color.BLACK);
            tmp.setStyle(Paint.Style.FILL);
            colors.put(i,tmp);
        }

        //Canvas canvas = new Canvas();

        //size = Math.min(sizeX,sizeY);

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

        //System.out.println("Max Size: " + getMaxTextureSize()+"x"+getMaxTextureSize());
        //tiv.setZoom(1);
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
        }
    }
    public void updateImageView(){
        if(!update) return;
        int[][] timeStep;
        int last = board.getHistory().size()-1;
        //System.out.println("Last One: " + last + " || size: " + 10);
        timeStep = board.getHistory().get(last);
        task = new DrawTask(timeStep,tiv,size);
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
            simulate();
            super.onPostExecute(ints);
        }

        @Override
        protected int[][] doInBackground(Void... voids) {
            int[][] returnVale = board.nextTimeStep(true);
            return returnVale;
        }
    }
    class DrawTask extends AsyncTask<Void, Bitmap, Bitmap> {

        ImageView iv;
        int[][] board;
        Bitmap bmp;
        int size;
        int sizeX, sizeY;
        public DrawTask(int[][] board, ImageView iv, int size){
            this.iv = iv;
            iv.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
            this.bmp = drawable.getBitmap();
            this.size = size;
            this.board = board;
            this.sizeX = board[0].length*size;
            this.sizeY = board.length*size;
            //Canvas canvas = new Canvas();
            //float tmp = (float)sizeX / (float)canvas.getMaximumBitmapWidth();
            //float tmp2 = (float)sizeY/ (float)canvas.getMaximumBitmapHeight();
            //System.out.println("size / max X: " + tmp + "|| size /mex Y: " + tmp2);
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
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            white.setStyle(Paint.Style.FILL);
            bmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            //System.out.println(board);
            for(int i=0; i<board.length;i++){
                for(int j=0;j<board[0].length;j++){
                    Rect rect = new Rect(j*size,i*size,(j+1)*size,(i+1)*size);
                    if(board[i][j] != 0){
                        canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom, Objects.requireNonNull(colors.get(board[i][j])));
                    }else{
                        canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,white);
                    }
                }
                if(isCancelled())
                    return bmp;
            }
            return bmp;
        }
    }

    public static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }
}
