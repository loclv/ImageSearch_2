package com.framgia.imagemanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.imagemanagement.adapters.GoogleImageBean;
import com.framgia.imagemanagement.adapters.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
implements AdapterView.OnItemClickListener {
    // number of image per page we want to get
    private final static byte imageResultNum = 8;
    //
    private final static byte VIEWMODE = 0;
    // name of solo image
    private TextView textViewSoloMsg;
    private GridView gridView;
    // adapter for gridView
    public ImageAdapter imageAdapter = null;
    // because not make one more a activity, take id of solo_image.xml
    public ImageView imageViewSoloImage;
    // save bundle backup for MainActivity
    public Bundle mBackupBundle;
    // string to search
    private String strSearch;
    // string from edited text
    private EditText editText;
    // search button
    private Button searchBttn;
    // list of images
    private ArrayList<Object> listImageObj;
    // start index for load the next page, startIndex = pageNum*8, n is number of pages
    private byte startIndex;
    // number of page
    private byte numPage = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //save savedInstanceState into myBackupBundle
        mBackupBundle = savedInstanceState;
        // set view as activity_main.xml
        setContentView(R.layout.activity_main);
        // set view to controller
        setFindViewById();
        // set a listener to search button
        setSearchButtonOnClickListener();
        // init image of index (ex: in page 1, the first image's index is 0)
        startIndex = 0;
    }
    public void setFindViewById() {
        // can edit the text in text box
        editText = (EditText)findViewById(R.id.editTextForSearch);
        // can click the search button
        searchBttn =(Button)findViewById(R.id.search_button);
    }
    private void setSearchButtonOnClickListener() {
        searchBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set event when click on search button
                btnSearchClick(v);
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // when click on a item of grid view, show the image detail dependent on image's position
        showImagedetail(position);
    }
    public void showImagedetail(int position) {
        // get information of a image by GoogleImageBean from image list
        GoogleImageBean imageBean = (GoogleImageBean)this.listImageObj.get(position);
        Intent intent = new Intent(MainActivity.this, InternetImageDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.imageUrl), imageBean.getThumbUrl());
        bundle.putString(getString(R.string.imageTitle), imageBean.getTitle());
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void setGridViewContent(ArrayList<Object> listImageObj) {
        gridView = (GridView) findViewById(R.id.gridView1);
        //setup data source for Adapter
        imageAdapter = new ImageAdapter(this, listImageObj);
        // set image's adapter to gridView
        gridView.setAdapter(imageAdapter);
        // setup event to see image's detail
        gridView.setOnItemClickListener(this);
    }
    public void btnSearchClick(View v) {
        // get string from edit text
        this.strSearch = this.editText.getText().toString();
        // encode that key work to add to the link
        this.strSearch = Uri.encode(this.strSearch);

        (new MainActivity.getImagesTask()).execute(new Void[0]);
    }
    public ArrayList<Object> getImageList(JSONArray resultArray) {
        // get a list of image
        ArrayList listImages = new ArrayList();
        try {
            for(int e = 0; e < resultArray.length(); ++e) {
                // get a json object from result of array
                JSONObject obj = resultArray.getJSONObject(e);
                GoogleImageBean bean = new GoogleImageBean();
                // get title information
                bean.setTitle(obj.getString("title"));
                // get image thumb URL information
                bean.setThumbUrl(obj.getString("tbUrl"));
                // add a bean (couple of information) to list images
                listImages.add(bean);
            }
            // return a list of image of URL and title
            return listImages;
        } catch (JSONException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public class getImagesTask extends AsyncTask<Void, Void, Void> {
        JSONObject json;
        ProgressDialog dialog;

        public getImagesTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // show a dialog tell to user wait for feedback's result
            this.dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.wait_msg));
        }

        protected Void doInBackground(Void... params) {
            createJsonObjFromUrl();
            return null;
        }

        protected void createJsonObjFromUrl() {
            try {
                StringBuilder builder = new StringBuilder();
                String line;
                // config the URL
                URL url = new URL(getString(R.string.base_google_image_api_request_link)
                    + ".0&q="
                    + MainActivity.this.strSearch
                    + "&rsz="
                    + imageResultNum
                    + "&start="
                    + startIndex);
                URLConnection e = url.openConnection();
                // add request property to the connection
                e.addRequestProperty("Referer", getString(R.string.fram_web));
                // read from connection
                BufferedReader reader = new BufferedReader(new InputStreamReader(e.getInputStream()));
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                // create a json object
                this.json = new JSONObject(builder.toString());
            } catch (MalformedURLException var7) {
                var7.printStackTrace();
            } catch (IOException var8) {
                var8.printStackTrace();
            } catch (JSONException var9) {
                var9.printStackTrace();
            }
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // if a dialog is showing
            if(this.dialog.isShowing()) {
                // dismiss that dialog
                this.dialog.dismiss();
            }
            getImageListAndShowIt();
        }

        protected void getImageListAndShowIt() {
            try {
                // get a json object
                JSONObject e = this.json.getJSONObject("responseData");
                // get result of array from json object
                JSONArray resultArray = e.getJSONArray("results");
                // get a list of image from result array
                listImageObj = MainActivity.this.getImageList(resultArray);
                // show image to the view
                setGridViewContent(listImageObj);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }
        }
    }
}
