package tech.vee.veecoldwallet.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import tech.vee.veecoldwallet.Account.VEEAccount;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Fragment.SettingsFragment;
import tech.vee.veecoldwallet.Fragment.WalletFragment;
import tech.vee.veecoldwallet.Util.QRCodeUtil;

public class ColdWalletActivity extends AppCompatActivity {
    private WalletFragment wallet;
    private SettingsFragment settings;
    private FragmentManager fragmentManager;

    private String qrContents;
    private ImageView qrCode;
    private Bitmap exportQRCode;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_wallet:
                    switchToFragment(wallet);
                    return true;
                case R.id.navigation_settings:
                    switchToFragment(settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wallet = new WalletFragment();
        settings = new SettingsFragment();
        fragmentManager = null;

        switchToFragment(wallet);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClickImportBtn(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScannerActivity.class);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        qrContents = result.getContents();

        if(result != null) {
            if(qrContents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
            else {
                //Toast.makeText(this, "Scanned: " + qrContents, Toast.LENGTH_LONG).show();
                String priKey = QRCodeUtil.parsePriKey(qrContents);
                VEEAccount account = new VEEAccount(false, priKey);
                Toast.makeText(this, "Private Key: " + account.getPriKey() +
                        "\n\nPublic Key: " + account.getPubKey() +
                        "\n\nAddress: " + account.getAddress(), Toast.LENGTH_LONG).show();
                qrCode = (ImageView)findViewById(R.id.qr_code);
                exportQRCode = QRCodeUtil.exportPubKeyAddr(account, 800);
                qrCode.setImageBitmap(exportQRCode);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Used to switch fragments when icon on bottom navigation menu is clicked
     * @param fragment
     */
    private void switchToFragment(Fragment fragment){
        if (fragment != null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container,fragment).commit();
        }
    }

}
