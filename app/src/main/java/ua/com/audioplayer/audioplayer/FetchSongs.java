package ua.com.audioplayer.audioplayer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Digriz on 30.07.2016.
 */

//Класс отвечающий за поиск песен на устройстве и их загрузку
public class FetchSongs {
    boolean fetchstatus=false;
    ArrayList<File> songs=new ArrayList<File>();

    public FetchSongs(){
    }

    public ArrayList<File> findSongs(File root){
        ArrayList<File> al=new ArrayList<File>();
        File[] files=root.listFiles();
        for (File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                al.addAll(findSongs(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3")){
                    al.add(singleFile);
                }
            }
        }
        fetchstatus=true;
        songs=al;
        return al;
    }
    public boolean getfetchstatus(){
        return fetchstatus;
    }
    public ArrayList<File> getsonglist(){
        return songs;
    }

}
