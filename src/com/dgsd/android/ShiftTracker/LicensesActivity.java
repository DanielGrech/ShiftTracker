package com.dgsd.android.ShiftTracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.webkit.WebView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dgsd.android.ShiftTracker.Util.DiagnosticUtils;
import com.dgsd.android.ShiftTracker.Util.IntentUtils;

/**
 * @author Daniel Grech
 */
public class LicensesActivity extends SherlockActivity {
	private static final String TAG = LicensesActivity.class.getSimpleName();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.licenses);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

		WebView wv = new WebView(this);
		wv.loadUrl("file:///android_asset/about.html");

		setContentView(wv);
	}

    @Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
                StApp.doDefaultNavigateUp(this);
                return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
