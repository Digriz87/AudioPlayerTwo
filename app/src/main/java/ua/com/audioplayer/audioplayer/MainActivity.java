package ua.com.audioplayer.audioplayer;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;




import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ua.com.audioplayer.audioplayer.utils.FileDialog;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    String[] items;
    FetchSongs fs;
    ArrayList<File> mySongs;
    ProgressDialog dialog;
    ImageView browser;
    EditText inputSearch;
    ArrayAdapter<String> adp;
    RelativeLayout relativeLayout;
    FrameLayout mainFrame;
    ImageView shadow_top, shadow, shadow_sink;


    //
    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "");
    private String mChosenFile;
    private static final String FTYPE = ".txt";
    private static final int DIALOG_LOAD_FILE = 1000;

    FileDialog fileDialog;

    File currentDirectory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        lv = (ListView)findViewById(R.id.listView);
        browser = (ImageView) findViewById(R.id.browser_ImageView);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        shadow_top= (ImageView) findViewById(R.id.shadow_top);
        shadow = (ImageView) findViewById(R.id.shadow);
        shadow_sink = (ImageView) findViewById(R.id.shadow_sink);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mainFrame = (FrameLayout) findViewById(R.id.main_frame);

        fetchByDir(Environment.getExternalStorageDirectory());

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



        //Оптимизация relative layout
        FrameLayout.LayoutParams relativeLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        relativeLayoutParams.setMargins(0, tenPixelsHeight*10, 0, 0);
        relativeLayout.setLayoutParams(relativeLayoutParams);

        //Оптимизация list view
        RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        listViewParams.setMargins(0, 0, 0, 0);
        lv.setLayoutParams(listViewParams);

        FrameLayout.LayoutParams shadowParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,tenPixelsHeight*10);
        shadowParams.setMargins(0, 0, 0, 0);
        shadow.setLayoutParams(shadowParams);


        FrameLayout.LayoutParams shadowSinkParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,1);
        shadowSinkParams.setMargins(0, tenPixelsHeight * 10, 0, 0);
        shadow_sink.setLayoutParams(shadowSinkParams);

        FrameLayout.LayoutParams shadowTopParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,tenPixelsHeight*5);
        shadowTopParams.setMargins(0, 0, 0, 0);
        shadow_top.setLayoutParams(shadowTopParams);

        FrameLayout.LayoutParams searchParams = new FrameLayout.LayoutParams(tenPixelsWight*60,tenPixelsHeight*8);
        searchParams.setMargins(20, 10, 0, 0);
        inputSearch.setLayoutParams(searchParams);
        inputSearch.setPadding(20, 0, 0, 0);

        FrameLayout.LayoutParams browserIconParams = new FrameLayout.LayoutParams(tenPixelsWight*8,tenPixelsHeight*8);
        browserIconParams.setMargins(tenPixelsWight * 75, 10, 0, 0);
        browser.setLayoutParams(browserIconParams);



        // Поиск
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                MainActivity.this.adp.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Браузер файлов
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //fetchByDir(onDirClick());

                onDirClick();

            }
        });





    }

    public File onDirClick(){
        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        fileDialog = new FileDialog(this, mPath, ".mp3");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
            }
        });
        fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
            @Override
            public void directorySelected(File directory) {
                currentDirectory = directory;
                fetchByDir(currentDirectory);
            }
        });

        fileDialog.setSelectDirectoryOption(true);
        fileDialog.showDialog();

        return mPath;
    }


    private void fetchByDir(File currentDirectory){
        fs = new FetchSongs();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Подождите пожалуйста, идет загрузка песен...");
        dialog.setCancelable(true);
        dialog.show();

        //Если песни не загружены или не найдены:
        while(fs.getfetchstatus()!=true){
            mySongs=fs.findSongs(currentDirectory); //Environment.getExternalStorageDirectory()
        }

        if(mySongs!=null){
            dialog.dismiss();
        }

        mySongs = fs.getsonglist();

        //Инициализируем наш массив куда поместим все песни для ЛистВью
        items = new String[mySongs.size()];
        for (int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","");
        }

        //Назначаем адаптер для ЛистВью с массивом из найденных песен, и передаем их все на следующий активити
        adp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items){


            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {




                View view =super.getView(position, convertView, parent);


                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            //Выбор цвета
                textView.setTextColor(Color.WHITE);

                int temp =position+1;


                textView.setText("" + temp + ". " + textView.getText());
                return view;
            }

        };
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Player.class);
                intent.putExtra("pos", i);
                startActivity(intent);
                finish();
            }
        });

    }




    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}






