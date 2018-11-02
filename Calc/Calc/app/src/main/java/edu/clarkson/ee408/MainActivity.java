package edu.clarkson.ee408;


//OI!  Ignore overflow/underflow (-neg - neg = pos) parts of question, (or until the last thing)


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.clarkson.ee408.R;

public class MainActivity extends AppCompatActivity {
    private static final String PLAYER1_WIN = "Player 1 won.";
    private static final String PLAYER2_WIN = "Player 2 won.";

    private MediaPlayer mp;
    private TextView display;
    private Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bPlus, bMinus, bMult, bDiv, bClear, bEq, bRPara, bLPara;
    private TextView statusBar;

    private static boolean op1Set=false;
    private static int operand1=0;
    private static int op=-1; // -1: no op; 0: +; 1:-;2:*;3:/
    private static boolean calc=false;

    private static boolean lastWasError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0 = (Button) findViewById(R.id.button0);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        b6 = (Button) findViewById(R.id.button6);
        b7 = (Button) findViewById(R.id.button7);
        b8 = (Button) findViewById(R.id.button8);
        b9 = (Button) findViewById(R.id.button9);
        bPlus = (Button) findViewById(R.id.buttonPlus);
        bMinus = (Button) findViewById(R.id.buttonMinus);
        bMult = (Button) findViewById(R.id.buttonMult);
        bDiv = (Button) findViewById(R.id.buttonDiv);
        bClear = (Button) findViewById(R.id.buttonClear);
        bEq = (Button) findViewById(R.id.buttonEq);
        bLPara = (Button) findViewById(R.id.buttonLeftPara);
        bRPara = (Button) findViewById(R.id.buttonRightPara);

        display = (TextView) findViewById(R.id.display);
        statusBar = (TextView) findViewById(R.id.statusBar);

        View.OnClickListener charHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastWasError) {
                    lastWasError = false;
                    display.setText("");
                }
                Button btn = (Button) v;
                display.setText(display.getText().toString() + btn.getText().toString());
            }
        };

        View.OnClickListener evaluate = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(display.getText().toString() == "") return;
                statusBar.setText("");
                try {

                    //We set this now when there isn't actually an issue so we don't have to do it multiple times later.  If there is no exception, the display and the error flag is reset.
                    //If there is an error, the 'try' block will be exited before the display and flags can be reset, and they will be in the correct state for the error.
                    lastWasError = true;
                    display.setText("Error!");

                    int value = ParserII.DO(display.getText().toString());
                    display.setText(Integer.toString(value));

                    lastWasError = false;
                }
                catch (NumberFormatException e)
                {
                    statusBar.setText("Numbers too large to handle!");
                }
                catch (Exception e)
                {
                    if(e.getMessage().startsWith("malformed")) {
                        //display.setText("Syntax error!");
                        display.setText("Unable to evaluate expression!");
                        statusBar.setText(e.getMessage());
                    }
                    else if(e.getMessage().endsWith("divide by zero")) {
                        statusBar.setText("Unable to divide by 0!");
                    }
                    else {
                        statusBar.setText("Generic error: " + e.getMessage());
                    }
                }
            }
        };

        View.OnClickListener lNum = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (calc) {
                    calc = false;
                    display.setText("");
                }
                if (op != -1 && !op1Set) {
                    try {
                        operand1 = Integer.parseInt(display.getText().toString());
                        op1Set = true;
                        display.setText("");
                    }
                    catch(NumberFormatException e)
                    {
                        statusBar.setText("Number too long to preform computations with!");
                        operand1 = 0;
                        display.setText("0");
                        return;
                    }

                }

                String text;
                text = display.getText().toString() + b.getText().toString();

                //Check for leading 0's
                if(text.matches("0[0-9]")) text = text.substring(1);

                display.setText(text);
                statusBar.setText("");
            }
        };

        View.OnClickListener lOp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                if (op != -1 && op1Set) {
                    int result = 0;
                    switch (op) {
                        case 0: // +
                            result = operand1 + Integer.parseInt(display.getText().toString());
                            break;
                        case 1: // -
                            result = operand1 - Integer.parseInt(display.getText().toString());
                            break;
                        case 2: // *
                            result = operand1 * Integer.parseInt(display.getText().toString());
                            break;
                        case 3: // /
                            result = operand1 / Integer.parseInt(display.getText().toString());
                            break;
                    }
                    op = -1;
                    op1Set = false;
                    display.setText(Integer.toString(result));
                }
                if (op == -1 && !calc) {
                    if (b.getText().equals("+")) {
                        op = 0;
                    }
                    if (b.getText().equals("-")) {
                        op = 1;
                    }
                    if (b.getText().equals("*")) {
                        op = 2;
                    }
                    if (b.getText().equals("/")) {
                        op = 3;
                    }
                }
            }
        };

        View.OnClickListener lDo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (op1Set) {
                    try {
                        int result;
                        switch (op) {
                            case (0):
                                // note: need to expand to handle -,*, and /!!!
                                result = operand1 + Integer.parseInt(display.getText().toString());
                                break;

                            case (1):
                                result = operand1 - Integer.parseInt(display.getText().toString());
                                break;

                            case (2):
                                result = operand1 * Integer.parseInt(display.getText().toString());
                                break;

                            case (3):
                                int divisor = Integer.parseInt(display.getText().toString());

                                if (divisor == 0) {
                                    statusBar.setText("Div/0 Error!");
                                    display.setText("INF!");
                                    result = 0;
                                } else result = operand1 / divisor;
                                break;

                            default:
                                result = 0; //In case things go bad.
                        }
                        //Don't update the display if there was a div/0 error
                        if (statusBar.getText().equals("Div/0 Error!") == false)
                            display.setText(Integer.toString(result));
                        op = -1;
                        op1Set = false;
                        calc = true;
                    }
                    catch(NumberFormatException e)
                    {
                        statusBar.setText("Number too long to preform computations with!");
                        display.setText("0");
                    }

                }
            }
        };

        View.OnClickListener clearButton = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                display.setText("");
            }
        };

        b0.setOnClickListener(charHandler);
        b1.setOnClickListener(charHandler);
        b2.setOnClickListener(charHandler);
        b3.setOnClickListener(charHandler);
        b4.setOnClickListener(charHandler);
        b5.setOnClickListener(charHandler);
        b6.setOnClickListener(charHandler);
        b7.setOnClickListener(charHandler);
        b8.setOnClickListener(charHandler);
        b9.setOnClickListener(charHandler);

        bPlus.setOnClickListener(charHandler);
        bMinus.setOnClickListener(charHandler);
        bMult.setOnClickListener(charHandler);
        bDiv.setOnClickListener(charHandler);



        bRPara.setOnClickListener(charHandler);
        bLPara.setOnClickListener(charHandler);

        bClear.setOnClickListener(clearButton);

        bEq.setOnClickListener(evaluate);

        // starts the music
        mp = MediaPlayer.create(this, R.raw.ocean_waves);
        mp.start();

    }
}
