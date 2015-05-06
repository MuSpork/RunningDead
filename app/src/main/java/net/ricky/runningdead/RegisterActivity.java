package net.ricky.runningdead;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
//needs to implement onClickListener for the Done button

public class RegisterActivity extends Activity implements View.OnClickListener {
    EditText userNameTextView;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        userNameTextView = (EditText) findViewById(R.id.userName);
        userNameTextView.setText(intent.getStringExtra("Username"));

        spinner = (Spinner) findViewById(R.id.genderDropDown);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(this);
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        //...

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    private void launchRegoSuccess(){
        Intent intent = new Intent(this,RegisterSuccessful.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Register register = new Register();
        register.execute(buildParameter());
    }

    public EditText[] findViewsById(){
        EditText[] edits = new EditText[5];
        edits[0] = (EditText) findViewById(R.id.firstName);
        edits[1] = (EditText) findViewById(R.id.lastName);
        edits[2] = (EditText) findViewById(R.id.emailAddress);
        edits[3] = (EditText) findViewById(R.id.password);
        edits[4] = (EditText) findViewById(R.id.dateOfBirth);

        return edits;
    }

    public String[] getTextsFromViews(EditText[] edits){
        ArrayList<String> strings = new ArrayList<String>();
        for(EditText texts : edits){
            strings.add(texts.getText().toString());
        }
        System.out.println("ArrayList = "+strings.toString());

        return Arrays.copyOf(strings.toArray(),strings.toArray().length,String[].class);
    }

    public String buildParameter(){
        String[] texts = getTextsFromViews(findViewsById());
        String body = "firstName="+texts[0]+"&" +
                "lastName="+texts[1]+"&" +
                "emailAddress="+texts[2]+"&" +
                "username="+userNameTextView.getText().toString()+"&" +
                "password="+texts[3]+"&" +
                "gender="+spinner.getSelectedItem().toString()+"&"+
                "dateOfBirth="+texts[4];
        System.out.println("Inside buildParamater = "+body);
        return body;
    }

    public class Register extends AsyncTask<String,Void,Boolean>{
        String request = "http://rickx.ddns.net/RegisterServlet";
        URL url = null;
        HttpURLConnection connection = null;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                url = new URL(request);
                connection = (HttpURLConnection) url.openConnection();
                System.out.println("String inside dobackground= "+params[0]);
                sendPost(params[0]);
                String response = readFromServlet();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            launchRegoSuccess();
            finish();
        }

        private String readFromServlet() throws IOException {
            BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return stream.readLine();
        }

        private void sendPost(String message) throws IOException {
            connection.setRequestMethod("POST");
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(message);
            wr.flush();
            wr.close();
        }
    }
}
