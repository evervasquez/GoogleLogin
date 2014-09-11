package ever.sonico.pe.googlelogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;


public class MyActivity extends Activity
        implements View.OnClickListener,GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    private ProgressDialog mConnectionProgressDialog;

    public static final String AUTH_SCOPES[] = {
            Scopes.PLUS_LOGIN,
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/developerssite" };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(AUTH_SCOPES)
                .build();

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        mConnectionProgressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Person user = mPlusClient.getCurrentPerson();
        mConnectionProgressDialog.dismiss();
        Toast.makeText(this,"login ctm",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mConnectionResult = result;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sign_in && !mPlusClient.isConnected()) {
            if (mConnectionResult == null) {
            } else {
                try {
                    mConnectionProgressDialog.setMessage("Signing in...");
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Intenta la conexión de nuevo.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK)
        {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }
}
