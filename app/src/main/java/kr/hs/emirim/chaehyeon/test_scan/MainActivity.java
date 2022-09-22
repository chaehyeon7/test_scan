package kr.hs.emirim.chaehyeon.test_scan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button startButton; // 시작버튼
    private Button stopButton;  // 중지버튼
    private Button resetButton; // 초기화버튼

    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    private boolean hasCoarseLocationPermission = false;
    private boolean hasFineLocationPermission = false;
    private boolean hasBluetoothScanPermission = false;
    private boolean hasBluetoothConnectPermission = false;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback leScanCallback;

    private boolean scanning = false;

    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private ArrayList<ScanResult> scanResultArrayList;

    private ArrayList<String> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// setContentView로 xml 파일을 inflating 시켜주어야 id로 각 view를 찾아서 사용할 수 있다.
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        resetButton = findViewById(R.id.reset_button);

        startButton.setOnClickListener(v -> start());
        stopButton.setOnClickListener(v -> stop());
        resetButton.setOnClickListener(v -> reset());

        listView = findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);
        scanResultArrayList = new ArrayList<>();

    }
}