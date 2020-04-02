package pl.edu.ciesla.drulez.View;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.InputType;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.core.Board.Board2D;

public class StructuresAdapter extends RecyclerView.Adapter<StructuresAdapter.StructureViewHolder>{
    List<Map.Entry<String, int[][]>> structures;
    Board2D board;
    boardChangeddListener listener;
    public StructuresAdapter(List<Map.Entry<String, int[][]>> structures, Board2D board, boardChangeddListener listener){
        this.structures = structures;
        this.board = board;
        this.listener = listener;
    }
    @NonNull
    @Override
    public  StructuresAdapter.StructureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.structure_view, parent,false);
        return new StructureViewHolder(v,(ImageView) v.findViewById(R.id.sv_image),
                (TextView) v.findViewById(R.id.sv_text),
                board, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StructureViewHolder holder, int position) {
        holder.setStructure(structures.get(position).getValue(),structures.get(position).getKey());
    }


    @Override
    public int getItemCount() {
        return structures.size();
    }
    public interface boardChangeddListener{
        void onBoardChanged();
    }

    static class StructureViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener,
            View.OnDragListener {
        private int[][] structure;
        Board2D board;
        private String text;
        private ImageView iv;
        private TextView tv;
        private Integer x,y;
        boardChangeddListener listener;
        public StructureViewHolder(View view, ImageView iv, TextView tv,Board2D board, boardChangeddListener listener){
            super(view);
            this.iv =iv;
            this.tv = tv;
            this.listener = listener;
            this.board = board;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnDragListener(this);
        }
        public void setStructure(int[][] structure, String text){
            this.structure = structure;
            this.text = text;
            this.tv.setText(text);
            this.iv.setImageBitmap(bmpGenerator(structure));
        }
        @Override
        public void onClick(View v) {
            System.out.println("structure: " + text);
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("gdzie wstawić?");
            final EditText xET = new EditText(v.getContext());
            xET.setInputType(InputType.TYPE_CLASS_NUMBER);
            final EditText yET = new EditText(v.getContext());
            yET.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout mainLayout = new LinearLayout(v.getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.addView(xET);
            mainLayout.addView(yET);
            builder.setView(mainLayout);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        x = Integer.parseInt(xET.getText().toString());
                        y = Integer.parseInt(yET.getText().toString());
                    }catch (Exception ignore){};
                    board.setCells(x,y,structure);
                    listener.onBoardChanged();
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

        private Bitmap bmpGenerator(int[][] board){
            Paint black = new Paint();
            black.setColor(Color.BLACK);
            black.setStyle(Paint.Style.FILL);
            Paint white = new Paint();
            white.setColor(Color.CYAN);
            white.setStyle(Paint.Style.FILL);
            int size = 25;
            int sizeX = board.length*size;
            int sizeY = board[0].length*size;
            Bitmap bmp = Bitmap.createBitmap(sizeX,sizeY, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmp);
            //System.out.println(board);
            for(int i=0; i<board.length;i++){
                for(int j=0; j<board[0].length;j++){
                    Rect rect = new Rect(i*size,j*size,(i+1)*size,(j+1)*size);
                    if(board[i][j] == 0){
                        canvas.drawRect(rect,white);
                    }else{
                        canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,black);
                    }
                }
            }
            return bmp;
        }

        @Override
        public boolean onLongClick(View v) {
            ClipData.Item cdItem = new ClipData.Item(text);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(iv);
            ClipData dragData = new ClipData(text,new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },cdItem);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow, null, 0);
            } else {
                v.startDrag(dragData, myShadow, null, 0);
            }
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            return true;
        }
    }
}
