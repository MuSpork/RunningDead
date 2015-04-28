package net.ricky.runningdead;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class HowToPlay extends ActionBarActivity implements View.OnClickListener {
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        Button learnToPlay = (Button) findViewById(R.id.nextButton);
        counter = 0;
        learnToPlay.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_how_to_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        TextView view = (TextView)findViewById(R.id.contextText);
        TextView title = (TextView)findViewById(R.id.headingTitle);
        Button nextButton = (Button)findViewById(R.id.nextButton);
        String[] resStrings = {getResources().getString(R.string.learning_2),getResources().getString(R.string.playing_2)};
        String[] titles = {getResources().getString(R.string.learning_1),getResources().getString(R.string.playing_1)};

        if(counter < resStrings.length){
            view.setText(resStrings[counter]);
            title.setText(titles[counter]);
            if(counter == resStrings.length-1){
                nextButton.setText(R.string.backToMain);
            }
        }
        else{
            /*Intent intent = new Intent(this,MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            finish();
        }
        counter++;
    }
}
