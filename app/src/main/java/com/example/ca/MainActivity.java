package com.example.ca;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private Button submitButton;
    private static final int COUNT = 20;
    private Thread downloadingThread;
    private ArrayList<String> filenames;
    private int[] imgViews;
    private TextView progressText;
    //static Bitmap[] fetched;
    static ArrayList<Bitmap> selected = new ArrayList<Bitmap>();
    protected static ActivityResultLauncher<Intent> rlGameActivity;
    protected static boolean musicFlag = true;
    ImageButton btnMusic;
    Button startButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, MyMusicService.class));

        rlGameActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            musicFlag = data.getBooleanExtra("flagReturn", true);
                        }
                    }
                }
        );

        btnMusic = findViewById(R.id.btnMusic);

        if (musicFlag) {
            btnMusic.setBackgroundResource(R.drawable.music_play);
        }
        else {
            btnMusic.setBackgroundResource(R.drawable.music_stop);
        }

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicFlag) {
                    btnMusic.setBackgroundResource(R.drawable.music_stop);
                    musicFlag = false;
                }
                else  {
                    btnMusic.setBackgroundResource(R.drawable.music_play);
                    musicFlag = true;
                }
                Intent intent = new Intent(MainActivity.this, MyMusicService.class);
                intent.setAction("play_bg_music");
                startService(intent);
            }
        });



        filenames = new ArrayList<String>();
        //fetched = new Bitmap[20];
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(COUNT);
        submitButton = findViewById(R.id.btnSubmitUrl);
        submitButton.setOnClickListener(this);
        imgViews = new int[COUNT];
        Resources resource = getResources();
        String pkgName = getPackageName();
        for (int i = 0; i < COUNT; i++) {
            String resName = "img" + (i+1);
            imgViews[i] = resource.getIdentifier(resName, "id", pkgName);
        }
        progressText = findViewById(R.id.txtProgress);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == submitButton.getId()) {
            EditText et = findViewById(R.id.txtUrl);
            String url = et.getText().toString();
            clearCurrentImages();
            downloadImages(url);
            clearCurrentSelected();
        }
    }

    public void clearCurrentSelected(){
        selected = new ArrayList<>();
        TextView tv = findViewById(R.id.txtNumberSelected);
        tv.setText("");
    }

    public List<String> getImgSrc(String htmlStr) {
        if (htmlStr == null) {
            return null;
        }
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
        String regEx_img = "<img.* src=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = m_image.group();
            Matcher m = Pattern.compile(" src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }

    public String getHtml(String urlString) {
        String html = "";
        try {
            URL url = new URL(urlString);
            try {
                InputStream is = url.openStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");

                BufferedReader br = new BufferedReader(isr);
                String data = br.readLine();
                while (data != null) {
                    html += data;
                    data = br.readLine();
                }
                br.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return html;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    private void clearCurrentImages() {
        Log.d("UserProcess", "clearing all images");
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        for (String path : filenames) {
            File file = new File(dir, path);
            if (file.exists()) {
                file.delete();
            }
            filenames = new ArrayList<String>();
        }
        if (downloadingThread != null) {
            downloadingThread.interrupt();
            downloadingThread = null;
        }
        for(int i : imgViews) {
            Log.d("UserProcess", "clearing imgView " + i);
            ImageView view = findViewById(i);
            if (view != null) {
                Log.d("UserProcess", i + "view not found.");
                view.setImageResource(R.drawable.placeholder);
                view.invalidate();
            }
        }
        progressBar.setProgress(0);
        progressBar.invalidate();
        progressText.setText("0 out of 0 downloaded.");
        progressText.invalidate();

    }

    private void downloadImages(String url) {
        downloadingThread = new Thread(() -> {
            String html = getHtml(url);
            ArrayList<String> pics = (ArrayList<String>) getImgSrc(html);
            Bitmap[] fetched = new Bitmap[COUNT];
            for (Bitmap bm : fetched) {
                bm = null;
            }
            int num = 0;
            for (String pic : pics) {
                Bitmap bitmap = getBitmapFromURL(pic);
                if (bitmap != null) {
                    fetched[num] = bitmap;
                    num++;
                    runOnUiThread(() -> saveBitmapToFile(bitmap, pic));
                    runOnUiThread(new myRunnable(num, submitButton, fetched, progressBar));
                    if (num == COUNT) {
                        break;
                    }
                }
            }
        });
        downloadingThread.start();
    }
    private void saveBitmapToFile(Bitmap bm, String name) {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String filename = name.substring(name.lastIndexOf('/'));
            File file = new File(dir, filename);
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            filenames.add(filename);
        }
        catch (Exception e) {
            Log.d("UserProcess", e.getMessage());
            e.printStackTrace();
        }
    }

 /*   protected void registerForResult() {
        rlGameActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            musicFlag = data.getBooleanExtra("flagReturn", true);
                        }
                    }
                }
        );
    }*/

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(MainActivity.this, MyMusicService.class);
        intent.setAction("pause_bg_music");
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (musicFlag) {
            btnMusic.setBackgroundResource(R.drawable.music_play);
        }
        else {
            btnMusic.setBackgroundResource(R.drawable.music_stop);
        }
        Intent intent = new Intent(MainActivity.this, MyMusicService.class);
        intent.setAction("resume_bg_music");
        startService(intent);
    }

}
