package mehddi.com.ioiomatrixkeyboard;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by Mujtaba on 01/09/2017.
 */

public class MyDigitalOutput {
    private DigitalOutput pin;
    private int pin_no;

    public MyDigitalOutput(){}
    public MyDigitalOutput(int p_no, IOIO ioio, DigitalOutput.Spec.Mode mode){
        try {
            this.pin=ioio.openDigitalOutput(p_no,mode,false);
            this.pin_no=p_no;
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public void write(boolean val) throws ConnectionLostException{
        this.pin.write(val);
    }

    public int getPin_no() {
        return pin_no;
    }

    public void close(){
        this.pin.close();
    }
    public void setPin_no(int pin_no) {
        this.pin_no = pin_no;
    }
}
