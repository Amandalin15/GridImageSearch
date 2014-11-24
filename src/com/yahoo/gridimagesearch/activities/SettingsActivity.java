package com.yahoo.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.yahoo.gridimagesearch.R;
import com.yahoo.gridimagesearch.models.Settings;

public class SettingsActivity extends Activity {

    private Spinner sImageSize;
    private Spinner sColorFilter;
    private Spinner sImageType;
    private EditText etSiteFilter;

    private Settings advSettings;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get data from Intent
        advSettings = (Settings) getIntent().getSerializableExtra(SearchActivity.SETTINGS_KEY);

        setupViews();
    }

    @SuppressWarnings("unchecked")
	public void setupViews() {
        sImageSize = (Spinner) findViewById(R.id.sImageSize);
        sColorFilter = (Spinner) findViewById(R.id.sColorFilter);
        sImageType = (Spinner) findViewById(R.id.sImageType);
        etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);

        // Set data from settings object on the view
        int pos;

        arrayAdapter = (ArrayAdapter<String>) sImageSize.getAdapter();
        pos = arrayAdapter.getPosition(advSettings.getSize());
        sImageSize.setSelection(pos);

        arrayAdapter = (ArrayAdapter<String>) sColorFilter.getAdapter();
        pos = arrayAdapter.getPosition(advSettings.getColor());
        sColorFilter.setSelection(pos);

        arrayAdapter = (ArrayAdapter<String>) sImageType.getAdapter();
        pos = arrayAdapter.getPosition(advSettings.getType());
        sImageType.setSelection(pos);

        etSiteFilter.setText(advSettings.getSite());
    }

    public void onSaveSettings(View v) {
        // Validate etSiteFilter as a valid domain
        advSettings.setSize(sImageSize.getSelectedItem().toString());
        advSettings.setColor(sColorFilter.getSelectedItem().toString());
        advSettings.setType(sImageType.getSelectedItem().toString());
        advSettings.setSite(etSiteFilter.getText().toString());

        Intent intente = new Intent();
        intente.putExtra(SearchActivity.SETTINGS_KEY, advSettings);
        setResult(RESULT_OK, intente);
        finish();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
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
