package kr.hs.emirim.chaehyeon.test_scan;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            hasCoarseLocationPermission = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            hasFineLocationPermission = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            hasBluetoothScanPermission = result.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false);
            hasBluetoothConnectPermission = result.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false);

            if (hasCoarseLocationPermission && hasFineLocationPermission && hasBluetoothScanPermission && hasBluetoothConnectPermission) {
                enableBluetooth();
            } else {
                Log.e(TAG, "권한 없음.");
                startButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });

        enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.e(TAG, "블루투스 켜짐.");
                startButton.setEnabled(true);
                stopButton.setEnabled(true);
            } else {
                Log.e(TAG, "블루투스 꺼짐.");
                startButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });

        requestPermission();
    }

    /**
     * 권한 요청하는 함수
     */
    private void requestPermission() {
        hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        hasBluetoothScanPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        hasBluetoothConnectPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequests = new ArrayList<>();

        if (!hasCoarseLocationPermission)
            permissionRequests.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (!hasFineLocationPermission)
            permissionRequests.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (!hasBluetoothScanPermission)
            permissionRequests.add(Manifest.permission.BLUETOOTH_SCAN);

        if (!hasBluetoothConnectPermission)
            permissionRequests.add(Manifest.permission.BLUETOOTH_CONNECT);

        if (!permissionRequests.isEmpty()) {
            requestPermissionsLauncher.launch(permissionRequests.toArray(new String[0]));
        } else {
            enableBluetooth();
        }
    }

    /**
     * 블루투스를 켜는 함수
     */
    private void enableBluetooth() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "블루투스 켜져 있음.");
            startButton.setEnabled(true);
            stopButton.setEnabled(true);
            prepareBluetoothScan();
            return;
        }

    }

    private void prepareBluetoothScan() {
    }
}