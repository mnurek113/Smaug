package knab.com.smaug.bluetooth.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import knab.com.smaug.R;
import knab.com.smaug.SmaugApplication;
import knab.com.smaug.bluetooth.mvp.BTPresenter;
import knab.com.smaug.bluetooth.mvp.BluetoothMVP;
import knab.com.smaug.transmition.ui.TransmitionActivity;

public class StartActivity extends AppCompatActivity implements BluetoothMVP.View{

    private static final String TAG = "StartActivity";

    private ArrayList<BluetoothDevice> bluetoothDevicesList;
    private DeviceListAdapter deviceListAdapter;


    @BindView(R.id.btOnOff)
    Button btONOFF;
    @BindView(R.id.btDiscover)
    Button btnStartDiscovering;

    private ListView lvNewDevices;

    @Inject
    BTPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        bluetoothDevicesList = new ArrayList<>();

        ((SmaugApplication)getApplication()).getInjector().inject(this);

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);

        btONOFF.setOnClickListener(v-> presenter.btEnableDisable());
        btnStartDiscovering.setOnClickListener(v -> presenter.btEnableDiscovering());
        lvNewDevices.setOnItemClickListener((adapter, view, position, id)->pairDevices(position));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        unregisterReceiver(presenter.discoveredDevicesReceiver);
        unregisterReceiver(presenter.bondingDevicesReceiver);
    }

    private void pairDevices(int position) {

        BluetoothDevice device = bluetoothDevicesList.get(position);

            if(device.getName() != "HC-05"){
                if(!device.createBond())
                startNextActivity(device);
            }
            else{
                Log.d(TAG, "Błąd - sprawdź swoje urządzenie bluetooth HC-05");
                this.finish();
            }

        IntentFilter pairingDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(presenter.bondingDevicesReceiver, pairingDevicesIntent);
    }

    @Override
    public void enableBT() {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBTIntent);
        Log.d(TAG, "bluetooth enabled");
    }

    @Override
    public void disableBT() {
        Log.d(TAG, "bluetooth disabled");
    }

    @Override
    public void error() {
        Log.d(TAG, "bluetooth not available");
        Toast.makeText(this, "bluetooth is not avaliable", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void discoverDevices() {
        if(deviceListAdapter != null)
            deviceListAdapter.clear();
        IntentFilter btDiscoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(presenter.discoveredDevicesReceiver, btDiscoverIntent);

    }

    @Override
    public void listDevices(Context context, BluetoothDevice device) {
        bluetoothDevicesList.add(device);
        deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, bluetoothDevicesList);
        lvNewDevices.setAdapter(deviceListAdapter);
    }

    @Override
    public void startNextActivity(BluetoothDevice device) {

        Intent intent = new Intent(this, TransmitionActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
        this.finish();
    }
}
