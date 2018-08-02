package tech.vee.veecoldwallet.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tech.vee.veecoldwallet.R;

public class GenerateSeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_seed);
    }

    // Copy to clipboard
    private void copy(String content) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("SEED", content);
        clipboard.setPrimaryClip(clip);
    }
}
