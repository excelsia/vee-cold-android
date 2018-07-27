package tech.vee.veecoldwallet.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.wavesplatform.wavesj.Base58;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tech.vee.veecoldwallet.Util.DialogUtil;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEETransaction;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Fragment.SettingsFragment;
import tech.vee.veecoldwallet.Fragment.WalletFragment;
import tech.vee.veecoldwallet.Util.JsonUtil;
import tech.vee.veecoldwallet.Util.QRCodeUtil;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class ColdWalletActivity extends AppCompatActivity {
    private static final String TAG = "Winston";
    private static final String WALLET_FILE_NAME = "wallet.dat";

    private ActionBar actionBar;

    private WalletFragment walletFrag;
    private SettingsFragment settingsFrag;
    private FragmentManager fragmentManager;

    private String qrContents;
    private ImageView qrCode;
    private Bitmap exportQRCode;

    private VEEWallet wallet;
    private File walletFile;
    private String walletFilePath;
    private ArrayList<VEEAccount> accounts;
    private VEEAccount account;
    private String password;

    public VEEWallet getWallet() { return wallet; }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_wallet:
                    actionBar.setLogo(R.drawable.ic_navigation_wallet);
                    actionBar.setTitle(R.string.title_wallet);
                    switchToFragment(walletFrag);
                    return true;

                case R.id.navigation_settings:
                    actionBar.setLogo(R.drawable.ic_navigation_settings);
                    actionBar.setTitle(R.string.title_settings);
                    switchToFragment(settingsFrag);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.scan:
                QRCodeUtil.scan(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_navigation_wallet);
        actionBar.setTitle(R.string.title_wallet);

        walletFrag = new WalletFragment();
        settingsFrag = new SettingsFragment();
        fragmentManager = null;
        switchToFragment(walletFrag);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        walletFilePath = getFilesDir().getPath().toString() + "/" + WALLET_FILE_NAME;
        //Log.d(TAG, "Wallet file path: " + walletFilePath);
        walletFile = new File(walletFilePath);
        password = "";
        accounts = new ArrayList<>();

        if (walletFile.exists()){
            String seed = JsonUtil.load(walletFilePath);
            if (seed != "" && seed != JsonUtil.ERROR) {
                wallet = new VEEWallet(seed);
                accounts = wallet.generateAccounts();
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        qrContents = result.getContents();
        qrCode = walletFrag.getQrCodeView();

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
                                break;
                        case 9: transaction = VEETransaction.makeLeaseCancelTx(jsonMap, accounts);
                    }

                    if (transaction != null) {
                        exportQRCode = QRCodeUtil.generateQRCode(transaction.getJson(), 800);
                        qrCode.setImageBitmap(exportQRCode);
                        Log.d(TAG, transaction.getFullJson());
                    }
                    break;

                case 2:
                    String seed = QRCodeUtil.parseSeed(qrContents);

                    if(VEEAccount.validateSeedPhrase(seed)) {
                        wallet = VEEWallet.recover(seed, 2);
                        JsonUtil.save(wallet.getJson(), walletFilePath);
                        Log.d(TAG, wallet.getJson());

                        if (wallet != null){
                            accounts = wallet.generateAccounts();
                            account = accounts.get(0);
                            DialogUtil.createExportAddressDialog(this, account);
                        }
                    }
                    else {
                        Log.d(TAG,"Invalid account seed!");
                    }
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

