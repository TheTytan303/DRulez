package pl.edu.ciesla.drulez.View;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.edu.ciesla.drulez.R;
import pl.edu.ciesla.drulez.core.Board.Board1D;
import pl.edu.ciesla.drulez.core.Board.Board2D;
import pl.edu.ciesla.drulez.core.cell.Cell;

public class CellViewAdapter extends RecyclerView.Adapter<CellViewAdapter.CellViewHolder> {
    Board2D board;
    CellViewListener cvl;
    int size;
    public interface CellViewListener{
        void onCellsSwiped(Cell[] cells);
    }
    public CellViewAdapter(Board2D board, CellViewListener cvl, int size){
        this.board = board;
        this.cvl = cvl;
        this.size = size;
    }
    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_view, parent,false);
        return new CellViewHolder(v,cvl, size);
    }
    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        holder.setCell(board.getCells().get(position));

    }
    @Override
    public int getItemCount() {
        return board.getCells().size();
    }

    public static class CellViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener{
        CellViewListener cvl;
        ImageView iv;
        Cell cell;
        Bitmap bmp;
        int size;
        CellViewHolder(View v, CellViewListener cvl, int size){
            super(v);
            this.cvl = cvl;
            this.iv = v.findViewById(R.id.cv_iv);
            iv.setMaxWidth(size);
            iv.setMaxHeight(size);
            this.size = size;
            this.iv.setOnLongClickListener(this);
        }
        public void setCell(Cell cell){
            this.cell = cell;
            this.updateView();
        }
        void updateView(){
            if(cell.getState() == 0){
                bmp = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
                this.iv.setImageBitmap(bmp);
            }else{
                DrawTask drawTask = new DrawTask(bmp, iv,cell,size);
                drawTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int state = cell.getState();
            System.out.println("LongClick state" + state);
            if(state != 0){
                this.cell.setState(0);
            }else{
                this.cell.setState(1);
            }
            this.updateView();
            return false;
        }

        static class DrawTask extends AsyncTask<Void, Bitmap,Bitmap> {
            Bitmap bmp;
            ImageView iv;
            int size;
            Cell cell;
            public DrawTask(Bitmap bmp, ImageView iv, Cell cell, int size){
                this.iv = iv;
                this.bmp = bmp;
                this.size = size;
                this.cell =cell;
            }
            @Override
            protected void onProgressUpdate(Bitmap... values) {
                iv.setImageBitmap(values[0]);
                super.onProgressUpdate(values);
            }
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Paint p = new Paint();
                if(cell.getState() == 0){
                    p.setColor(Color.WHITE);
                }else{
                    p.setColor(Color.BLACK);
                }
                p.setStyle(Paint.Style.FILL);
                bmp = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bmp);
                canvas.drawPaint(p);
                publishProgress(bmp);
                return bmp;
            }
        }
    }

}
