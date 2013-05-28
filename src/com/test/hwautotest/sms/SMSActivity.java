package com.test.hwautotest.sms;

import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gionee.hwautotest.R;

public class SMSActivity extends Activity {

	private Button addMessage;
	private EditText messageNumber;
	private Button clearMessage;
	private TextView messageTitle;
	private RadioGroup words;
	private RadioButton senventy;
	private RadioButton oneHundredsixty;
	private RadioButton eightHundreds;
	
	private RadioGroup type;
	private RadioButton inbox;
	private RadioButton sent;
	private RadioButton drag;
	private RadioGroup read;
	private RadioButton alreadyRead;
	private RadioButton unRead;
	
	private ProgressDialog m_pDialog;
	SMSUtils mMessageUtils = new SMSUtils(this);
	
	int messageType = 1;
	int wordsNumber = 0;
	int readSatuts = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		messageTitle = (TextView)findViewById(R.id.messageTitle);
		addMessage = (Button)findViewById(R.id.addMessage);
		clearMessage = (Button)findViewById(R.id.clearMessage);
		messageNumber = (EditText)findViewById(R.id.messageNumber);
		words = (RadioGroup)findViewById(R.id.words);
		senventy = (RadioButton)findViewById(R.id.senventy);
		oneHundredsixty = (RadioButton)findViewById(R.id.oneHundredsixty);
		eightHundreds = (RadioButton)findViewById(R.id.eightHundreds);
		
		type = (RadioGroup)findViewById(R.id.type);
		inbox = (RadioButton)findViewById(R.id.inbox);
		sent = (RadioButton)findViewById(R.id.sent);
		drag = (RadioButton)findViewById(R.id.drag);
		read = (RadioGroup)findViewById(R.id.read);
		alreadyRead = (RadioButton)findViewById(R.id.alreadyRead);
		unRead = (RadioButton)findViewById(R.id.unRead);
		
		messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
		
		words.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == senventy.getId()){
					wordsNumber = 0;
				}else if(checkedId == oneHundredsixty.getId()){
					wordsNumber = 1;
				}else if(checkedId == eightHundreds.getId()){
					wordsNumber = 2;
				}
			}
		});
	
		type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == inbox.getId()){
					messageType = 1;
				}else if(checkedId == sent.getId()){
					messageType = 2;
				}else if(checkedId == drag.getId()){
					messageType = 3;
				}
			}
		});
		
		read.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == alreadyRead.getId()){
					readSatuts = 1;
				}else if(checkedId == unRead.getId()){
					readSatuts = 0;
				}
			}
		});
		
		addMessage.setOnClickListener(new View.OnClickListener() {
		
		private String mNumber;
		private int number;

		@Override
		public void onClick(View v) {

			// TODO Auto-generated method stub
			try {
				mNumber = messageNumber.getText().toString();
				number = Integer.parseInt(mNumber);
				m_pDialog = new ProgressDialog(SMSActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_pDialog.setTitle("正在添加");
				m_pDialog.setMax(number);
				m_pDialog.setIndeterminate(false);
				m_pDialog.setCancelable(true);
				m_pDialog.show();
				ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(number,wordsNumber, readSatuts, messageType);
				asyncTask.execute(1000);	
			} catch (Exception e) {
				// TODO: handle exception
				if (mNumber != "") {
					mMessageUtils.DisplayToast("请输入整数");
				}

			}
			messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			
			}
		});
		clearMessage.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				// Query SMS
				Uri uriSms = Uri.parse("content://sms");
				getContentResolver().delete(uriSms, null, null);
				mMessageUtils.DisplayToast("清空短信完成");
				messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			}
		});
	}
	private class ProgressDailogAsyncTask extends AsyncTask<Integer, Integer, String> {
		
		private int number;
		private int wordsNumber;
		private int readSatuts;
		private int messageType;
		
		private ProgressDailogAsyncTask (int number,int wordsNumber,int readSatuts,int messageType){
			super();
			this.number = number;
			this.wordsNumber = wordsNumber;
			this.readSatuts = readSatuts;
			this.messageType = messageType;
		}
		
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int i = 0;
			Random mRandom = new Random();
			for (i = 0; i < number; i++) {
		
				try {
					mMessageUtils.insertSms(wordsNumber, readSatuts, messageType);
		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				publishProgress(i + 1);
		
			}
			return params[0].intValue() + "";
		
		}
		
		@Override
		protected void onPostExecute(String result) {
			m_pDialog.setTitle("添加完成");
			messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			m_pDialog.cancel();
		}
		
		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			// m_pDialog.setTitle("正在添加");
		}
		
		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			int vlaue = values[0];
			m_pDialog.setProgress(vlaue);
		}
		}
			
	
	
}
