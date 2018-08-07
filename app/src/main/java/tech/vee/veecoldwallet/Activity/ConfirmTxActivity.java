package tech.vee.veecoldwallet.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.UIUtil;
import tech.vee.veecoldwallet.Wallet.VEEAccount;

public class ConfirmTxActivity extends AppCompatActivity {
    private static final String TAG = "Winston";
    private ActionBar actionBar;
    private ConfirmTxActivity activity;

    private VEEAccount sender;
    private String recipient,assetId, feeAssetId, txId, attachment;
    private long timestamp, amount, fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_tx);

        activity = this;

        Intent intent = getIntent();
        String action = intent.getStringExtra("ACTION");

        Gson gson = new Gson();
        String senderStr;

        switch (action) {
            case "PAYMENT":
                senderStr = getIntent().getStringExtra("SENDER");

                sender = gson.fromJson(senderStr, VEEAccount.class);
                recipient = intent.getStringExtra("RECIPIENT");
                amount= intent.getLongExtra("AMOUNT", 0);
                fee = intent.getLongExtra("FEE", 0);
                timestamp =intent.getLongExtra("TIMESTAMP", 0);

                UIUtil.createPaymentTxDialog(activity, sender, recipient, amount,
                       fee, timestamp);
                break;

            case "TRANSFER":
                senderStr = getIntent().getStringExtra("SENDER");

                sender = gson.fromJson(senderStr, VEEAccount.class);
                recipient = intent.getStringExtra("RECIPIENT");
                assetId = intent.getStringExtra("ASSET_ID");
                feeAssetId = intent.getStringExtra("FEE_ASSET_ID");
                attachment = intent.getStringExtra("ATTACHMENT");
                amount= intent.getLongExtra("AMOUNT", 0);
                fee = intent.getLongExtra("FEE", 0);
                timestamp =intent.getLongExtra("TIMESTAMP", 0);

                UIUtil.createTransferTxDialog(activity, sender, recipient, amount,
                        assetId, fee, feeAssetId, attachment, timestamp);
                break;

            case "LEASE":
                senderStr = getIntent().getStringExtra("SENDER");

                sender = gson.fromJson(senderStr, VEEAccount.class);
                recipient = intent.getStringExtra("RECIPIENT");
                amount= intent.getLongExtra("AMOUNT", 0);
                fee = intent.getLongExtra("FEE", 0);
                timestamp =intent.getLongExtra("TIMESTAMP", 0);

                UIUtil.createLeaseTxDialog(activity, sender, recipient, amount, fee, timestamp);
                break;

            case "CANCEL_LEASE":
                senderStr = getIntent().getStringExtra("SENDER");

                sender = gson.fromJson(senderStr, VEEAccount.class);
                txId = intent.getStringExtra("TX_ID");
                fee = intent.getLongExtra("FEE", 0);
                timestamp =intent.getLongExtra("TIMESTAMP", 0);

                UIUtil.createCancelLeaseTxDialog(activity, sender, txId, fee, timestamp);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_qr_code);
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        actionBar.setTitle(R.string.title_confirm_tx);
    }
}
