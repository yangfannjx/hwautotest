﻿package com.test.hwautotest.mms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.test.hwautotest.mms.Telephony.Mms;
import com.test.hwautotest.mms.Telephony.Threads;
import com.test.hwautotest.mms.Telephony.Mms.Addr;
import com.test.hwautotest.mms.Telephony.Mms.Part;



public class MMSUtils {
	private static final String IMAGE_NAME_1 = "image_1.jpeg";
    private static final String IMAGE_NAME_2 = "image_2.jpeg";
    private static final String SMIL_TEXT_IMAGE = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"2000ms\"><text src=\"text_1.txt\" region=\"Text\"/><img src=\"%s\" region=\"Image\"/></par><par dur=\"2000ms\"><text src=\"text_2.txt\" region=\"Text\"/><img src=\"%s\" region=\"Image\"/></par></body></smil>";
    private static final String IMAGE_CID = "<img_cid>";

    private static final String AUDIO_NAME = "audio_1.ogg";
    private static final String SMIL_TEXT_AUDIO = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"21500ms\"><text src=\"text_1.txt\" region=\"Text\"/><audio src=\""
            + AUDIO_NAME + "\" dur=\"21500\" /></par></body></smil>";
    private static final String AUDIO_CID = "<300k>";

    private static final String VIDEO_NAME = "video_1.3gp";
    private static final String SMIL_TEXT_VIDEO = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"21500ms\"><text src=\"text_1.txt\" region=\"Text\"/><VIDEO src=\""
            + VIDEO_NAME + "\" dur=\"21500\" /></par></body></smil>";
    private static final String VIDEO_CID = "<300k>";
    
    private static final String FROM_NUM = "146";
    
    public Context mContext;
	
	public MMSUtils(Context mContext){
		this.mContext = mContext;
		Log.i("look",mContext+"");
	}
    

	public void insert(int msgBoxType, AttachmentType type, String idx) {
		Log.i("look",mContext+"");
        long threadId = Threads.getOrCreateThreadId(mContext, idx);
        Log.e("look", "threadId = " + threadId);

        String name_1 = null;
        String name_2 = null;
        String smil_text = null;
        ContentValues cv_part_1 = null;
        ContentValues cv_part_2 = null;

        switch (type) {
        
            case IMAGE:
                name_1 = IMAGE_NAME_1;
                name_2 = IMAGE_NAME_2;
                smil_text = String.format(SMIL_TEXT_IMAGE, name_1, name_2);
                cv_part_1 = createPartRecord(0, "image/jpeg", name_1, IMAGE_CID, name_1, null, null);
                cv_part_2 = createPartRecord(0, "image/jpeg", name_2, IMAGE_CID.replace("cid", "cid_2"), name_2, null, null);
                break;
                
            case AUDIO:
                name_1 = AUDIO_NAME;
                smil_text = String.format(SMIL_TEXT_AUDIO, name_1);
                cv_part_1 = createPartRecord(0, "audio/ogg", AUDIO_NAME, AUDIO_CID, AUDIO_NAME, null, null);
                break;
                
            case VIDEO:
                name_1 = VIDEO_NAME;
                smil_text = String.format(SMIL_TEXT_VIDEO, name_1);
                cv_part_1 = createPartRecord(0, "video/3gpp", VIDEO_NAME, VIDEO_CID, VIDEO_NAME, null, null);
                break;
        }

        // make MMS record
        ContentValues cvMain = new ContentValues();
        cvMain.put(Mms.THREAD_ID, threadId);

        cvMain.put(Mms.MESSAGE_BOX, msgBoxType);
        cvMain.put(Mms.READ, 1);
        cvMain.put(Mms.DATE, System.currentTimeMillis() / 1000);
        cvMain.put(Mms.SUBJECT, "my subject " + idx);
        
        cvMain.put(Mms.CONTENT_TYPE, "application/vnd.wap.multipart.related");
        cvMain.put(Mms.MESSAGE_CLASS, "personal");
        cvMain.put(Mms.MESSAGE_TYPE, 132); // Retrive-Conf Mms
        cvMain.put(Mms.MESSAGE_SIZE, getSize(name_1) + getSize(name_2) + 512);  // suppose have 512 bytes extra text size
        cvMain.put(Mms.PRIORITY, String.valueOf(129));
        cvMain.put(Mms.READ_REPORT, String.valueOf(129));
        cvMain.put(Mms.DELIVERY_REPORT, String.valueOf(129));
        Random random = new Random();
        cvMain.put(Mms.MESSAGE_ID, String.valueOf(random.nextInt(100000)));
        cvMain.put(Mms.TRANSACTION_ID, String.valueOf(random.nextInt(120000)));

        long msgId = 0;
        try {
            msgId = ContentUris.parseId(mContext.getContentResolver().insert(Mms.CONTENT_URI, cvMain));
        } catch (Exception e) {
            Log.e("", "insert pdu record failed", e);
            return;
        }

        // make parts
        ContentValues cvSmil = createPartRecord(-1, "application/smil", "smil.xml", "<siml>", "smil.xml", null, smil_text);
        cvSmil.put(Part.MSG_ID, msgId);

        cv_part_1.put(Part.MSG_ID, msgId);
        cv_part_2.put(Part.MSG_ID, msgId);

        ContentValues cv_text_1 = createPartRecord(0, "text/plain", "text_1.txt", "<text_1>", "text_1.txt", null, null);
        cv_text_1.put(Part.MSG_ID, msgId);
        cv_text_1.remove(Part.TEXT);
        cv_text_1.put(Part.TEXT, "slide 1 text");
        cv_text_1.put(Part.CHARSET, "106");

        ContentValues cv_text_2 = createPartRecord(0, "text/plain", "text_2.txt", "<text_2>", "text_2.txt", null, null);
        cv_text_2.put(Part.MSG_ID, msgId);
        cv_text_2.remove(Part.TEXT);
        cv_text_2.put(Part.TEXT, "slide 2 text");
        cv_text_2.put(Part.CHARSET, "106");

        // insert parts
        Uri partUri = Uri.parse("content://mms/" + msgId + "/part");
        try {
        	mContext.getContentResolver().insert(partUri, cvSmil);

            Uri dataUri_1 = mContext.getContentResolver().insert(partUri, cv_part_1);
            if (!copyData(dataUri_1, name_1)) {
                Log.e("", "write " + name_1 + " failed");
                return;
            }
            mContext.getContentResolver().insert(partUri, cv_text_1);

            Uri dataUri_2 = mContext.getContentResolver().insert(partUri, cv_part_2);
            if (!copyData(dataUri_2, name_2)) {
                Log.e("", "write " + name_2 + " failed");
                return;
            }
            mContext.getContentResolver().insert(partUri, cv_text_2);
        } catch (Exception e) {
            Log.e("", "insert part failed", e);
            return;
        }

        // to address
        ContentValues cvAddr = new ContentValues();
        cvAddr.put(Addr.MSG_ID, msgId);
        cvAddr.put(Addr.ADDRESS, "703");
        cvAddr.put(Addr.TYPE, "151");
        cvAddr.put(Addr.CHARSET, 106);
        mContext.getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);

        // from address
        cvAddr.clear();
        cvAddr.put(Addr.MSG_ID, msgId);
        cvAddr.put(Addr.ADDRESS, FROM_NUM + idx);
        cvAddr.put(Addr.TYPE, "137");
        cvAddr.put(Addr.CHARSET, 106);
        mContext.getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);
    }

    private int getSize(final String name) {
        InputStream is = null;
        int size = 0;

        try {
            is = mContext.getAssets().open(name);
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = is.read(buffer)) != -1;)
                size += len;
            return size;
        } catch (FileNotFoundException e) {
            Log.e("", "failed to found file?", e);
            return 0;
        } catch (IOException e) {
            Log.e("", "write failed..", e);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                Log.e("", "close failed...");
            }
        }
        return 0;
    }

    private ContentValues createPartRecord(int seq, String ct, String name, String cid, String cl, String data,
            String text) {
        ContentValues cv = new ContentValues(8);
        cv.put(Part.SEQ, seq);
        cv.put(Part.CONTENT_TYPE, ct);
        cv.put(Part.NAME, name);
        cv.put(Part.CONTENT_ID, cid);
        cv.put(Part.CONTENT_LOCATION, cl);
        if (data != null)
            cv.put(Part._DATA, data);
        if (text != null)
            cv.put(Part.TEXT, text);
        return cv;
    }

    private boolean copyData(final Uri dataUri, final String name) {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = mContext.getAssets().open(name);
            output = mContext.getContentResolver().openOutputStream(dataUri);

            byte[] buffer = new byte[1024];
            for (int len = 0; (len = input.read(buffer)) != -1;) 
                output.write(buffer, 0, len);
        } catch (FileNotFoundException e) {
            Log.e("", "failed to found file?", e);
            return false;
        } catch (IOException e) {
            Log.e("", "write failed..", e);
            return false;
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Log.e("", "close failed...");
                return false;
            }
        }
        return true;
    }
    
    
    enum AttachmentType {
        IMAGE, AUDIO, VIDEO,;
    }
}
