package com.yahoo.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.gridimagesearch.EndlessScrollListener;
import com.yahoo.gridimagesearch.R;
import com.yahoo.gridimagesearch.adapters.ImageResultsAdapter;
import com.yahoo.gridimagesearch.models.ImageResult;
import com.yahoo.gridimagesearch.models.Settings;

public class SearchActivity extends Activity {
    public final static String SETTINGS_KEY = "settings";
    public final static String GOOGLE_IMAGE_SEARCH_API_SCHEME = "https";
    public final static String GOOGLE_IMAGE_SEARCH_API_AUTHORITY = "ajax.googleapis.com";
    public final static String GOOGLE_IMAGE_SEARCH_API_PATH = "/ajax/services/search/images";
    public final static int IMAGE_RESULT_SIZE = 8;

    public final static int EDIT_SETTINGS_REQUEST_CODE = 1;

    private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
    private Settings advSettings;
    private String searchQuery;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		// create the data source
		imageResults = new ArrayList<ImageResult>();
		// attach the data source to an adapter
		aImageResults = new ImageResultsAdapter(this, imageResults);
		// Link the adapter to the adapterview (gridview)
		gvResults.setAdapter(aImageResults);
		advSettings = new Settings();
	}

	private void setupViews() {
		etQuery = (EditText)findViewById(R.id.etQuery);
		gvResults = (GridView)findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Launch the image display activity
				// Create an intent
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				// get the image result to display
				ImageResult result = imageResults.get(position);
				// pass the image result to the intent
				i.putExtra("result", result); // need to either be serializable or parcelable
				// Launch the new activity
				startActivity(i);
			}
			
		});
		
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
                if (page >0 && page < 8) {
                    int offset = page * IMAGE_RESULT_SIZE;
                    AsyncHttpClient client = new AsyncHttpClient();

                    client.get(buildRequestUrl(searchQuery, offset, advSettings),
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    JSONArray imageJsonResults = null;
                                    try {
                                        imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
                                        aImageResults.addAll(ImageResult.fromJSONArray(imageJsonResults));
                                        Log.d("DEBUG", imageResults.toString());
                                    } catch (JSONException e) {

                                    }
                                }
                            });
                }
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		
		return true;
	}
	
	// Fired whenever the button is pressed
	public void onImageSearch(View v) {
		String query = etQuery.getText().toString();
		searchQuery = query;
		// Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		// http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
		String searchUrl = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";
		searchUrl = this.buildRequestUrl(query, 0, advSettings);
		client.get(searchUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("DEBUG", response.toString());
				JSONArray imageResultJson = null;
				try {
					imageResultJson = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear(); // clear the existing (in case where its a new search)
					// when u make change to the adapter, it does modify the underlying data
					aImageResults.addAll(ImageResult.fromJSONArray(imageResultJson));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("INFO", imageResults.toString());
			}
		});
	}
	
    private String buildRequestUrl(String query, int offset, Settings advSettings) {
		// http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
		String searchUrl = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0"
							+ "&q=" + query
							+ "&rsz=" + String.valueOf(IMAGE_RESULT_SIZE)
							+ "&start=" + String.valueOf(offset);
        if (advSettings.getSize() != Settings.OPTION_ALL) {
        	searchUrl += ("&imgsz=" + advSettings.getSize());
        }

        if (advSettings.getColor() != Settings.OPTION_ALL) {
        	searchUrl += ("&imgcolor=" + advSettings.getColor());
        }

        if (advSettings.getType() != Settings.OPTION_ALL) {
        	searchUrl += ("&imgtype=" + advSettings.getType());
        }

        if (advSettings.getSite() != "") {
        	searchUrl += ("&as_sitesearch=" + advSettings.getSite());
        }

        Log.d("DEBUG", searchUrl);

        return searchUrl;
    }

	// Fired when settings button pressed
	public void onSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra(SETTINGS_KEY, advSettings);
        startActivityForResult(i, EDIT_SETTINGS_REQUEST_CODE);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_SETTINGS_REQUEST_CODE) {
            advSettings = (Settings) data.getSerializableExtra(SETTINGS_KEY);
        }
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
