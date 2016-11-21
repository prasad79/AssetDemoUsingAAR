package ch.ethz.coss.nervousnet.assetdemousingaar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;

import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVM;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

public class MainActivity extends AppCompatActivity {


    public NervousnetVM nn_VM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nn_VM = new NervousnetVM(getApplicationContext());

        final Switch mainSwitch, accelerometerSwitch;
        final Switch batterySwitch , gyroSwitch, locationSwitch;

        mainSwitch = (Switch) findViewById(R.id.switch1);
        if(nn_VM.getState() == NervousnetVMConstants.STATE_RUNNING)
            mainSwitch.setChecked(true);
        else
            mainSwitch.setChecked(false);

        accelerometerSwitch = (Switch) findViewById(R.id.switch2);
        batterySwitch = (Switch) findViewById(R.id.batt);
        gyroSwitch = (Switch) findViewById(R.id.gyro);
        locationSwitch = (Switch) findViewById(R.id.loc);

        if(nn_VM.getState() == NervousnetVMConstants.STATE_RUNNING)
            mainSwitch.setChecked(true);
        else
            mainSwitch.setChecked(false);


        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mainSwitch.isChecked()) {

                    EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST));
                    accelerometerSwitch.setEnabled(true);
                    batterySwitch.setEnabled(true);
                    gyroSwitch.setEnabled(true);
                    locationSwitch.setEnabled(true);
                }
                else {
                    EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST));
                    accelerometerSwitch.setChecked(false);
                    batterySwitch.setChecked(false);
                    gyroSwitch.setChecked(false);
                    locationSwitch.setChecked(false);

                    accelerometerSwitch.setEnabled(false);
                    batterySwitch.setEnabled(false);
                    gyroSwitch.setEnabled(false);
                    locationSwitch.setEnabled(false);
                }
            }
        });

        accelerometerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte state = accelerometerSwitch.isChecked() ? NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW : NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
                NNLog.d("MainActivity", "Accelerometer state = "+state);

                EventBus.getDefault().post(new NNEvent((long) 0, state, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
            }
        });


        batterySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte state = batterySwitch.isChecked() ? NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW : NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
                NNLog.d("MainActivity", "batterySwitch state = "+state);

                EventBus.getDefault().post(new NNEvent((long) 1, state, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
            }
        });

        gyroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte state = gyroSwitch.isChecked() ? NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW : NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
                NNLog.d("MainActivity", "gyroSwitch state = "+state);

                EventBus.getDefault().post(new NNEvent((long) 2, state, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte state = locationSwitch.isChecked() ? NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW : NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
                NNLog.d("MainActivity", "locationSwitch state = "+state);

                EventBus.getDefault().post(new NNEvent((long) 3, state, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
            }
        });
    }
}
