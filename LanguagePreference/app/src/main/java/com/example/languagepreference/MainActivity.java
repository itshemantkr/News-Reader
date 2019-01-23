package com.example.languagepreference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView textView;
    public void setLanguage(String language){

        sharedPreferences.edit().putString("Language",language).apply();

        textView.setText(language);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.english) {
            setLanguage("English");
            return true;
        }
        else if (item.getItemId()== R.id.hindi) {
            setLanguage("Hindi");
            return true;
        }
        else
            return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView=findViewById(R.id.textView);
        sharedPreferences=this.getSharedPreferences("com.example.languagepreference", Context.MODE_PRIVATE);



        String language=sharedPreferences.getString("Language","");

        if(language=="") {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_btn_speak_now)
                    .setTitle("Choose a Language")
                    .setMessage("Which Language would you like?")
                    .setPositiveButton("English", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            setLanguage("English");

                        }
                    })
                    .setNegativeButton("Hindi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLanguage("Hindi");
                        }
                    })
                    .show();
        }else{

            textView.setText(language);
        }
    }
}
