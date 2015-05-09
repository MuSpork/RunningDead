package net.ricky.runningdead;

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
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class RunningNFC extends ActionBarActivity {

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
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
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"NFC intent received!",Toast.LENGTH_LONG).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage(("Checkpoint 1"));

            writeNdefMessage(tag, ndefMessage);
        }
    }

    @Override
    protected void onResume() {
        enableForegroundDispatchSystem();
        super.onResume();
    }
    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
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

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,MainMenu.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentfilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentfilter, null);

    }

    private void disableForegroundDispatchSystem(){

    }
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
    }

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
    }

    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = NdefRecord.createTextRecord(null,content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});


        return ndefMessage;
    }
}
