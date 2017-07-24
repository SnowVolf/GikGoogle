package ru.SnowVolf.gikgoogle;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Snow Volf on 11.05.2017, 17:59
 */

public class UrlListFragment extends Fragment {
    public View rootView;
    private static ListView listView;
    private static final String GLOBAL_URL = "url";
    private String globalUrl;
    private static ArrayList<UrlItem> urls;
    public static TinyDB tinyDB;
    public static ProgressDialog dialog;
    public static Handler h;

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) rootView.findViewById(R.id.list_urls);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTinyDB();
        urls = tinyDB.getListObject("SaveUrl");
        ArrayAdapter<UrlItem> adapter = new UrlsAdapter(getActivity(), urls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UrlItem item = urls.get(i);
                MainActivity.mQueueText.setText(item.getTitle().length());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                UrlItem link = urls.get(i);
                copyText(link.getUrl());
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    private void copyText(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        //копируем текст из mQueueText
        ClipData clipData = ClipData.newPlainText("GikGoogle", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getActivity(), R.string.url_copied, Toast.LENGTH_SHORT).show();
    }

    private void initTinyDB() {
        tinyDB = new TinyDB(getActivity());
        globalUrl = getActivity().getIntent().getStringExtra(GLOBAL_URL);
        if (globalUrl == null) {
            globalUrl = " ";
        }
    }

    public static void saveUrl(String title, String url) {
        if (title == null){
            title = System.currentTimeMillis() + "";
        }
        ArrayList<UrlItem> urls = tinyDB.getListObject("SaveUrl");
        /*Иначе падает когда в urls нет ничего*/
        if (urls.size() != 0) {
            if (!checkUrlsDuplicate(url, urls)) {
                urls.add(0, new UrlItem(title, url));
                tinyDB.putListObject("SaveUrl", urls);
            }
        } else {
            urls.add(0, new UrlItem(title, url));
            tinyDB.putListObject("SaveUrl", urls);
        }
    }

    private static boolean checkUrlsDuplicate(String url, ArrayList<UrlItem> items) {
        boolean have = false;
        for (UrlItem item : items) {
            if (url.equals(item.getUrl())) {
                have = true;
            }
        }
        return have;
    }

    @Deprecated
    public static void refreshDB(Context ctx){
        listView.setAdapter(null);
        if (listView.getAdapter() == null) {
            urls = tinyDB.getListObject("SaveUrl");
            ArrayAdapter<UrlItem> adapter = new UrlsAdapter(ctx, urls);

            dialog = new ProgressDialog(ctx);
            dialog.setMessage(ctx.getString(R.string.dialog_db_wait_for_refresh));
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(true);
            dialog.setMax(urls.size());
            dialog.show();
            h = new Handler(){
                public void handleMessage(Message msg){
                    dialog.setIndeterminate(false);
                    if (dialog.getProgress() > dialog.getMax()){
                        dialog.incrementProgressBy(urls.size()/100);
                    } else {
                        dialog.dismiss();
                    }
                }
            };
            h.sendEmptyMessageDelayed(0, urls.size() * 100);
            listView.setAdapter(adapter);
        }
    }

    public static void cleanUrlDB(){
        tinyDB.remove("SaveUrl");
        urls.clear();
        listView.setAdapter(null);
    }

}
