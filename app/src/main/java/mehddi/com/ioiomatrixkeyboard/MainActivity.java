package mehddi.com.ioiomatrixkeyboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;

import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class MainActivity extends IOIOActivity {
    Keypad keypad;
    private AtomicReference<String> keypadVal=null;
    private EditText input_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_textView = (EditText) findViewById(R.id.textInput);
    }

    /**
     * Show UI messages from Background threads
     * @param message
     * @param length
     */
    protected void displayToast(final String message, final int length){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,length).show();
            }
        });
    }

    /**
     * IOIO main looper class
     */
    class Looper extends BaseIOIOLooper {

        @Override
        public void setup() {

            try {
                setupConnections();
            }
            catch(Exception e){
                Log.e("Error IOIO setup", e.getMessage());
            }
        }

        private void setupConnections(){
            try{
                keypad = new Keypad();
                keypadVal = new AtomicReference<>(null);
                keypad.setup(ioio_,keypadVal);
                displayToast("KEYPAD Connected",Toast.LENGTH_SHORT);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void loop() {

            try {
                if (keypad != null)
                    doKeypadInput();
            }
            catch(Exception e){
                Log.e("Error IOIO loop",e.toString());
            }
        }
        @Override
        public void incompatible(){
            super.incompatible();

            try{
                displayToast("IOIO INCOMPATIBLE",Toast.LENGTH_LONG);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void disconnected() {
            super.disconnected();
            displayToast("KEYPAD Disconnected",Toast.LENGTH_SHORT);

        }
        private void doKeypadInput(){
            synchronized (keypad) {
                keypad.setKeypadInput();
            }

            //String keypadVal=Keypad.setKeypadInput();
            if(keypadVal.get()!=null && keypadVal.get().length()>0) {
                final String str = String.format(keypadVal.get());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appendText(str);
                        }
                    });
                }

        }
    }

    /**
     * IOIO Activity Override method for creating a looper object
     * @return
     */
    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }


    /**
     * Display text on UI on press of button
     * @param str
     */
    private void appendText(String str) {
        if(str.equals(";CLR"))
            input_textView.setText("");
        else
            input_textView.append(str);
    }


}
