package ua.com.audioplayer.audioplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Digriz on 30.07.2016.
 */
public class Player extends AppCompatActivity {
    int position;
    FetchSongs fs;
    ArrayList<File> mySongs;
    String path;
    ImageView albumart;
    TextView name,artist;
    ImageButton play,prev,next,playlist;

    static seekUpdater su;
    Uri u;
    SeekBar sb;
    String artistName,songName;
    byte[] art;
    Bitmap songArt;
    MediaMetadataRetriever mmr;
    static MediaPlayer mPlayer;
    boolean notificationflag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        albumart=(ImageView)findViewById(R.id.albumArt);
        name=(TextView)findViewById(R.id.songName);
        artist=(TextView)findViewById(R.id.artistName);
        play=(ImageButton)findViewById(R.id.playButton);
        prev=(ImageButton)findViewById(R.id.previousButton);
        next=(ImageButton)findViewById(R.id.nextButton);
        playlist=(ImageButton)findViewById(R.id.playlistButton);
        sb=(SeekBar)findViewById(R.id.seekBar);

        // Установка Статус Бара определенного цвета
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLUE);
        }

        // Оптимизация вьюшек
        //Получаем размер нашего экрана
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels ;
        int wight = metrics.widthPixels ;

        //Получаем наш размер в 10 пикселях любого экрана
        int tenPixelsHeight = height/128;
        int tenPixelsWight = wight/77;

        RelativeLayout.LayoutParams textOneParams = new RelativeLayout.LayoutParams(tenPixelsWight*20,tenPixelsHeight*6);
        textOneParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        textOneParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        textOneParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        textOneParams.addRule(RelativeLayout.LEFT_OF, R.id.playlistButton);
        textOneParams.addRule(RelativeLayout.START_OF, R.id.playlistButton);
        textOneParams.setMargins(0, 25, 0, 0);
        name.setLayoutParams(textOneParams);


        RelativeLayout.LayoutParams artistNameParams = new RelativeLayout.LayoutParams(tenPixelsWight*19,tenPixelsHeight*5);
        artistNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        artistNameParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        artistNameParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.songName);
        artistNameParams.addRule(RelativeLayout.ALIGN_END, R.id.songName);
        artistNameParams.addRule(RelativeLayout.BELOW, R.id.songName);
        artistNameParams.setMargins(0, 25, 0, 0);
        artist.setLayoutParams(artistNameParams);


        //Оптимизация
        RelativeLayout.LayoutParams albumParams = new RelativeLayout.LayoutParams(tenPixelsWight*40,tenPixelsHeight*40);
        albumParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        albumParams.addRule(RelativeLayout.BELOW, R.id.artistName);
        albumParams.setMargins(0, 40, 0, 0);
        albumart.setLayoutParams(albumParams);






        RelativeLayout.LayoutParams playButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        playButtonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.nextButton);
        playButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        playButtonParams.setMargins(0, tenPixelsHeight * 80, 0, 20);
        play.setLayoutParams(playButtonParams);

        RelativeLayout.LayoutParams nextButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        nextButtonParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.playlistButton);
        nextButtonParams.addRule(RelativeLayout.ALIGN_END, R.id.playlistButton);

        nextButtonParams.setMargins(0, tenPixelsHeight*80, 0, 20);
        next.setLayoutParams(nextButtonParams);

        RelativeLayout.LayoutParams prevButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        prevButtonParams.addRule(RelativeLayout.ALIGN_TOP, R.id.playButton);
        prevButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        prevButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

        prevButtonParams.setMargins(0, tenPixelsHeight, 0, 20);
        prev.setLayoutParams(prevButtonParams);

        RelativeLayout.LayoutParams seekParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        seekParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        seekParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        seekParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        seekParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        seekParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);

        seekParams.setMargins(0, 11, 0, 0);
        sb.setLayoutParams(seekParams);

        RelativeLayout.LayoutParams plRevParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        plRevParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        plRevParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        plRevParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.songName);
        plRevParams.setMargins(0, tenPixelsHeight*10, 0, 0);
        playlist.setLayoutParams(plRevParams);

        if(mPlayer!=null){
            if(su!=null){
                su.endthread();
                su.interrupt();
                su=null;
            }
            mPlayer.stop();
            mPlayer.release();
        }

        fs=new FetchSongs();
        if(fs.getfetchstatus()!=true){
            mySongs=fs.findSongs(Environment.getExternalStorageDirectory());
        }
        else{
            mySongs=fs.getsonglist();
        }

        Intent intent=getIntent();
        position=intent.getIntExtra("pos",0); //Принимаем наш интент с позицией
        path=mySongs.get(position).toString();
        u=Uri.parse(path);

        setsongdata(path,position);
        mPlayer=MediaPlayer.create(getApplicationContext(),u);
        mPlayer.start();
        mPlayer.setLooping(true);
        play.setImageResource(R.drawable.pause);

        sb.setMax(mPlayer.getDuration());
        su=new seekUpdater(true);
        su.start();

        //Назначаем слушателя для сикбара
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(sb.getProgress());
            }
        });
//Обработчики наших кнопок проигрывателя
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
                else {
                    mPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(su!=null){
                    su.endthread();
                    su.interrupt();
                    su=null;
                }
                mPlayer.stop();
                mPlayer.release();
                position=(position+1)%mySongs.size();
                path=mySongs.get(position).toString();
                setsongdata(path,position);
                u=Uri.parse(path);
                mPlayer=MediaPlayer.create(getApplicationContext(),u);
                mPlayer.start();
                mPlayer.setLooping(true);
                play.setImageResource(R.drawable.pause);
                sb.setMax(mPlayer.getDuration());
                sb.setProgress(mPlayer.getCurrentPosition());
                su=new seekUpdater(true);
                su.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(su!=null){
                    su.endthread();
                    su.interrupt();
                    su=null;
                }
                mPlayer.stop();
                mPlayer.release();
                position=(position-1<0)? mySongs.size()-1:position-1;
                path=mySongs.get(position).toString();
                setsongdata(path,position);
                u=Uri.parse(path);
                mPlayer=MediaPlayer.create(getApplicationContext(),u);
                mPlayer.start();
                mPlayer.setLooping(true);
                play.setImageResource(R.drawable.pause);
                sb.setMax(mPlayer.getDuration());
                sb.setProgress(mPlayer.getCurrentPosition());
                su=new seekUpdater(true);
                su.start();
            }
        });
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    class seekUpdater extends Thread{

        private boolean running;

        seekUpdater(boolean status){
            running=status;
        }

        public void endthread(){
            running=false;
        }

        @Override
        public void run() {
            try {
                while (running==true) {
                    int dur = mPlayer.getDuration();
                    int cur = mPlayer.getCurrentPosition();
                    while (cur < dur) {
                        sleep(500);
                        cur = mPlayer.getCurrentPosition();
                        sb.setProgress(cur);
                    }
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
                running=false;
            }
            //super.run();
        }
    }

    public void setsongdata(String songpath, int pos){
        mmr=new MediaMetadataRetriever();
        mmr.setDataSource(songpath);
        try {
            songName=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            art = mmr.getEmbeddedPicture();
            songArt = BitmapFactory.decodeByteArray(art, 0, art.length);
            artistName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            artist.setText(artistName);
            name.setText(songName);
            albumart.setImageBitmap(songArt);
        }
        catch (Exception e){
            albumart.setImageResource(R.drawable.albumart);
            artist.setText("Не известный артист");
            name.setText(mySongs.get(pos).getName().toString().replace(".mp3", ""));
        }
    }

}
