package tech.vee.veecoldwallet.Activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Wallet.VEEChain;

public class AboutUsActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TextView version;
    private byte chain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_vee);
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_about_us);

        TextView version = (TextView) findViewById(R.id.version);
        chain = intent.getByteExtra("CHAIN_ID", (byte)0);

        switch (chain) {
            case VEEChain.MAIN_NET:
                version.setText(R.string.version_main);
                break;
            case VEEChain.TEST_NET:
                version.setText(R.string.version_test);
                break;
        }
    }
}
