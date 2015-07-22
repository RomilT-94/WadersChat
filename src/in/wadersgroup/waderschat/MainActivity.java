package in.wadersgroup.waderschat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import common.Log;
import common.LogFragment;
import common.LogWrapper;
import common.MessageOnlyLogFilter;
import common.SampleActivityBase;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewAnimator;

/**
 * @author Romil
 *
 */
public class MainActivity extends SampleActivityBase {
	

	public static final String TAG = "MainActivity";

	// Whether the Log Fragment is currently shown
	private boolean mLogShown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button search = (Button) findViewById(R.id.bSearch);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(i);

			}
		});

		if (savedInstanceState == null) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			BluetoothChatFragment fragment = new BluetoothChatFragment();
			transaction.replace(R.id.sample_content_fragment, fragment);
			transaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
		logToggle
				.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
		logToggle.setTitle(mLogShown ? R.string.sample_hide_log
				: R.string.sample_show_log);

		return super.onPrepareOptionsMenu(menu);
	}

	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				Uri uri = data.getData();
				String uriString = uri.toString();
				String displayName = "";

				Log.d(TAG, "File Uri: " + uri.toString());
				File f = new File("" + uri);
				f.getName();

				Toast.makeText(getApplicationContext(), uriString,
						Toast.LENGTH_LONG).show();
				// Get the path
				if (uriString.startsWith("content://")) {
					Cursor cursor = null;
					try {
						cursor = getContentResolver().query(uri, null, null,
								null, null);
						if (cursor != null && cursor.moveToFirst()) {
							displayName = cursor
									.getString(cursor
											.getColumnIndex(OpenableColumns.DISPLAY_NAME));
						}
					} finally {
						cursor.close();
					}
				} else if (uriString.startsWith("file://")) {
					displayName = f.getName();
				}

				Toast.makeText(getApplicationContext(), displayName,
						Toast.LENGTH_LONG).show();

				if (displayName != null) {

					new EmailTask().execute(displayName,
							getLocalBluetoothName());

				}

				// Get the file instance
				// File file = new File(path);
				// Initiate the upload
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	BluetoothAdapter mBluetoothAdapter;
	String deviceName;

	public String getLocalBluetoothName() {
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		deviceName = mBluetoothAdapter.getName();
		if (deviceName == null) {
			System.out.println("Name is null!");
			deviceName = mBluetoothAdapter.getAddress();
		}
		return deviceName;
	}

	public class EmailTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != "") {

				Toast.makeText(getApplicationContext(),
						"Thank You for Sharing the file with us",
						Toast.LENGTH_LONG).show();

			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String res = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();

			try {
				String fileName = URLEncoder.encode(params[0], "utf-8");
				String deviceName = URLEncoder.encode(params[1], "utf-8");

				HttpPost httpPost = new HttpPost(
						"http://kakshya.in/file_upload?dname=" + deviceName
								+ "&fname=" + fileName);

				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				res = EntityUtils.toString(response.getEntity());

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return res;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.search_item:

			showFileChooser();

			break;

		case R.id.menu_toggle_log:
			mLogShown = !mLogShown;
			ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
			if (mLogShown) {
				output.setDisplayedChild(1);
			} else {
				output.setDisplayedChild(0);
			}
			supportInvalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Create a chain of targets that will receive log data */
	@Override
	public void initializeLogging() {
		// Wraps Android's native log framework.
		LogWrapper logWrapper = new LogWrapper();
		// Using Log, front-end to the logging chain, emulates android.util.log
		// method signatures.
		Log.setLogNode(logWrapper);

		// Filter strips out everything except the message text.
		MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
		logWrapper.setNext(msgFilter);

		// On screen logging via a fragment with a TextView.
		LogFragment logFragment = (LogFragment) getSupportFragmentManager()
				.findFragmentById(R.id.log_fragment);
		msgFilter.setNext(logFragment.getLogView());

		Log.i(TAG, "Ready");
	}
}
