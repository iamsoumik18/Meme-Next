package com.example.memenext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        loadMeme(new View(this));
    }


    private void loadMeme(View view){
        final ProgressBar bar = (ProgressBar) findViewById(R.id.loading);
        bar.setVisibility(View.VISIBLE);

        final String url ="https://meme-api.herokuapp.com/gimme";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            imageurl = response.getString("url");
                            ImageView memeImage = (ImageView) findViewById(R.id.memeImage);
                            Picasso.get().load(imageurl).into(memeImage, new com.squareup.picasso.Callback(){

                                @Override
                                public void onSuccess() {
                                    bar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    bar.setVisibility(View.GONE);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Oops! Something went wrong.",Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void shareMeme(View view) {
        Picasso.get().load(imageurl).into(new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                try {
                    intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(Intent.createChooser(intent,null));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    public void nextMeme(View view) {
        loadMeme(view);
    }



    public Uri getBitmapFromView(Bitmap bmp) throws IOException {
        Uri bmpUri = null;
        File file = new File(this.getExternalCacheDir(),
                Long.toString(System.currentTimeMillis())+".jpg");
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG,90,out);
        out.close();
        bmpUri = Uri.fromFile(file);
        return bmpUri;
    }

}