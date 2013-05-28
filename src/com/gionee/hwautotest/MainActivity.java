package com.gionee.hwautotest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gionee.hwautotest.calllog.CallLogActivity;
import com.gionee.hwautotest.contacts.ContactsActivity;
import com.gionee.hwautotest.filemanager.FileManagerActivity;
import com.gionee.hwautotest.mms.MMSActivity;
import com.gionee.hwautotest.sms.SMSActivity;
import com.gionee.hwautotest.txttoxls.TxtToXlsActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView mainlist = (ListView) findViewById(R.id.mainlist);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		String[] title = { "联系人容量设置", "短信容量设置", "通话记录容量设置", "文件管理器容量设置",
				"生成自动化测试报告","彩信容量设置"};
		String[] text = { "自动添加中文联系人、英文联系人以及清除全部联系人", "自动添加短信以及删除全部短信",
				"自动添加通话记录以及删除全部通话记录", "调整SD卡和内部管理器的容量", "自动生成xls的自动化测试报告",
				"自动添加彩信以及删除全部彩信"};

		for (int i = 0; i < title.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", title[i]);
			map.put("ItemText", text[i]);
			mylist.add(map);
		}

		// 生成适配器，数组===》ListItem
		SimpleAdapter mSchedule = new SimpleAdapter(this, mylist,
				R.layout.mainlist, new String[] { "ItemTitle", "ItemText" },
				new int[] { R.id.ItemTitle, R.id.ItemText });
		// 添加并且显示
		mainlist.setAdapter(mSchedule);

		mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent contactIntent = new Intent(MainActivity.this,
							ContactsActivity.class);
					startActivity(contactIntent);
					break;
				case 1:
					Intent smsIntent = new Intent(MainActivity.this,
							SMSActivity.class);
					startActivity(smsIntent);
					break;
				case 2:
					Intent calllogIntent = new Intent(MainActivity.this,
							CallLogActivity.class);
					startActivity(calllogIntent);
					break;
				case 3:
					Intent fileManagerIntent = new Intent(MainActivity.this,
							FileManagerActivity.class);
					startActivity(fileManagerIntent);
					break;
				case 4:
					Intent TextIntent = new Intent(MainActivity.this,
							TxtToXlsActivity.class);
					startActivity(TextIntent);
					break;
				case 5:
					Intent MmsIntent = new Intent(MainActivity.this,
							MMSActivity.class);
					startActivity(MmsIntent);
				default:
					break;
				}

			}
		});

	}
}
