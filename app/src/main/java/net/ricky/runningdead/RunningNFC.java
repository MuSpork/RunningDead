package net.ricky.runningdead;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;


public class RunningNFC extends ActionBarActivity implements View.OnClickListener {

    private NfcAdapter nfcAdapter;
    private boolean writeModeEnabled;
    private Button writeButton;
    private EditText textField;
    private TextView statusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        writeButton = (Button)findViewById(R.id.writeButton1);
        writeButton.setOnClickListener(this);


        textField = (EditText) findViewById(R.id.textField1);

        statusView = (TextView)findViewById(R.id.statusView1);

        writeModeEnabled = false;

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC is available", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"NFC is not available", Toast.LENGTH_LONG).show();
        }
    }

    /*
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.example.hellonfcworld", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         *
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }*/




    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        /*
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"NFC intent received!",Toast.LENGTH_LONG).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage(("Checkpoint 1"));

            writeNdefMessage(tag, ndefMessage);
        }*/
        if(writeModeEnabled) {
            writeModeEnabled = false;

            // write to newly scanned tag
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        disableWriteMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_running_nfc, menu);
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
    /*
    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,RunningNFC.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentfilter = new IntentFilter[]{};


        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentfilter, null);

    }

    private void disableForegroundDispatchSystem(){

    }
/*
    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not NDEF Formatable!", Toast.LENGTH_SHORT).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        }catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }*/
    /*
    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
    {
        try {
            if(tag == null){
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_LONG);
                return;
            }
            Ndef ndef = Ndef.get(tag);

            if(ndef == null) {
                //format tag with the ndef format and writes the message.
                formatTag(tag,ndefMessage);
            }
            else {
                ndef.connect();
                if(!ndef.isWritable()){
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_LONG);

                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag written!", Toast.LENGTH_LONG);

            }

        }catch (Exception e){
            Log.e("writeNDEFMessage", e.getMessage());
        }
    }*/
        /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = NdefRecord.createTextRecord(null,content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});


        return ndefMessage;
    }*/

    @Override
    public void onClick(View v) {
        statusView.setText("WRITE MODE ENABLED, HOLD PHONE TO TAG");
        enableWriteMode();

    }


    private boolean writeTag(Tag tag) {

        // record that contains our custom data from textfield, using custom MIME_TYPE
        String textToSend = textField.getText().toString();
        byte[] payload = textToSend.getBytes();
        byte[] mimeBytes = "application/tag".getBytes(Charset.forName("US-ASCII"));
        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[] { record});

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    statusView.setText(" Read-only tag :-( ");
                    return false;
                }

                // work out how much space we need for the data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    statusView.setText("Tag doesn't have enough free space :-(");
                    return false;
                }

                ndef.writeNdefMessage(message);
                statusView.setText("Tag written successfully.");
                return true;
            } else {
                // attempt to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        statusView.setText("Tag written successfully!\nClose this app and scan tag.");
                        return true;
                    } catch (IOException e) {
                        statusView.setText("Unable to format tag to NDEF.");
                        return false;
                    }
                } else {
                    statusView.setText("Tag doesn't appear to support NDEF format.");
                    return false;
                }
            }
        } catch (Exception e) {
            statusView.setText("Failed to write tag");
        }

        return false;
    }


    /**
     * Force this Activity to get NFC events first before phone OS
     */
    private void enableWriteMode() {
        writeModeEnabled = true;

        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableWriteMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }
}

