package mehddi.com.ioiomatrixkeyboard;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by Mujtaba on 01/09/2017.
 */

public class MyDigitalInput {
    private DigitalInput pin;
    private int pin_no;

    public MyDigitalInput(){}
    public MyDigitalInput(int p_no, IOIO ioio, DigitalInput.Spec.Mode mode){
        try {
            this.pin=ioio.openDigitalInput(p_no,mode);
            this.pin_no=p_no;
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public boolean read() throws InterruptedException, ConnectionLostException{
        return this.pin.read();
    }
    public void waitForValue(boolean value) throws InterruptedException,ConnectionLostException{
        this.pin.waitForValue(value);
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
