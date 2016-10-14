package arvaan.androidfacebookaccountkit;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.tbruyelle.rxpermissions.RxPermissions;

public class MainActivity extends AppCompatActivity {

    public static int APP_REQUEST_CODE = 99;

    private enum LoginViaAccountKit {
        MOBILE_NUMBER, EMAIL
    }

    private LoginViaAccountKit loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Must be done during an initialization phase like onCreate
        RxPermissions.getInstance(this)
                .request(Manifest.permission.RECEIVE_SMS)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can receive now SMS
                    } else {
                        // Ooppss permission denied
                    }
                });
    }

    public void onLoginPhone(final View view) {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            Log.e("TAG", accessToken.getAccountId());
            //You already login, handle your code as you want
            retrieveInformationAccountKit(null);
        } else {
            //Handle new or logged out user
            loginType = LoginViaAccountKit.MOBILE_NUMBER;
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, APP_REQUEST_CODE);
        }
    }

    public void onLoginEmail(final View view) {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            Log.e("TAG", accessToken.getAccountId());
            //You already login, handle your code as you want
            retrieveInformationAccountKit(null);
        } else {
            //Handle new or logged out user
            loginType = LoginViaAccountKit.EMAIL;
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.EMAIL,
                            AccountKitActivity.ResponseType.TOKEN);

            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, APP_REQUEST_CODE);
        }
    }

    public void logoutAccountKit(final View view) {
        Log.e("TAG", "logoutAccountKit");
        AccountKit.logOut();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format("Success:%s...", loginResult.getAuthorizationCode());
                }
                retrieveInformationAccountKit(null);
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void retrieveInformationAccountKit(final View view) {

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                Log.e("TAG", "onSuccess");
                // Get Account Kit ID
                String accountKitId = account.getId();
                Log.e("TAG", "accountKitId : " + accountKitId);

                if (loginType == LoginViaAccountKit.MOBILE_NUMBER) {// Get phone number
                    try {
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
                        Log.e("TAG", "phoneNumberString : " + phoneNumberString);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "You are not login with Mobile number", Toast.LENGTH_LONG).show();
                    }

                } else { // Get email
                    try {
                        String email = account.getEmail();
                        Log.e("TAG", "email : " + email);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "You are not login with Email", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
                Log.e("TAG", "onError");
            }
        });
    }
}
