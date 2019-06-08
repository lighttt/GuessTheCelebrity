package np.com.manishtuladhar.guessthecelebrity;

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

    //storing the content
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();

    // Game vairables
    int chosenCeleb = 1;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button0,button1,button2,button3;

/*
       ------------------------ Button Selected ---------------------
*/
    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Wrong! It was "+ celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        createNewQuestion();
    }

/*
       ------------------------ Image Downloading ---------------------
*/
    public class ImageDownloader extends  AsyncTask<String,Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

/*
        ----------------------- Content Downloading ---------------------
*/
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result =" ";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                 url = new URL(urls[0]);

                 urlConnection = (HttpURLConnection)url.openConnection();

                 InputStream in = urlConnection.getInputStream();

                 InputStreamReader reader = new InputStreamReader(in);

                 int data = reader.read();

                 while(data != -1){

                     char current = (char)data;

                     result = result + current;

                     data = reader.read();
                 }
                 return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

/*
        ----------------------- Executing the Downloading ---------------------
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);

        /*
        ----------------------- Content Downloading ---------------------
        */

        DownloadTask contentTask = new DownloadTask();
        String result = null;

        try {

            // writing the url
            result = contentTask.execute("http://www.posh24.se/kandisar").get();

            // spliting the url to the desired
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            // now checking with the string
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        createNewQuestion();
    }

/*
        ----------------------- Creating each question  ---------------------
*/

    public void createNewQuestion(){

        // creating the random vairable and assign a random value to chosenleb
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        /*
             ----------------------- Image Downloading ---------------------
        */
        ImageDownloader imageTask = new ImageDownloader();

        Bitmap celebImage;

        try
        {
            celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;

            for(int i=0; i<4;i++){

                if(i == locationOfCorrectAnswer){
                    answers[i] = celebNames.get(chosenCeleb);
                }
                else {
                    incorrectAnswerLocation = random.nextInt(celebURLs.size());

                    while (incorrectAnswerLocation == chosenCeleb)
                    {
                        incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
