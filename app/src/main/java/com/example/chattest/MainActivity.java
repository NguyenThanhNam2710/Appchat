package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private Button btnSendUserName, btnChat;
    private EditText edtUserName;
    private ListView lvUserName, lvChat;
    private TextView tvOnline;
    ArrayList<String> mangUserName;
    ArrayList<String> mangChat;

    private void init() {
        btnSendUserName = (Button) findViewById(R.id.btnSendUserName);
        btnChat = (Button) findViewById(R.id.btnChat);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        lvUserName = (ListView) findViewById(R.id.lvUserName);
        lvChat = (ListView) findViewById(R.id.lvChat);
        tvOnline = (TextView) findViewById(R.id.tvOnline);
        mangChat = new ArrayList<String>();
    }

    {
        try {
            mSocket = IO.socket("http://192.168.0.103:3000");
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        mSocket.on("sever-register-result", onNewMessage_severRegisterResult);
        mSocket.on("sever-send-arrUserName", onNewMessage_severSendArrUserName);
        mSocket.on("sever-send-arrChat", onNewMessage_severSendArrChat);
        init();
        btnSendUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendUserName();
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendMessenger();
            }
        });
    }

    private Emitter.Listener onNewMessage_severSendArrChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String messenger;
                    String username;
                    try {
                        messenger = data.getString("messenger");
                        username = data.getString("username");
                        mangChat.add(username.toString() + ": " + messenger.toString());
                        Log.e("mangChat ", mangChat.size()+"");

                        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mangChat);
                        lvChat.setAdapter(arrayAdapter);
                    } catch (Exception e) {
                        Log.e("Error ", e.getMessage());
                        return;
                    }

                }
            });
        }
    };
    private Emitter.Listener onNewMessage_severSendArrUserName = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray arrUserName;
                    try {
                        arrUserName = data.getJSONArray("send");
                        mangUserName = new ArrayList<String>();
                        for (int i = (arrUserName.length() - 1); i > -1; i--) {
                            mangUserName.add(arrUserName.get(i).toString());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mangUserName);
                        lvUserName.setAdapter(arrayAdapter);
                        tvOnline.setText("Online(" + arrUserName.length() + ")");
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };
    private Emitter.Listener onNewMessage_severRegisterResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String ketquaDangKyUn;
                    try {
                        ketquaDangKyUn = data.getString("result");

                        if (ketquaDangKyUn == "true") {
                            Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Đăng ký thất bại. \n" + ketquaDangKyUn.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private void attemptSendUserName() {
        String username = edtUserName.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(MainActivity.this, "Đăng ký tên trước khi  vào phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        edtUserName.setText("");
        mSocket.emit("cilent-send-username", username);
    }

    private void attemptSendMessenger() {
        String messenger = edtUserName.getText().toString().trim();
        if (TextUtils.isEmpty(messenger)) {
            Toast.makeText(MainActivity.this, "Nhập nội dung trước khi gửi", Toast.LENGTH_SHORT).show();
            return;
        }

        edtUserName.setText("");
        mSocket.emit("cilent-send-messenger", messenger);
    }

}