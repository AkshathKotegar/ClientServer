package com.thecreator.ClientServer.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends ActionBarActivity {
	private TextView tvServerMessage;	
	Button send;	
	final private String SERVER_PORT = "8080";
	int id=0;
	private String Client_Name = "Bobby";	
	EditText textS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		final WifiManager myWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		tvServerMessage = (TextView) findViewById(R.id.textViewServerMessage);
		textS = (EditText)findViewById(R.id.editText1);
		send =(Button) findViewById(R.id.button1);		
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {								
				ClientAsyncTask clientAST = new ClientAsyncTask();
				clientAST.execute(new String[] {
						intToIP(myWifiManager.getDhcpInfo().gateway), SERVER_PORT,
				Client_Name+" : "+textS.getText().toString() });
			}
		});	
	}

	public String intToIP(int i) {
		return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF));
	}


	/**
	 * AsyncTask which handles the communication with the server
	 */
	class ClientAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {

				Socket socket = new Socket(params[0],
						Integer.parseInt(params[1]));

				InputStream is = socket.getInputStream();

				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);

				out.println(params[2]);

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));

				result = br.readLine();

				socket.close();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String s) {

			tvServerMessage.setText(s);
		}
	}
}