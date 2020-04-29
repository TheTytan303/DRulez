package pl.edu.ciesla.drulez.View.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.View.TouchImageView;
import pl.edu.ciesla.drulez.core.Board.Board2D;

public class NucleationPointActivity extends AppCompatActivity implements View.OnTouchListener {

    Board2D.Nucleation nucleation ;
    Board2D.GrainGrowth gg ;
    int values ;
    float lValue;
    int rValue;
    int width;
    int height;
    float radius;
    boolean isPeriodic;
    List<int[]> points;
    Canvas canvas;
    Bitmap bmp;
    Paint black;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleation_point);

        nucleation = (Board2D.Nucleation) getIntent().getSerializableExtra("nucleation");
        gg = (Board2D.GrainGrowth) getIntent().getSerializableExtra("grainGrowth");
        values = getIntent().getIntExtra("values",0);
        lValue = getIntent().getFloatExtra("lValue",3.5f);
        rValue = getIntent().getIntExtra("rValue",5);
        width = getIntent().getIntExtra("width",5);
        height = getIntent().getIntExtra("height",5);
        radius =  getIntent().getFloatExtra("growthRadius",1.5f);
        isPeriodic = getIntent().getBooleanExtra("periodic",true);
        points = new LinkedList<>();
        findViewById(R.id.anp_main_image).setOnTouchListener(this);
        int sizeX = width*10;
        int sizeY = height*10;
        bmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmp);
        ((TouchImageView)findViewById(R.id.anp_main_image)).setImageBitmap(bmp);
        ((TouchImageView)findViewById(R.id.anp_main_image)).setMinZoom(1f);
        ((TouchImageView)findViewById(R.id.anp_main_image)).setMaxZoom(1);
        ((TouchImageView)findViewById(R.id.anp_main_image)).setZoom(1);

        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.FILL);
    }




    public void contin(View v){
        Intent contin = new Intent(this, CellarAutomata.class);
        contin.putExtra("nucleation",nucleation);
        contin.putExtra("periodic",isPeriodic);
        contin.putExtra("grainGrowth",gg);
        contin.putExtra("lValue",lValue);
        contin.putExtra("rValue",rValue);
        contin.putExtra("width",width);
        contin.putExtra("height",height);
        contin.putExtra("growthRadius",radius);

        //Board2D board2D
        contin.putExtra("values",points.size());
        int[] X = new int[points.size()];
        int[] Y = new int[points.size()];
        for(int i =0; i<points.size();i++){
            X[i]=points.get(i)[0];
            Y[i]=points.get(i)[1];
        }
        contin.putExtra("X",X);
        contin.putExtra("Y",Y);
        startActivity(contin);




    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TouchImageView tiv = (TouchImageView) v;
        int action = event.getActionMasked();
        int x = (int)event.getX()*2;
        int y = (int)event.getY()*2;
        if (action == MotionEvent.ACTION_DOWN) {
            System.out.println("clicked at: [" + x + "|" + y+"]");
            int[] tmp = new int[] {x,y};
            points.add(tmp);
            canvas.drawRect(x-5, y+5,x+5,y-5, black);
        }
        tiv.setImageBitmap(bmp);
        //for(int[] tab : points){
        //
        //}
        return false;




    }
}
