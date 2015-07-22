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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Romil
 * 
 */
public class SortedSmilies extends Activity {

	char hiCount, happyCount, sadCount, angryCount;
	TextView resultText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sort);
		resultText = (TextView) findViewById(R.id.tvSortedResult);

		Button resultFetch = (Button) findViewById(R.id.bResultFetch);

		resultFetch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new EmailTask().execute();

			}
		});

	}

	public class EmailTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != "") {

				hiCount = result.charAt(0);
				happyCount = result.charAt(1);
				sadCount = result.charAt(2);
				angryCount = result.charAt(3);
				int hi, happy, sad, angry;

				hi = (int) hiCount;
				happy = (int) happyCount;
				sad = (int) sadCount;
				angry = (int) angryCount;

				int[] sorta = { hi, happy, sad, angry };

				for (int i = 0; i < 4; i++) {
					for (int j = i; j < 3; j++) {
						if (sorta[j] < sorta[j + 1]) {
							int temp;
							temp = sorta[j];
							sorta[j] = sorta[j + 1];
							sorta[j + 1] = temp;

						}
					}
				}

				resultText.setText("HI: " + hiCount + " HAPPY: " + happyCount
						+ " SAD: " + sadCount + " ANGRY: " + angryCount);

				Toast.makeText(getApplicationContext(), "Got it!!!!!",
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

				HttpPost httpPost = new HttpPost(
						"http://kakshya.in/chat_process.php");

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
