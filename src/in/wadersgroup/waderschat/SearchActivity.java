package in.wadersgroup.waderschat;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Romil
 *
 */
public class SearchActivity extends Activity {

	TextView resultText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);

		Button query = (Button) findViewById(R.id.bQuery);
		final EditText searchString = (EditText) findViewById(R.id.etSearchString);
		resultText = (TextView) findViewById(R.id.tvResult);

		query.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String queryText = searchString.getText().toString();
				if (queryText.contentEquals("")) {
					Toast.makeText(getApplicationContext(),
							"Please enter something to search",
							Toast.LENGTH_LONG).show();

				} else {
					new EmailTask().execute(queryText);
				}

			}
		});

	}

	public class EmailTask extends AsyncTask<String, Void, String> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(SearchActivity.this);
			super.onPreExecute();
			pDialog.setMessage("Searching...");
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != "") {
				pDialog.dismiss();

				String[] resultsArray = result.split("-");

				resultText.setText("Your file with filename: "
						+ resultsArray[1] + " is with user: " + resultsArray[0]
						+ ". You can request him to send you the "
						+ "file using the Chat option that we provide.");

				Toast.makeText(getApplicationContext(), result,
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
				String queryString = URLEncoder.encode(params[0], "utf-8");

				System.out.print(queryString);

				HttpPost httpPost = new HttpPost(
						"http://kakshya.in/file_search?fname=" + queryString);

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

}
