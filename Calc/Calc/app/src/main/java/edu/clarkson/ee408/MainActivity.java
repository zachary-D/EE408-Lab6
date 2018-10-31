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

        b0.setOnClickListener(lNum);
        b1.setOnClickListener(lNum);
        b2.setOnClickListener(lNum);
        b3.setOnClickListener(lNum);
        b4.setOnClickListener(lNum);
        b5.setOnClickListener(lNum);
        b6.setOnClickListener(lNum);
        b7.setOnClickListener(lNum);
        b8.setOnClickListener(lNum);
        b9.setOnClickListener(lNum);

        bPlus.setOnClickListener(lOp);
        bMinus.setOnClickListener(lOp);
        bMult.setOnClickListener(lOp);
        bDiv.setOnClickListener(lOp);

        bEq.setOnClickListener(lDo);

        bClear.setOnClickListener(clearButton);

        // starts the music
        mp = MediaPlayer.create(this, R.raw.ocean_waves);
        mp.start();

    }
}
