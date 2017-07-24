package ru.SnowVolf.gikgoogle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    public static EditText mQueueText;
    String urlL = "https://google.gik-team.com/?q=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueueText = (EditText) findViewById(R.id.zapros);

        getFragmentManager().beginTransaction().replace(R.id.frame_container, new UrlListFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        ImageButton mBtnClear = (ImageButton) findViewById(R.id.clear);
        ImageButton mBtnCopy = (ImageButton) findViewById(R.id.copy);
        ImageButton mBtnShare = (ImageButton) findViewById(R.id.share_intent);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearText();
            }
        });
        mBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copy2Clipboard();
            }
        });
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go2();
            }
        });
    }

    @Override
    //объявляем меню
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.git:
                visitGit();
                return true;
            case R.id.cleardb:
                UrlListFragment.cleanUrlDB();
                return true;
            case R.id.refreshdb:
                UrlListFragment.refreshDB(this);
                return true;
            case R.id.about:
                showAboutDialog();
                return true;
            default:
                break;
        }
        return false;
    }

    public void copy2Clipboard(){
        ClipboardManager mClipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        //копируем текст из mQueueText
        String urlEncoded = mQueueText.getText().toString().replaceAll("\\s", "+");
        ClipData clipData = ClipData.newPlainText("GikGoogle", urlL + urlEncoded);
        UrlListFragment.saveUrl(mQueueText.getText().toString(), urlL + urlEncoded);
        mClipboardManager.setPrimaryClip(clipData);
        if(clipData != null && !clipData.toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.open_gik_brace) + mQueueText.getText().toString() + getString(R.string.close_gik_brace), Toast.LENGTH_LONG).show();
        } else if (clipData != null && clipData.toString().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.nothing_to_copy, Toast.LENGTH_SHORT).show();
        }
    }

    public void go2(){
        if (mQueueText.getText().toString().isEmpty())
            Toast.makeText(getBaseContext(), R.string.enter_quere_first, Toast.LENGTH_SHORT).show();
        else {
            String urlEncoded2 = mQueueText.getText().toString().replaceAll("\\s", "+");
            UrlListFragment.saveUrl(mQueueText.getText().toString(), urlL + urlEncoded2);
            Uri uri = Uri.parse(urlL + urlEncoded2);
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(urlIntent, getString(R.string.open_in_browser)));

        }

    }

    public void clearText(){
        mQueueText.setText("");
    }

    public void visitGit(){
        startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_url))), getString(R.string.explore_source)));
    }

    private void showAboutDialog(){
        final StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("about.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.app_name);
        dlg.setMessage(sb);
        dlg.setPositiveButton(android.R.string.ok, null);
        dlg.create();
        dlg.show();
    }

}
