package pl.edu.ciesla.drulez.View.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.core.Board.Board2D;

public class CellarAutomataSetup extends AppCompatActivity implements View.OnClickListener {
    static int DEFAULT_CELL_SIZE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_automata_setup);
        findViewById(R.id.acas_nucleation).post(new Runnable() {
            @Override
            public void run() {
                View v = findViewById(R.id.acas_main_constraint);
                int tmp = v.getHeight()/DEFAULT_CELL_SIZE;
                ((EditText)findViewById(R.id.acas_height_et)).setText(tmp+"");
                tmp = v.getWidth()/DEFAULT_CELL_SIZE;
                ((EditText)findViewById(R.id.acas_width_et)).setText(tmp+"");
            }
        });

        ((RadioGroup)findViewById(R.id.acas_nucleation)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                detailsVisible(true);
                switch(checkedId){
                    case R.id.acas_nucleation_homogenous:
                        setTV("ilość w kolumnie:","ilość w rzędzie:");
                        break;
                    case R.id.acas_nucleation_radius:
                        setTV("promień:","ilość zarodków:");
                        break;
                    case R.id.acas_nucleation_random:
                        setTV("number:",null);
                        break;
                    case R.id.acas_nucleation_banned:
                        detailsVisible(false);
                        break;
                }
            }
            private void detailsVisible(boolean visible){
                if(visible){
                    findViewById(R.id.acas_nucleation_l).setVisibility(View.VISIBLE);
                    findViewById(R.id.acas_nucleation_r).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.acas_nucleation_l).setVisibility(View.GONE);
                    findViewById(R.id.acas_nucleation_r).setVisibility(View.GONE);
                }
            }
            private void setTV(String lText, String rText){
                TextView tv = findViewById(R.id.acas_nucleation_l_tv);
                tv.setText(lText);
                if(rText == null){
                    findViewById(R.id.acas_nucleation_r).setVisibility(View.GONE);
                }else{
                    tv = findViewById(R.id.acas_nucleation_r_tv);
                    tv.setText(rText);
                }
            }
        });
        ((RadioGroup)findViewById(R.id.acas_grain_growth)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                detailsVisible(true);
                    switch(checkedId) {
                        case R.id.acas_grain_growth_hex:
                            findViewById(R.id.acas_grain_growth_details_pent_type).setVisibility(View.GONE);
                            findViewById(R.id.acas_grain_growth_details_radius).setVisibility(View.GONE);
                            findViewById(R.id.acas_grain_growth_details_hex_type).setVisibility(View.VISIBLE);
                            break;
                        case R.id.acas_grain_growth_pent:
                            findViewById(R.id.acas_grain_growth_details_pent_type).setVisibility(View.VISIBLE);
                            findViewById(R.id.acas_grain_growth_details_radius).setVisibility(View.GONE);
                            findViewById(R.id.acas_grain_growth_details_hex_type).setVisibility(View.GONE);
                            break;
                        case R.id.acas_grain_growth_radius:
                            findViewById(R.id.acas_grain_growth_details_pent_type).setVisibility(View.GONE);
                            findViewById(R.id.acas_grain_growth_details_hex_type).setVisibility(View.GONE);
                            findViewById(R.id.acas_grain_growth_details_radius).setVisibility(View.VISIBLE);
                            break;
                        case R.id.acas_grain_growth_Moore:
                        case R.id.acas_grain_growth_vonNeuman:
                        default:
                            detailsVisible(false);
                    }
                }
            private void detailsVisible(boolean visible){
                if(visible){
                    findViewById(R.id.acas_grain_growth_details).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.acas_grain_growth_details).setVisibility(View.GONE);
                }
            }
            private void setTV(String lText){
                TextView tv = findViewById(R.id.acas_grain_groth_details_tv);
                tv.setText(lText);
            }
        });
        findViewById(R.id.acas_nucleation_v_tv).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Ilosć rodzajów ziaren");
                final TextView xET = new EditText(v.getContext());
                xET.setText("Zostanie zasymulowana nastepująca ilość rodzajów ziaren.\n" +
                        "Pozostaw 0 aby każde ziarno miało osobny kolor");
                LinearLayout mainLayout = new LinearLayout(v.getContext());
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.addView(xET);
                builder.setView(mainLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {} });
                builder.show();
            }
        });
    }

    public void onContinue(View view){

        float growthRadius =0;
        RadioGroup rg = findViewById(R.id.acas_grain_growth);
        Board2D.GrainGrowth gg = Board2D.GrainGrowth.vonNeuman;
        Board2D.Nucleation nucleation = Board2D.Nucleation.random;
        switch(rg.getCheckedRadioButtonId()){
            case R.id.acas_grain_growth_hex:
                rg = findViewById(R.id.acas_grain_growth_details_hex_type);
                switch(rg.getCheckedRadioButtonId()) {
                    case R.id.acas_grain_growth_details_hex_right:
                        gg = Board2D.GrainGrowth.hex_right;
                        break;
                    case R.id.acas_grain_growth_details_hex_left:
                        gg = Board2D.GrainGrowth.hex_left;
                        break;
                    case R.id.acas_detils_grain_growth_hex_random:
                    default:
                        gg = Board2D.GrainGrowth.hex_random;
                        break;
                }
                break;
            case R.id.acas_grain_growth_Moore:
                gg = Board2D.GrainGrowth.Moore;
                break;
            case R.id.acas_grain_growth_pent:
                rg = findViewById(R.id.acas_grain_growth_details_pent_type);
                switch(rg.getCheckedRadioButtonId()){
                    case R.id.acas_grain_growth_pent_bottom:
                        gg = Board2D.GrainGrowth.pent_bottom;
                        break;
                    case R.id.acas_grain_growth_pent_left:
                        gg = Board2D.GrainGrowth.pent_left;
                        break;
                    case R.id.acas_grain_growth_pent_top:
                        gg = Board2D.GrainGrowth.pent_top;
                        break;
                    case R.id.acas_grain_growth_pent_right:
                        gg = Board2D.GrainGrowth.pent_right;
                        break;
                    case R.id.acas_grain_growth_pent_random:
                        gg = Board2D.GrainGrowth.pent_random;
                }
                break;
            case R.id.acas_grain_growth_radius:
                gg = Board2D.GrainGrowth.radius;
                growthRadius = Float.parseFloat(((EditText)findViewById(R.id.acas_grain_growth_details_radius_et)).getText().toString());
                break;
            case R.id.acas_grain_growth_vonNeuman:
            default:
                gg = Board2D.GrainGrowth.vonNeuman;
                break;
        }
        rg = findViewById(R.id.acas_nucleation);
        switch(rg.getCheckedRadioButtonId()){
            case R.id.acas_nucleation_homogenous:
                nucleation = Board2D.Nucleation.homogeneous;
                break;
            case R.id.acas_nucleation_banned:
                nucleation = Board2D.Nucleation.banned;
                break;
            case R.id.acas_nucleation_radius:
                nucleation = Board2D.Nucleation.radius;
                break;
            case R.id.acas_nucleation_random:
                nucleation = Board2D.Nucleation.random;
        }
        float lValue;
        int rValue;
        int values;
        int width, height;
        boolean periodic;
        try{
            lValue = Float.parseFloat(((EditText)findViewById(R.id.acas_nucleation_lvalue)).getText().toString());
            rValue = Integer.parseInt(((EditText)findViewById(R.id.acas_nucleation_rvalue)).getText().toString());
            values = Integer.parseInt(((EditText)findViewById(R.id.acas_nucleation_values)).getText().toString());
            width = Integer.parseInt(((EditText)findViewById(R.id.acas_width_et)).getText().toString());
            height = Integer.parseInt(((EditText)findViewById(R.id.acas_height_et)).getText().toString());
            periodic = ((Switch)findViewById(R.id.acas_periodic_sw)).isChecked();
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Incorrect numbers ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {} });
            builder.show();
            return;
        }


        Intent contin = new Intent(this, CellarAutomata.class);
        contin.putExtra("nucleation",nucleation);
        contin.putExtra("periodic",periodic);
        contin.putExtra("grainGrowth",gg);
        contin.putExtra("values",values);
        contin.putExtra("lValue",lValue);
        contin.putExtra("rValue",rValue);
        contin.putExtra("width",width);
        contin.putExtra("height",height);
        contin.putExtra("growthRadius",growthRadius);
        startActivity(contin);
    }


    @Override
    public void onClick(View v) {

    }

}
