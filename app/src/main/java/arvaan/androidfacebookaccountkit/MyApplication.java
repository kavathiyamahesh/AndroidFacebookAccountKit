package arvaan.androidfacebookaccountkit;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by admin on 10/14/2016.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
