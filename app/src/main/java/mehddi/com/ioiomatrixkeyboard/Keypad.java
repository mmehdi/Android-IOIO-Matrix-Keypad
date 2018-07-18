package mehddi.com.ioiomatrixkeyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;

/**
 * Created by Mujtaba on 18/07/2018.
 */

public class Keypad {


    private int []rows;
    private int []columns;

    private IOIO ioio_;
    private String [][] pinsMatrix;

    private List<MyDigitalOutput> ColumnPins;
    private List<MyDigitalInput> RowPins;


    private int InputAlphabet=0;
    private int columnDiff = 37;
    private int rowDiff = 31;

    private AtomicBoolean ispress = new AtomicBoolean(false);

    private AtomicReference<String> kVal=null;


    /**
     * This method should be called in the main activity IOIO setup
     * @param ioio_
     */
    public void setup(IOIO ioio_, AtomicReference<String> keypadVal){
        this.ioio_=ioio_;
        this.columns=new int[]{39,38,37};
        this.rows=new int[]{36,35,34,33,32,31};


        ColumnPins= new ArrayList<>();
        RowPins= new ArrayList<>();
        initMatrices();
        initGPIOLeds(this.ioio_);
        kVal=keypadVal;
    }

    public void initMatrices(){


        /*

        [row pin] [column pin] = [button on panel]

        [37][36]="1";
        [38][36]="2";
        [39][36]="3";
        [37][35]="4";
        [38][35]="5";
        [39][35]="6";
        [37][34]="7";
        [38][34]="8";
        [39][34]="9";
        [37][33]="A>";
        [38][33]="0";
        [39][33]="<Z";
        [37][32]=";CLR";
        [38][32]=";CODE";
        [39][32]=";CALL";
        [37][31]=";TRADE";
        [38][31]=";INFO";
        [39][31]=";Porter";

        */


        //memory optmize = for rows 0=31 and for columns 0=37
        pinsMatrix= new String [3][6];

        pinsMatrix[37-columnDiff][36-rowDiff]="1"; //0,5
        pinsMatrix[38-columnDiff][36-rowDiff]="2"; //
        pinsMatrix[39-columnDiff][36-rowDiff]="3";
        pinsMatrix[37-columnDiff][35-rowDiff]="4";
        pinsMatrix[38-columnDiff][35-rowDiff]="5";
        pinsMatrix[39-columnDiff][35-rowDiff]="6";
        pinsMatrix[37-columnDiff][34-rowDiff]="7";
        pinsMatrix[38-columnDiff][34-rowDiff]="8";
        pinsMatrix[39-columnDiff][34-rowDiff]="9";
        pinsMatrix[37-columnDiff][33-rowDiff]="A>";
        pinsMatrix[38-columnDiff][33-rowDiff]="0";
        pinsMatrix[39-columnDiff][33-rowDiff]="<Z";
        pinsMatrix[37-columnDiff][32-rowDiff]=";CLR";
        pinsMatrix[38-columnDiff][32-rowDiff]=";CODE";
        pinsMatrix[39-columnDiff][32-rowDiff]=";CALL";
        pinsMatrix[37-columnDiff][31-rowDiff]=";TRADE";
        pinsMatrix[38-columnDiff][31-rowDiff]=";INFO";
        pinsMatrix[39-columnDiff][31-rowDiff]=";Porter";

    }

    /*
    Nested for loops to get Matrix keypad button press.
     */
    public void setKeypadInput(){
        try {
            outerloop:
            for(MyDigitalOutput cPin:ColumnPins){

                cPin.write(false); //false for rows -  pull up resistor
                this.ioio_.sync();
                innerloop:
                for (MyDigitalInput rPin: RowPins) {
                    if(!rPin.read()) {
                        kVal.set(this.pinsMatrix[cPin.getPin_no()-columnDiff][rPin.getPin_no()-rowDiff]);
                        break innerloop;
                    }
                }
                cPin.write(true);
            }

            /**
             * extra features goes here. we have Alphabet entry buttons ;  >A and <Z
             */
            if(kVal.get()!=null && kVal.get().length()>0){
                if(!ispress.get()) { //last time not pressed to prevent continuous press
                    setAlphabet();
                    ispress.set(true);
                }
                else {
                    kVal.set(null);
                }
            }
            else if (ispress.get())
                ispress.set(false);
        }

        catch (Exception e){
            e.printStackTrace();

        }
    }

    private void setAlphabet() {

        if(kVal.get().contains("A>")){
            if(InputAlphabet==0) {
                InputAlphabet = (int)'A';
            }
            else if((char)InputAlphabet!='Z'){
                InputAlphabet++;
            }

            kVal.set(String.valueOf((char)InputAlphabet));
        }
        else if(kVal!=null && kVal.get().contains("<Z")){
            if(InputAlphabet==0) {
                InputAlphabet = (int)'Z';
            }
            else if((char)InputAlphabet!='A'){
                InputAlphabet--;
            }
            kVal.set(String.valueOf((char)InputAlphabet));
        }
        else
            InputAlphabet=0;
    }

    public void initGPIOLeds(IOIO ioio_){
        try {

            //RowPins.clear();
            for(int row:rows){
                System.out.println("adding row pin:"+row);
                MyDigitalInput rowInp = new MyDigitalInput(row, ioio_, DigitalInput.Spec.Mode.PULL_UP);
                RowPins.add(rowInp);
            }
            //ColumnPins.clear();
            for(int column:columns){
                System.out.println("adding column pin:"+column);
                MyDigitalOutput columnOut = new MyDigitalOutput(column, ioio_, DigitalOutput.Spec.Mode.NORMAL);
                ColumnPins.add(columnOut);

            }

        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    private void closePins(){
        try {

            for(MyDigitalOutput cPin:ColumnPins) {
                cPin.close();
            }
            for(MyDigitalInput rPin:RowPins) {
                rPin.close();
            }

        }
        catch (Exception e){
            e.printStackTrace();

        }
    }


    public void destroy(){
        try {
            ioio_=null;
            rows=null;
            columns=null;

            pinsMatrix=null;

            closePins();

            if(ColumnPins!=null)
                ColumnPins.clear();
            if(RowPins!=null)
                RowPins.clear();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
