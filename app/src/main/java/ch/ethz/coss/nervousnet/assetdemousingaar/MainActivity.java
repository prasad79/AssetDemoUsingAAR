package ch.ethz.coss.nervousnet.assetdemousingaar;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVM;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

public class MainActivity extends AppCompatActivity implements RemoteCallback {


    public NervousnetVM nn_VM;
    private RadioGroup radioSurvey;
    private RadioButton radioButton;
    private Button btnSubmit, btnFetch;
    private TextView ansTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nn_VM = new NervousnetVM(getApplicationContext());

        final Switch mainSwitch;




        mainSwitch = (Switch) findViewById(R.id.switch1);
        if(nn_VM.getNervousnetState() == NervousnetVMConstants.STATE_RUNNING)
            mainSwitch.setChecked(true);
        else
            mainSwitch.setChecked(false);




        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mainSwitch.isChecked()) {

                    EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST));
                }
                else {
                    EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST));

                }
            }
        });




        radioSurvey = (RadioGroup) findViewById(R.id.radioSurvey);
        btnSubmit = (Button) findViewById(R.id.button);
        btnFetch = (Button) findViewById(R.id.Fetch);

        ansTV = (TextView) findViewById(R.id.textView3);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Check if Nervousnet Service is running
                if(nn_VM.getNervousnetState() == NervousnetVMConstants.STATE_RUNNING) {
                    // get selected radio button from radioGroup
                    int selectedId = radioSurvey.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);


                    //Nervousnet Survey Sensor parameter names.
                    String [] params = new String[] {
                            "surveyID",
                            "qID",
                            "question",
                            "type",
                            "option1",
                            "option2",
                            "option3",
                            "option4",
                            "chosenAnswer"
                    };
                    ArrayList<String> paramNames = new ArrayList<String>(Arrays.asList(params));
                    //Survey Object id = 10002 for the sensor. Name = SurveySensor.
                    SensorReading surveyReading = new SensorReading(10002, "SurveySensor", paramNames);

                    //Survey Object values Survey ID and Questions ID modify accodingly. Type parameter keep as 1 now.
                    String [] values = new String[] {
                            "survey-id-1",
                            "question-id-1",
                            "How useful is the Asset App",
                            "1",
                            "Not useful",
                            "Usefull",
                            "-",
                            "-",
                            "***replace chosenAnswer here***"
                    };

                    values[values.length-1] = ""+radioButton.getText();
                    ArrayList<String> valuesList = new ArrayList<String>(Arrays.asList(values));

                    //Set the values onto the survey reading object
                    surveyReading.setValues(valuesList);

                    //Write to database
                        nn_VM.store(surveyReading);
                        Toast.makeText(MainActivity.this,
                                "Written to database", Toast.LENGTH_SHORT).show();




                } else {
                    Toast.makeText(MainActivity.this,
                            "Nervousnet Service not running", Toast.LENGTH_SHORT).show();

                }


            }

        });

        btnFetch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Fetch survey reading from database. Result will bein Callback methods success or failure.
                try {
                    nn_VM.getReading((long)10002, MainActivity.this);
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }





            }

        });




    }


    @Override
    public void success(List<SensorReading> list) throws RemoteException {

        SensorReading sensorReading = list.get(list.size()-1);
        if(sensorReading != null) {
            ArrayList<String> retreivedValues = sensorReading.getValues();

            ansTV.setText("Survey Question: "+retreivedValues.get(1)+" \n Chosen Answer - "+retreivedValues.get(8));
            Toast.makeText(MainActivity.this,retreivedValues.get(1)+" - "+retreivedValues.get(8), Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(MainActivity.this,"Survey database empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failure(InfoReading infoReading) throws RemoteException {
        Toast.makeText(MainActivity.this,"Error: "+infoReading.getInfoCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
