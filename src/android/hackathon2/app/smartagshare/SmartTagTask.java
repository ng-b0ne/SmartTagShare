package android.hackathon2.app.smartagshare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aioisystems.smarttagsample.SmartTag;

public class SmartTagTask extends AsyncTask<Void, Void, Void> {
    
    private static SmartTag mSmartTag;
    private static Context mContext;
    
    public SmartTagTask(Context context, SmartTag smartTag){
        mContext = context;
        mSmartTag = smartTag;
    }
    
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        mSmartTag.startSession();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {

        Exception error = mSmartTag.getLastError();
        if(error != null){
            Log.v("TEST","SmartTag Error!!!");
        }else{

            int function = mSmartTag.getFunctionNo();
            
            if (function == SmartTag.FN_DRAW_CAMERA_IMAGE) {
                Toast.makeText(mContext, "画像を書き込んだお",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
