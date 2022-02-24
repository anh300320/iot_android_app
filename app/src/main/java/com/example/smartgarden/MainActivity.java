package com.example.smartgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartgarden.client.Client;
import com.example.smartgarden.client.dto.GardenInfo;
import com.example.smartgarden.exceptions.UnauthenticatedException;
import com.example.smartgarden.object.GardenRealtimeData;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    private static final String WS_SERVER_URL = "ws://10.0.2.2:8081/ws";
    private String token;
    private TextView tvHumidityValue;
    private TextView tvTemperatureValue;
    private Gson gson;
    private Client client;
    private List<GardenInfo> gardens;
    private WebSocketClient webSocketClient;
    private Spinner gardenSpinner;
    private Switch autoWateringSwitch;
    private Switch waterControlSwitch;
    private TextView tvHumidityThresholdTag;
    private TextView tvHumidityTopThresholdTag;
    private SeekBar seekBarHumidityThreshold;
    private SeekBar seekBarHumidityTopThreshold;

    private ImageView imageView1;
    private ImageView imageView2;

    private GardenInfo currentGarden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        client = new Client();
        tvHumidityValue = findViewById(R.id.humidity_value);
        tvTemperatureValue = findViewById(R.id.temperature_value);
        gardenSpinner = findViewById(R.id.garden_spinner);
        autoWateringSwitch = findViewById(R.id.switch_control);
        waterControlSwitch = findViewById(R.id.switch_watering_control);
        tvHumidityThresholdTag = findViewById(R.id.seekbar_humidity_tag);
        seekBarHumidityThreshold = findViewById(R.id.seekbar_humidity_threshold);
        seekBarHumidityTopThreshold = findViewById(R.id.seekbar_humidity_top_threshold);
        tvHumidityTopThresholdTag = findViewById(R.id.seekbar_humidity_top_tag);

        imageView1 = findViewById(R.id.imageView7);
        imageView2 = findViewById(R.id.imageView8);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");

        gardens = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<GardenInfo>(this, R.layout.garden_spinner_dropdown_view, gardens);
        @SuppressLint("StaticFieldLeak")
        AsyncTask getGardenListTask = new AsyncTask<String, Void, List<GardenInfo>>() {
            @Override
            protected List<GardenInfo> doInBackground(String... voids) {
                try {
                    return client.getMyGardenList(token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<GardenInfo> listGardens) {
                gardens.addAll(listGardens);
                adapter.notifyDataSetChanged();
            }
        };
        String[] strs = {"asdqwe", "qweasd"};
        getGardenListTask.execute(strs);

        try {
            webSocketClient = listenToHumidityValue(token);
        } catch (UnauthenticatedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
            finish();
        }

        gardenSpinner.setAdapter(adapter);
        gardenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGarden = gardens.get(position);
                webSocketClient.send(String.valueOf(currentGarden.getGardenId()));

                visibilityReload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        autoWateringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentGarden.setAuto(isChecked);
                currentGarden.setWatering(false);
                visibilityReload();
                controlGarden(token, currentGarden.getGardenId(), isChecked, false, currentGarden.getHumidityThreshold(), currentGarden.getHumidityTopThreshold());
            }
        });

        waterControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentGarden.setWatering(isChecked);
                visibilityReload();
                controlGarden(token, currentGarden.getGardenId(), currentGarden.isAuto(), isChecked, currentGarden.getHumidityThreshold(), currentGarden.getHumidityTopThreshold());
            }
        });

        seekBarHumidityThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentThreshold = getResources().getString(R.string.seekbar_humidity_tag) + " " + progress + "%";
                tvHumidityThresholdTag.setText(currentThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int threshold = seekBar.getProgress();
                currentGarden.setHumidityThreshold(threshold);
                if(threshold > currentGarden.getHumidityTopThreshold()) {
                    currentGarden.setHumidityTopThreshold(threshold);
                    seekBarHumidityTopThreshold.setProgress(threshold);
                }

                controlGarden(token, currentGarden.getGardenId(), true, false, currentGarden.getHumidityThreshold(), currentGarden.getHumidityTopThreshold());
            }
        });

        seekBarHumidityTopThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentTopThreshold = getResources().getString(R.string.seekbar_humidity_top_tag) + " " + progress + "%";
                tvHumidityTopThresholdTag.setText(currentTopThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int threshold = seekBar.getProgress();
                currentGarden.setHumidityTopThreshold(seekBar.getProgress());
                if(threshold < currentGarden.getHumidityThreshold()) {
                    currentGarden.setHumidityThreshold(threshold);
                    seekBarHumidityThreshold.setProgress(threshold);
                }
                controlGarden(token, currentGarden.getGardenId(), true, false, currentGarden.getHumidityThreshold(), currentGarden.getHumidityTopThreshold());
            }
        });
    }

    public void visibilityReload() {
        autoWateringSwitch.setChecked(currentGarden.isAuto());
        waterControlSwitch.setChecked(currentGarden.isWatering());
        seekBarHumidityThreshold.setProgress(currentGarden.getHumidityThreshold());
        seekBarHumidityTopThreshold.setProgress(currentGarden.getHumidityTopThreshold());
        String currentThreshold = getResources().getString(R.string.seekbar_humidity_tag) + " " + currentGarden.getHumidityThreshold() + "%";
        tvHumidityThresholdTag.setText(currentThreshold);
        if(currentGarden.isAuto()) {
            waterControlSwitch.setVisibility(View.VISIBLE);
            seekBarHumidityThreshold.setVisibility(View.VISIBLE);
            tvHumidityThresholdTag.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);
        } else {
            waterControlSwitch.setVisibility(View.VISIBLE);
            seekBarHumidityTopThreshold.setVisibility(View.GONE);
            tvHumidityTopThresholdTag.setVisibility(View.GONE);
            seekBarHumidityThreshold.setVisibility(View.GONE);
            tvHumidityThresholdTag.setVisibility(View.GONE);
            imageView1.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
        }
    }

    public WebSocketClient listenToHumidityValue(String accessToken) throws UnauthenticatedException {

        if(accessToken == null) throw new UnauthenticatedException();
        URI uri = null;
        try {
            uri = new URI(WS_SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("SocketClient uri", "URISyntaxException: " + e.getMessage());
        }

        WebSocketClient webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.d("SocketClient", "connection established");
            }

            @Override
            public void onTextReceived(final String message) {
                Log.d("SocketClient received", "TextReceived: " + message);
                GardenRealtimeData data = gson.fromJson(message, GardenRealtimeData.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvHumidityValue.setText(String.valueOf(data.getHumidity()));
                        tvTemperatureValue.setText(String.valueOf(data.getTemperature()));
                        if(autoWateringSwitch.isChecked())
                        waterControlSwitch.setChecked(data.isWatering());
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.d("SocketClient received", "BytesReceived: " + Arrays.toString(data));
            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                Log.d("SocketClient error", "onException: " + e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.d("SocketClient", "onCloseReceived: ");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.addHeader("Authorization", "Bearer " + accessToken);
        webSocketClient.addHeader("Origin", WS_SERVER_URL);
        webSocketClient.connect();
        return webSocketClient;
    }

    private void controlGarden(String token, int gardenId, boolean isAuto, boolean isWatering, int humidityTheshold, int humidityTopThreshold) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask controlGardenTask = new AsyncTask<String, Integer, GardenInfo>() {
            @Override
            protected GardenInfo doInBackground(String... objects) {
                try {
                    return client.sendControlCommand(token, gardenId, isAuto, isWatering, humidityTheshold, humidityTopThreshold);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(GardenInfo gardenInfo) {
                if(gardenInfo == null) return;
                currentGarden = gardenInfo;
//                visibilityReload();
            }
        };

        String[] strs = {"asdqwe", "qweasd"};
        controlGardenTask.execute(strs);
    }
}
