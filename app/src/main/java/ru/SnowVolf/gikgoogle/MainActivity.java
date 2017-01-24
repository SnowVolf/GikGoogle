package ru.SnowVolf.gikgoogle;

import android.app.Activity;
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
import android.widget.Toast;

public class MainActivity extends Activity {
    public EditText urlText;
    String DOMAIN = "http://google.gik-team.com/";
    String q = "?q=";
    String urlL = DOMAIN + q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlText = (EditText) findViewById(R.id.zapros);
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
        if(id == R.id.git){
            VisitGit();
            return true;
        }
        return onOptionsItemSelected(item);
    }
    public void Copy2Clipboard(View v){
        ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        //копируем текст из urlText
        ClipData clipData = ClipData.newPlainText("GikGoogle", urlL+urlText.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        if(clipData != null){
            Toast.makeText(getApplicationContext(), "Скопировано: GikGoogle [" + urlText.getText().toString() + "]", Toast.LENGTH_LONG).show();
        }
    }
    public void Go2(View v){
        if (urlText.getText().toString().isEmpty())
            Toast.makeText(getBaseContext(), "Сначала введите запрос!", Toast.LENGTH_SHORT).show();
        else {
            Uri uri = Uri.parse(urlL + urlText.getText().toString());
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(urlIntent, "Открыть Gik в браузере)"));
        }

    }
    public void clearText(View v){
        urlText.setText("");
    }
    public void VisitGit(){
        Uri uri = Uri.parse("https://github.com/SnowVolf/GikGoogle/");
        Intent gitIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(Intent.createChooser(gitIntent, "Изучить исходники"));
    }
}
