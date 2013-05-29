package com.test.hwautotest.txttoxls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gionee.hwautotest.R;

public class TxtToXlsActivity extends Activity {

	private Button ft_btn;
	private Button st_btn;
	private ProgressBar mProgressBar;
	private String filePath = getSDPath() + "/TestReport/";
	private XlsOperate xls = new XlsOperate(filePath);
	private File dir;
	private File caseTemplate;// 测试模板
	private String version;
	private String testReportName;
	private ArrayList<String> dirNames;
	private boolean isExist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txttoxsl);

		ft_btn = (Button) findViewById(R.id.button1);
		st_btn = (Button) findViewById(R.id.button2);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		version = SystemProperties.get("ro.gn.extvernumber");// 获得软件版本

		dir = new File(getSDPath() + "/TestReport");// 创建文件夹对象
		// 如果没有文件夹，创建文件夹
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(getSDPath() + "/dzsoftSmart/taskLogs/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dirNames = listAllFiles(TxtToXlsActivity.this, getSDPath() + "/dzsoftSmart/taskLogs/");

		ft_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				caseTemplate = new File(getSDPath()
						+ "/TestReport/ft_testCase.xls");// FT测试模板
				testReportName = version + "_AutoTestReport.xls";

				// 如果没有模板则拷贝模板
				if (!caseTemplate.exists()) {
					getReportTemplate("ft_testCase");
				}
				// 检查数据源
				isExist = true;
				for(int i = 0; i < dirNames.size(); i++){
					isExist = isExist && fileIsExists(dirNames.get(i) + "/taskSummary.txt") 
							&& fileIsExists(getSDPath() + "/TestReport/ft_testCase.xls");
					if(!isExist){
						break;
					}
				}	
				if (isExist) {
					new creatTestReport().execute("ft_testCase");// 异步写入数据
				} else {
					Toast.makeText(TxtToXlsActivity.this, "缺少生成报告的数据文件",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		st_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				caseTemplate = new File(getSDPath()
						+ "/TestReport/st_testCase.xls");// FT测试模板
				testReportName = version + "_StableTestReport.xls";

				// 如果没有模板则拷贝模板
				if (!caseTemplate.exists()) {
					getReportTemplate("st_testCase");
				}

				// 检查数据源
				isExist = true;
				for(int i = 0; i < dirNames.size(); i++){
					isExist = isExist && fileIsExists(dirNames.get(i) + "/taskSummary.txt") 
							&& fileIsExists(getSDPath() + "/TestReport/st_testCase.xls");
					Log.i("YANG", fileIsExists(dirNames.get(i) + "/taskSummary.txt") + "");
					if(!isExist){
						break;
					}
				}	
				if (isExist) {
					new creatTestReport().execute("st_testCase");// 异步写入数据
				} else {
					Toast.makeText(TxtToXlsActivity.this, "缺少生成报告的数据文件",
							Toast.LENGTH_SHORT).show();
				}	
				Log.i("YANG", "---------------end-------------------");
			}
		});

	}

	class creatTestReport extends AsyncTask<String, Integer, String> {
		/**
		 * AsyncTask的3个参数： 第一个是doInBackground的传入参数类型 第二个是onProgressUpdate的传入参数类型
		 * 第三个是doInBackground的返回值类型，此返回值是onPostExecute的传入参数
		 */
		@Override
		protected void onPreExecute() {
			mProgressBar.setProgress(0);// 进度条复位
			Toast.makeText(TxtToXlsActivity.this, "开始生成报告", Toast.LENGTH_SHORT)
					.show();
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setIndeterminate(true);// 设置为不可控进度条
		}

		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			publishProgress(0);
			
			for(int i = 0; i < dirNames.size(); i++){
				Log.i("YANG", "@@@@@@@@@@@@@@@@@@");
				ArrayList<String> taskSummarys = xls.ReadTxtFile(dirNames.get(i)
						+ "/taskSummary.txt");
				HashSet<String> set = xls.fillResult(taskSummarys, params[0]);
				xls.removeSheetRow(set, params[0]);
			}
			// 重命名文件
			new File(getSDPath() + "/TestReport/" + params[0] + ".xls")
					.renameTo(new File(getSDPath() + "/TestReport/"
							+ testReportName));
			
			publishProgress(100);
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			mProgressBar.setProgress(progress[0]);
		}

		protected void onPostExecute(String result) {
			Toast.makeText(TxtToXlsActivity.this, "完成", Toast.LENGTH_SHORT)
					.show();
			mProgressBar.setVisibility(View.GONE);
		}

		protected void onCancelled() {
			mProgressBar.setProgress(0);
		}
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		// new Environment().isExternalStorageRemovable();
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}

	public boolean fileIsExists(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return false;
		} else {
			return true;
		}
	}

	public void getReportTemplate(String templateName) {
		try {
			InputStream inputStream = getAssets().open(templateName + ".xls");
			FileOutputStream fos = new FileOutputStream(getSDPath()
					+ "/TestReport/" + templateName + ".xls");
			int count = 0;
			byte[] buffer = new byte[1024];
			while ((count = inputStream.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> listAllFiles(Context context,String dirName){// 列出目录下所有的文件夹
		ArrayList<String> dirNames = new ArrayList<String>();
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dirName.endsWith(File.separator)) {
			dirName = dirName + File.separator;
		}
		File dirFile = new File(dirName);
		if (!dirFile.isDirectory()) {
			Toast.makeText(context, "找不到目录：" + dirName, Toast.LENGTH_SHORT).show();
		}

		// 列出文件夹下所有的文件
		File[] files = dirFile.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				dirNames.add(files[i].toString());
				Log.i("YANG", files[i].toString());
			}
		}
		return dirNames;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
