package knab.com.smaug.dagger;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import knab.com.smaug.bluetooth.bluetooth_facade.BluetoothFacade;

/**
 * Created by hp on 2017-07-27.
 */
@Module
public class PersistenceModule {

    private BluetoothAdapter bluetoothAdapter;

    @Provides
    @Singleton
    public BluetoothFacade provideBluetoothFacade(){
        return new BluetoothFacade(bluetoothAdapter.getDefaultAdapter());
    }

}
