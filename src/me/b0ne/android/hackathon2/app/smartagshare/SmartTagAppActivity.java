package me.b0ne.android.hackathon2.app.smartagshare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.aioisystems.smarttagsample.SmartTag;

public class SmartTagAppActivity extends Activity {
    
    private NfcAdapter mAdapter = null;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    
    private SmartTagTask mTagTask;
    
    private static final SmartTag mSmartTag = new SmartTag();
    private static final int REQUEST_CROP_PICK = 1;
    private static final int REQUEST_PICK_CONTACT = 2;
    
    private ImageView imageView;
    private TextView textView;
    private Intent cropIntent = new Intent("com.android.camera.action.CROP");
    
    private String intentType;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        
        imageView = (ImageView)findViewById(R.id.imageview);
        textView = (TextView)findViewById(R.id.textview);
        
        //NFC機能初期設定
        try {
            mAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mAdapter != null) {
                mPendingIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                filter.addDataType("*/*");
                
                mFilters = new IntentFilter[]{
                        filter,
                    };
                mTechLists = new String[][]{new String[] { NfcF.class.getName() }};
            }
        } catch (Exception e) {
            Log.v("ERROR",e.getMessage());
        }
        
        Intent intent = getIntent();
        if (intent.getAction() == null) { // MainActivityからのIntent
            String type = intent.getStringExtra("TYPE");
            if (type.equals("image")) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_PICK_CONTACT);
            } else if (type.equals("text")) {
                setTextShare(intent.getStringExtra("TEXT"));
            }
        } else { // 共有からのIntent
            if (intent.getAction().equals(Intent.ACTION_SEND)) {
                intentType = intent.getType();
                if (intentType.equals("text/plain")) {
                    String shareTagText = intent.getExtras().getCharSequence(Intent.EXTRA_TEXT).toString();
                    setTextShare(shareTagText);
                } else if (intentType.equals("image/*")) {
                    Uri uri = Uri.parse(getIntent().getExtras().get("android.intent.extra.STREAM").toString());
                    if (uri != null) {
                        getTrimmingDialog(uri);
                    }
                }
            }
        }
    }

    private void setTextShare(final String text) {
        textView.setText(text);
        
        if (isURL(text)) { //URLの場合
            
            //URLを書きこむか、表示するだけか
            String[] dialogItem = new String[]{"URLを書きこむ","URLを表示する"};
            AlertDialog.Builder opDialog = new AlertDialog.Builder(this);
            opDialog.setTitle("Option");
            opDialog.setItems(dialogItem, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        mSmartTag.setFunctionNo(SmartTag.FN_WRITE_DATA);
                        mSmartTag.setWriteText(text);
                        break;
                    case 1:
                        mSmartTag.setFunctionNo(SmartTag.FN_DRAW_TEXT);
                        mSmartTag.setDrawText(text);
                        break;
                    }
                }
            }).create().show();
        } else { //普通のテキスト
            mSmartTag.setFunctionNo(SmartTag.FN_DRAW_TEXT);
            mSmartTag.setDrawText(text);
        }
    }

    private void getTrimmingDialog(Uri uri) {
        mSmartTag.setFunctionNo(SmartTag.FN_DRAW_CAMERA_IMAGE);
        cropIntent.setData(uri);
        
        //縦か横か、画像を切り抜く
        String[] dialogItem = new String[]{"Cutout Vertical","Cutout Horizontal"};
        AlertDialog.Builder opDialog = new AlertDialog.Builder(this);
        opDialog.setTitle("Option");
        opDialog.setItems(dialogItem, trimmingDialogListener).create().show();
    }

    private boolean isURL(String text) {
        String matchUrl = "^(https?|ftp)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";
        Pattern patt = Pattern.compile(matchUrl);
        Matcher matcher = patt.matcher(text);
        return matcher.matches();
    }
    
    private DialogInterface.OnClickListener trimmingDialogListener = 
        new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    cropIntent.putExtra("outputX", 192);
                    cropIntent.putExtra("outputY", 400);
                    cropIntent.putExtra("aspectX", 100);
                    cropIntent.putExtra("aspectY", 208);
                    break;
                case 1:
                    cropIntent.putExtra("outputX", 400);
                    cropIntent.putExtra("outputY", 192);
                    cropIntent.putExtra("aspectX", 208);
                    cropIntent.putExtra("aspectY", 100);
                    break;
                }
                cropIntent.putExtra("scale", true);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, REQUEST_CROP_PICK);
            }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CROP_PICK) {
                if (data != null) {
                    //画像表示
                    Bitmap viewBitmap = data.getExtras().getParcelable("data");
                    imageView.setImageBitmap(viewBitmap);
                    
                    //タグ書き込み用を作成
                    Bitmap tagBitmap = data.getExtras().getParcelable("data");
                    tagBitmap = MediaUtils.resizeBitamp(tagBitmap, 200, 96, true);
                    mSmartTag.setCameraImage(
                            MediaUtils.editBitmapForTag(tagBitmap));
                    
                }
            } else if (requestCode == REQUEST_PICK_CONTACT) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        getTrimmingDialog(uri);
                    }
                }
            }
        } else {
            Log.v("ERROR","result error:" + String.valueOf(requestCode));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(mAdapter != null){
            mAdapter.enableForegroundDispatch(
                    this, mPendingIntent, mFilters, mTechLists);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent){
        Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] idm = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        mSmartTag.selectTarget(idm, tag);
        
        //非同期処理クラス初期化
        mTagTask = new SmartTagTask(this, mSmartTag);
        mTagTask.execute();
    }
    
}