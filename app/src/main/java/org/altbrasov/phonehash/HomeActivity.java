package org.altbrasov.phonehash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by adi on 11/23/14
 */
public class HomeActivity extends Activity implements View.OnClickListener {

    private Button sendButton;
    private Button viewButton;

    private EditText hashEdit;
    private EditText messageEdit;

    private PublicApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        sendButton = (Button) findViewById(R.id.send);
        viewButton = (Button) findViewById(R.id.view);

        sendButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);

        hashEdit = (EditText) findViewById(R.id.hash);
        messageEdit = (EditText) findViewById(R.id.message);

        api = ApiHelper.getPublicApi();
    }

    //to a push notification test  on resume :)
    @Override
    protected void onResume() {
        super.onResume();
        boolean playServicesOk = GcmHelper.checkPlayServices(this);
        if (playServicesOk)
            GcmHelper.getAndGenerateRegId(this, new GcmHelper.GenerateRegIdListener() {
                @Override
                public void onGeneratedRegId(String regId) {
                    ApiHelper.getPublicApi().testPush(regId, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                sendMessage(messageEdit.getText().toString());

                break;
            case R.id.view:
                viewHash(hashEdit.getText().toString());
                break;
        }

    }

    private void viewHash(String hash) {
        Intent intent = new Intent(this, HashActivity.class);
        intent.putExtra(HashActivity.EXTRA_HASH, hash);
        startActivity(intent);

    }

    private void sendMessage(final String message) {
        sendButton.setText("sending");
        sendButton.setEnabled(false);
        api.sendMessage(message, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement jsonElement, Response response) {
                sendButton.setText("Send");
                sendButton.setEnabled(true);
                //check regex
                Toast.makeText(HomeActivity.this, "message sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                sendButton.setText("Send");
                sendButton.setEnabled(true);
                Toast.makeText(HomeActivity.this, "failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
