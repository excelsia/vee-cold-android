package tech.vee.veecoldwallet.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

import tech.vee.veecoldwallet.Account.VEEAccount;
import tech.vee.veecoldwallet.Account.VEETransaction;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Fragment.SettingsFragment;
import tech.vee.veecoldwallet.Fragment.WalletFragment;
import tech.vee.veecoldwallet.Util.JsonUtil;
import tech.vee.veecoldwallet.Util.QRCodeUtil;

public class ColdWalletActivity extends AppCompatActivity {
    private static final String TAG = "Winston";

    private WalletFragment wallet;
    private SettingsFragment settings;
    private FragmentManager fragmentManager;

    private String qrContents;
    private ImageView qrCode;
    private Bitmap exportQRCode;

    private ArrayList<VEEAccount> accounts;
    private VEEAccount account;

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

        account = new VEEAccount(false, "EXSu2hma58fD662tcTY8Jy4xnrPjEMy9xk5Sd6uwiuws");
        Log.d(TAG, "Public key: " + account.getPubKey());
        accounts = new ArrayList<>();
        accounts.add(account);

        switchToFragment(wallet);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        qrContents = result.getContents();
        qrCode = wallet.getQrCodeView();

        if(result != null) {
            switch (QRCodeUtil.processQrContents(qrContents)) {
                case 0:
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    break;

                case 1:
                    HashMap<String, Object> jsonMap = JsonUtil.getJsonAsMap(qrContents);
                    //Toast.makeText(this, jsonMap.toString(), Toast.LENGTH_LONG).show();

                    byte txType = -1;
                    VEETransaction transaction = null;

                    if (jsonMap.containsKey("transactionType")) {
                        txType = Double.valueOf((double)jsonMap.get("transactionType")).byteValue();
                    }

                    switch (txType) {
                        case 4: transaction = VEETransaction.makeTransferTx(jsonMap, accounts);
                                break;
                        case 8: transaction = VEETransaction.makeLeaseTx(jsonMap, accounts);
                    }

                    if (transaction != null) {
                        exportQRCode = QRCodeUtil.generateQRCode(transaction.getJson(), 800);
                        qrCode.setImageBitmap(exportQRCode);
                        Log.d(TAG, transaction.getFullJson());
                    }
                    break;

                case 2:
                    String priKey = QRCodeUtil.parsePriKey(qrContents);
                    VEEAccount account = new VEEAccount(false, priKey);
                    Toast.makeText(this, "Private Key: " + account.getPriKey() +
                            "\n\nPublic Key: " + account.getPubKey() +
                            "\n\nAddress: " + account.getAddress(), Toast.LENGTH_LONG).show();
                    exportQRCode = QRCodeUtil.exportPubKeyAddr(account, 800);
                    qrCode.setImageBitmap(exportQRCode);
                    break;

                case 3:
                    Toast.makeText(this, "Incorrect QR code format", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

