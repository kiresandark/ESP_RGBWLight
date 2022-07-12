package com.example.espmechapp.BLE;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.espmechapp.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothMain {

    private static final boolean TODO = true;
    private MainActivity context;

    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;

    private ScanSettings scanSettings;
    private List<ScanFilter> filters = new ArrayList<>();

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            // ...do whatever you want with this found device
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }
    };


    public BluetoothMain(MainActivity activity) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.getBluetoothLeScanner();
        this.context = activity;
    }

    /**
     * Метод для сканирования
     * */
    public boolean startScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        scanner.startScan(filters, scanSettings, scanCallback);
        return false;
    }

    /**
     * Проверка отсканированный
     * */
    public boolean isScannBle() {
        if (scanner != null) {
            startScan();
            //Log.d(TAG, "scan started");
            return true;
        } else {
            //Log.e(TAG, "could not get scanner object");
        }
        return false;
    }

    /**
     * Сканирование по UUID
     * */
    public void scanForUUID() {
        UUID BLP_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
        UUID[] serviceUUIDs = new UUID[]{BLP_SERVICE_UUID};
        List<ScanFilter> filters = null;
        if (serviceUUIDs != null) {
            filters = new ArrayList<>();
            for (UUID serviceUUID : serviceUUIDs) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setServiceUuid(new ParcelUuid(serviceUUID))
                        .build();
                filters.add(filter);
            }
        }
        startScan();
    }

    /**
     * Сканирование по UUID
     * */
    public void scanForMAC() {
        String[] peripheralAddresses = new String[]{"01:0A:5C:7D:D0:1A"};
        // Build filters list
        List<ScanFilter> filters = null;
        if (peripheralAddresses != null) {
            filters = new ArrayList<>();
            for (String address : peripheralAddresses) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceAddress(address)
                        .build();
                filters.add(filter);
            }
        }
//            scanner.startScan(filters, scanSettings, scanByServiceUUIDCallback);
    }

    /**
     * тестовое
     * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void test() {
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();
    }

    public void newConnect() {
//            BluetoothGatt gatt = device.connectGatt(context, false,
//                    bluetoothGattCallback, TRANSPORT_LE);
    }

    /**
     * Проверка разрешений
     * */
    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST);
                return false;
            }
        }
        return true;
    }

    public void newBluetoothDevice() {
        BluetoothDevice device =
                adapter.getRemoteDevice("12:34:56:AA:BB:CC");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        BluetoothGatt gatt =
                device.connectGatt(context, true, bluetoothGattCallback, TRANSPORT_LE);
    }

    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
        } else {
            gatt.close();
        }
    }
}
