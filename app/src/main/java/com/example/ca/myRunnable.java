package com.example.ca;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class myRunnable implements Runnable{
    private int i;
    private View v;
    private Bitmap[] fetched;
    private ProgressBar progressBar;

    public myRunnable(int i, View v, Bitmap[] fetched, ProgressBar progressBar){
        this.i = i;
        this.v = v;
        this.fetched = fetched;
        this.progressBar = progressBar;
    }

    @Override
    public void run(){
        ImageView iv = getImageView(v, i);
        iv.setImageBitmap(fetched[i-1]);
        progressBar.setProgress(i);
        TextView tv = v.getRootView().findViewById(R.id.txtProgress);
        tv.setText(i + " out of 20 downloaded");

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.selected.size() < 6){
                    Bitmap bm = fetched[i-1];
                    if(MainActivity.selected.contains(bm)){
                        iv.setBackgroundResource(0);
                        MainActivity.selected.remove(bm);
                        TextView tv = v.getRootView().findViewById(R.id.txtNumberSelected);
                        tv.setText(MainActivity.selected.size()+" selected");
                    }
                    else if (!MainActivity.selected.contains(bm)){
                        iv.setBackgroundResource(R.drawable.border);
                        MainActivity.selected.add(bm);
                        TextView tv = v.getRootView().findViewById(R.id.txtNumberSelected);
                        tv.setText(MainActivity.selected.size()+" selected");
                        if(MainActivity.selected.size() == 6){
                            Button startButton = v.getRootView().findViewById(R.id.startButton);
                            startButton.setVisibility(View.VISIBLE);
                            startButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(v.getContext(), GameActivity.class);
                                    v.getContext().startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public  ImageView getImageView(View v, int i){
        ImageView iv = null;
        switch (i){
            case 1:
                iv = v.getRootView().findViewById(R.id.img1);
                break;
            case 2:
                iv = v.getRootView().findViewById(R.id.img2);
                break;
            case 3:
                iv = v.getRootView().findViewById(R.id.img3);
                break;
            case 4:
                iv = v.getRootView().findViewById(R.id.img4);
                break;
            case 5:
                iv = v.getRootView().findViewById(R.id.img5);
                break;
            case 6:
                iv = v.getRootView().findViewById(R.id.img6);
                break;
            case 7:
                iv = v.getRootView().findViewById(R.id.img7);
                break;
            case 8:
                iv = v.getRootView().findViewById(R.id.img8);
                break;
            case 9:
                iv = v.getRootView().findViewById(R.id.img9);
                break;
            case 10:
                iv = v.getRootView().findViewById(R.id.img10);
                break;
            case 11:
                iv = v.getRootView().findViewById(R.id.img11);
                break;
            case 12:
                iv = v.getRootView().findViewById(R.id.img12);
                break;
            case 13:
                iv = v.getRootView().findViewById(R.id.img13);
                break;
            case 14:
                iv = v.getRootView().findViewById(R.id.img14);
                break;
            case 15:
                iv = v.getRootView().findViewById(R.id.img15);
                break;
            case 16:
                iv = v.getRootView().findViewById(R.id.img16);
                break;
            case 17:
                iv = v.getRootView().findViewById(R.id.img17);
                break;
            case 18:
                iv = v.getRootView().findViewById(R.id.img18);
                break;
            case 19:
                iv = v.getRootView().findViewById(R.id.img19);
                break;
            case 20:
                iv = v.getRootView().findViewById(R.id.img20);
                break;
            default:
                break;
        }
        return iv;
    }

}
