package com.example.taniajoseph.guessceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    Button butt1;
    Button butt2;
    Button butt3;
    Button butt4;
    Bitmap myImage;
    celebImg img = new celebImg();

    Random rand = new Random();
    String result = "";
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    ArrayList<String> ans = new ArrayList<>();

    int a = 0;
    int loc;



    public void chAns(View view) {
        if (view.getTag().toString().equals(Integer.toString(loc))){
            Toast toast = Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast incToast = Toast.makeText(getApplicationContext(),"Wrong, The answer is " + celebNames.get(a),Toast.LENGTH_SHORT);
            incToast.show();
        }

        genQues();

    }



    public class celebImg extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap mb = BitmapFactory.decodeStream(inputStream);

                return mb;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class celebInfo extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {


            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;


            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = (InputStream) urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "failed";
            }
                return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        butt1 = findViewById(R.id.button);
        butt2 = findViewById(R.id.button2);
        butt3 = findViewById(R.id.button3);
        butt4 = findViewById(R.id.button4);


        celebInfo name = new celebInfo();
        String result = null;

        try {
            result = name.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()){
                 celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(result);
            while(m.find()){
                celebNames.add(m.group(1));
            }





            myImage = img.execute(celebURLs.get(a)).get();
            image.setImageBitmap(myImage);

            genQues();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void genQues() {
        loc = rand.nextInt(4);

        int incAns ;

        ans.clear();
        celebImg img = new celebImg();
        a = rand.nextInt(celebURLs.size());

        try {
            myImage = img.execute(celebURLs.get(a)).get();
            image.setImageBitmap(myImage);

            for(int i = 0;i<4;i++){
                if(i==loc){
                    ans.add(celebNames.get(a));

                }
                else {
                    incAns= rand.nextInt(celebURLs.size());

                    while(incAns == loc){
                        incAns = rand.nextInt(celebURLs.size());
                    }

                    ans.add(celebNames.get(incAns));
                }
            }

            butt1.setText(ans.get(0));
            butt2.setText(ans.get(1));
            butt3.setText(ans.get(2));
            butt4.setText(ans.get(3));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
