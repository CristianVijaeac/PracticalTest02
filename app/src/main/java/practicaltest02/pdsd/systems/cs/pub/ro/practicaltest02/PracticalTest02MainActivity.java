package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText etServerPort;
    private EditText etClientPort;
    private EditText etClientAddress;
    private EditText etWord;

    private TextView tvDefinition;

    private Button btConnect;
    private Button btGetWord;

    private ServerThread serverThread;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        initView();
    }

    private void initView() {
        etServerPort = findViewById(R.id.server_port_edit_text);
        etClientAddress = findViewById(R.id.client_address_edit_text);
        etClientPort = findViewById(R.id.client_port_edit_text);
        etWord = findViewById(R.id.city_edit_text);

        tvDefinition = findViewById(R.id.weather_forecast_text_view);

        btConnect = findViewById(R.id.connect_button);
        btGetWord = findViewById(R.id.get_weather_forecast_button);

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPort = etServerPort.getText().toString();
                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }
                serverThread.start();
            }
        });

        btGetWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientAddress = etClientAddress.getText().toString();
                String clientPort = etClientPort.getText().toString();
                if (clientAddress == null || clientAddress.isEmpty()
                        || clientPort == null || clientPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (serverThread == null || !serverThread.isAlive()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String word = etWord.getText().toString();
                if (word == null || word.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                tvDefinition.setText("");

                clientThread = new ClientThread(
                        clientAddress, Integer.parseInt(clientPort), word, tvDefinition);
                clientThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
