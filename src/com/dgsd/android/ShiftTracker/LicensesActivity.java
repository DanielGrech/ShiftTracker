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
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
                Intent upIntent = new Intent(this, StApp.getHomeClass(this));
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.contact:
                startActivity(IntentUtils.newEmailIntent(getString(R.string.support_email),
                        DiagnosticUtils.getApplicationName(this) + " support", null, "Contact support"));
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
