package com.adibalwani.sbumealplanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class LoginActivity extends ActionBarActivity {
	EditText usernameEdit;
	EditText passwordEdit;
	EditText passwordConfirmEdit;
	Button mActionButtom;
	RequestTask requestTask;
	String html;
	String url1;
	String url2;
	String skey;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolBar);
		usernameEdit = (EditText) findViewById(R.id.login_username);
		passwordEdit = (EditText) findViewById(R.id.login_password);
		mActionButtom = (Button) findViewById(R.id.login_button);
		requestTask = new RequestTask();
		url1 = "https://services.jsatech.com/login.php?cid=129&";
		mActionButtom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestTask.execute(url1);
			}
		});
	}

	public void getSkey(Document document) {
		String username = usernameEdit.getText().toString().trim();
		String password = passwordEdit.getText().toString().trim();
		boolean fail = false;
		Elements forms = document.getElementsByTag("form");
		url2 = forms.attr("action");
		String formUrl = url2;
		String cid = "129";
		String save = "1";
		String wason = "";
		skey = "";
		Elements formNodes = document.getElementsByTag("input");
		//Toast.makeText(getApplicationContext(),formNodes.size() + "",Toast.LENGTH_SHORT).show();
		for(int i=0;i<formNodes.size();i++) {
			Element element = formNodes.get(i);
			if(element.attr("name").equalsIgnoreCase("skey")) {
				skey = element.attr("value");
				//Toast.makeText(getApplicationContext(), skey, Toast.LENGTH_SHORT).show();
			}
		}
		LoginTask loginTask= new LoginTask();
		loginTask.execute(url2,cid,skey,save,wason,username,password);
		requestTask = new RequestTask();
	}

	public void getBalance(Connection.Response res) {
		try {
			Document doc = res.parse();
			Elements scripts = doc.getElementsByTag("script");
			//if()
			Log.e("Response", scripts.get(0).toString());
			if(scripts.get(0).toString().contains("window.location.href='/login.php")) {

				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra("skey", skey);
				startActivity(intent);
				finish();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void gotoRegister(View view) {
//		Intent intent = new Intent(this, SignUpActivity.class);
//		startActivity(intent);
	}

	class RequestTask extends AsyncTask<String, String, String> {
		Document document;

		@Override
		protected String doInBackground(String... uri) {
			try {
				document = Jsoup.connect(uri[0]).get();//docBuilder.parse(new ByteArrayInputStream(result.getBytes(SA)));
			}  catch (IOException e) {
				return "fail";
			}

			return "success";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result.equalsIgnoreCase("success")) {
				getSkey(document);
			}

		}
	}

	class LoginTask extends AsyncTask<String, String, String> {
		Connection.Response res;
		@Override
		protected String doInBackground(String... uri) {
			try {
				res = Jsoup
						.connect(uri[0])
						.data("cid", uri[1], "skey", uri[2],"save",uri[3],"wason",uri[4],"loginphrase",uri[5],"password",uri[6])
						.method(Connection.Method.POST)
						.execute();
			}  catch (IOException e) {
				return "fail";
			}

			return "success";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result.equalsIgnoreCase("success")) {
				getBalance(res);
			}

		}
	}


}
