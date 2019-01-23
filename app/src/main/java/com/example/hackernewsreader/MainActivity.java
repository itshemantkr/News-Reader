package com.example.hackernewsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Map<Integer, String> articleURLs = new HashMap<Integer, String>();
    Map<Integer, String> articleTitles = new HashMap<Integer, String>();
    ArrayList<Integer> articleIds = new ArrayList<Integer>();

    SQLiteDatabase articlesDB;
    ArrayList<String>titles=new ArrayList<>();
    ArrayAdapter arrayAdapter;

    ArrayList<String> urls=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView=findViewById(R.id.listView);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),ArticleActivity.class);
                intent.putExtra("articleUrl",urls.get(position));
                startActivity(intent);


            }
        });


        articlesDB = this.openOrCreateDatabase("Articled", MODE_PRIVATE, null);

        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, url VARCHAR, title VARCHAR, content VARCHAR)");

        DownloadTask task=new DownloadTask();
        try {
            String result=task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

            JSONArray jsonArray=new JSONArray(result);

            articlesDB.execSQL("DELETE FROM articles");

            for(int i=0;i<20;i++){

                String articleId=jsonArray.getString(i);

                DownloadTask getArticle=new DownloadTask();

                String articleInfo=getArticle.execute("https://hacker-news.firebaseio.com/v0/item/"+articleId+".json?print=pretty").get();

                JSONObject jsonObject=new JSONObject(articleInfo);

                String articleTitle=jsonObject.getString("title");
                String articleURL=jsonObject.getString("url");

                articleIds.add(Integer.valueOf(articleId));
                articleTitles.put(Integer.valueOf(articleId),articleTitle);
                articleURLs.put(Integer.valueOf(articleId),articleURL);

                String sql="INSERT INTO articles (articleId,url,title) VALUES(?,?,?)";

                SQLiteStatement statement=articlesDB.compileStatement(sql);

                statement.bindString(1,articleId);
                statement.bindString(2,articleURL);
                statement.bindString(3,articleTitle);


               statement.execute();
            }

            Cursor c=articlesDB.rawQuery("SELECT * FROM articles ORDER BY articleId DESC",null);
            int articleIdIndex=c.getColumnIndex("articleId");
            int urlIndex=c.getColumnIndex("url");
            int titleIndex=c.getColumnIndex("title");

            c.moveToFirst();
            titles.clear();
            urls.clear();

            while(c!=null){

                titles.add(c.getString(titleIndex));
                urls.add(c.getString(urlIndex));
                c.moveToNext();
            }
            arrayAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try{

                url=new URL(strings[0]);

                urlConnection= (HttpURLConnection) url.openConnection();

                InputStream in=urlConnection.getInputStream();

                InputStreamReader reader=new InputStreamReader(in);

                int data= reader.read();
                while(data!=-1){

                    char current = (char)data;
                    result+=current;
                    data=reader.read();

                }


            }catch(Exception e){

                e.printStackTrace();

            }

            return result;
        }
    }


}
