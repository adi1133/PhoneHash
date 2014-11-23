package org.altbrasov.phonehash;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by adi on 11/23/14
 */
public class HashActivity extends Activity implements Callback<String[]> {

    public static final String EXTRA_HASH = "hash";

    private ListView hashList;
    private PublicApi api;
    private String hash;
    private HashListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hash_activity);
        hashList = (ListView) findViewById(R.id.hash_list);
        api = ApiHelper.getPublicApi();
        adapter = new HashListAdapter();
        hashList.setAdapter(adapter);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri hashUri = getIntent().getData();
            hash = hashUri.toString().substring("phonehash://".length());
        } else if (extras != null) {
            hash = extras.getString(EXTRA_HASH);
        }


        if (!TextUtils.isEmpty(hash)) {
            if (!hash.startsWith("#"))
                hash = "#" + hash;
            api.getMessages(hash, this);
        }

    }

    @Override
    public void success(String[] messages, Response response) {
        adapter.setMessages(messages);
    }

    @Override
    public void failure(RetrofitError error) {

    }


    private class HashListAdapter extends BaseAdapter {

        private String[] messages;

        @Override
        public int getCount() {
            return messages == null ? 0 : messages.length;
        }

        @Override
        public String getItem(int position) {
            return messages[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(HashActivity.this);
            textView.setText(getItem(position));

            Pattern pattern = Pattern.compile("#\\w+");
            Linkify.addLinks(textView, pattern, "phonehash://");

            return textView;
        }


        public void setMessages(String[] messages) {
            this.messages = messages;
            notifyDataSetChanged();
        }
    }
}
