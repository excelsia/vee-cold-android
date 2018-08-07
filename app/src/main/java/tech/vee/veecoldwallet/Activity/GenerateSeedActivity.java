package tech.vee.veecoldwallet.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class GenerateSeedActivity extends AppCompatActivity {
    private GenerateSeedActivity activity;
    private ActionBar actionBar;

    private TextView seedText;
    private TextView copy;
    private Button confirm;

    private String seed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_seed);

        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_seed);
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        actionBar.setTitle(R.string.title_generate_seed);

        seedText = findViewById(R.id.generate_seed_string);
        copy = findViewById(R.id.generate_seed_copy);
        confirm = findViewById(R.id.generate_seed_confirm);

        seed = VEEWallet.generateSeed();
        seedText.setText(formatSeed(seed));

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("SEED", seed);
                clipboard.setPrimaryClip(clip);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, BackupSeedActivity.class);
                intent.putExtra("SEED", seed);
                startActivity(intent);
            }
        });
    }

    private String formatSeed(String seed) {
        String[] words = seed.split(" ");
        String formattedSeed ="";
        for (int i=0; i<words.length; i++) {
            formattedSeed += words[i] + "    ";
        }
        return formattedSeed;
    }
}
