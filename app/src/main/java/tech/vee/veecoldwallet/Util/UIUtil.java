package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.SetPasswordActivity;
import tech.vee.veecoldwallet.Fragment.WalletFragment;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEETransaction;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class UIUtil {
    public static void createExportSeedDialog(final Activity activity, VEEWallet wallet) {
        if (wallet != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.custom_dialog_export_seed);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final TextView copy = (TextView) dialog.findViewById(R.id.export_seed_copy);
            ImageView qrCode = (ImageView) dialog.findViewById(R.id.export_seed);
            TextView seed = (TextView) dialog.findViewById(R.id.export_seed_string);
            final String seedString = wallet.getSeed();

            if (seedString.equals("")){
                copy.setTextColor(activity.getResources().getColor(R.color.textLight));
            }

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager)
                            activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("SEED", seedString);
                    clipboard.setPrimaryClip(clip);
                    copy.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                }
            });

            qrCode.setImageBitmap(QRCodeUtil.exportSeed(wallet, 800));
            seed.setText(seedString);

            dialog.setTitle("Export Seed");
            dialog.show();
        }
        else {
            Toast.makeText(activity, "No seed found", Toast.LENGTH_LONG).show();
        }
    }

    public static void createForeignSeedDialog(final Activity activity, final String seed) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_foreign_seed);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView seedText = (TextView) dialog.findViewById(R.id.foreign_seed);
        Button negative = (Button) dialog.findViewById(R.id.foreign_seed_negative);
        Button positive = (Button) dialog.findViewById(R.id.foreign_seed_positive);

        seedText.setText(seed);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SetPasswordActivity.class);
                intent.putExtra("SEED", seed);
                activity.startActivity(intent);
            }
        });

        dialog.setTitle("Foreign Seed");
        dialog.show();
    }


    public static void createExportAddressDialog(final Activity activity, VEEAccount account) {
        if (account != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.custom_dialog_export_address);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView qrCode = (ImageView) dialog.findViewById(R.id.export_address);
            TextView address = (TextView) dialog.findViewById(R.id.export_address_string);
            TextView title = (TextView) dialog.findViewById(R.id.export_account_title);
            Button sign = (Button) dialog.findViewById(R.id.sign_tx);

            qrCode.setImageBitmap(QRCodeUtil.exportPubKeyAddr(account,800));
            address.setText(account.getAddress());
            title.setText("Account " + String.valueOf(account.getNonce() + 1));

            sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QRCodeUtil.scan(activity);
                }
            });

            dialog.setTitle("Export Address");
            dialog.show();
        }
        else {
            Toast.makeText(activity, "No account found", Toast.LENGTH_LONG).show();
        }
    }

    public static void createPaymentTxDialog(final Activity activity, final VEEAccount sender,
                                              final String recipient, final long amount,
                                              final long fee, long timestamp) {
        activity.setContentView(R.layout.custom_layout_payment_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        final TextView recipientTx = (TextView) activity.findViewById(R.id.transaction_recipient);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView amountTx = (TextView) activity.findViewById(R.id.transaction_amount);
        TextView feeTx = (TextView) activity.findViewById(R.id.transaction_fee);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        float amountFloat = (float) amount/100000000;
        float feeFloat = (float) fee/100000000;

        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(amountFloat));
        feeTx.setText(String.valueOf(feeFloat));

        String time = new SimpleDateFormat("yyyy-MM-dd  HH:MM:SS")
                .format(new Timestamp(timestamp));
        timestampTx.setText(time + "\n" + TimeZone.getDefault().getDisplayName());

        final BigInteger timeBigInteger = BigInteger.valueOf(timestamp)
                .multiply(BigInteger.valueOf(1000000L));

        senderTx.setTag("MUTATED");
        senderTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderTx.getTag().equals("MUTATED")) {
                    senderTx.setText(sender.getAddress());
                    senderTx.setTag("COMPLETE");
                }
                else {
                    senderTx.setText(sender.getMutatedAddress());
                    senderTx.setTag("MUTATED");
                }
            }
        });

        recipientTx.setTag("MUTATED");
        recipientTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipientTx.getTag().equals("MUTATED")) {
                    recipientTx.setText(recipient);
                    recipientTx.setTag("COMPLETE");
                }
                else {
                    recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
                    recipientTx.setTag("MUTATED");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEETransaction transaction = VEETransaction.makePaymentTx(sender, recipient,
                        amount, fee, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void createTransferTxDialog(final Activity activity, final VEEAccount sender,
                                              final String recipient, final long amount,
                                              final String assetId, final long fee,
                                              final String feeAssetId, final String attachment,
                                              long timestamp) {
        activity.setContentView(R.layout.custom_layout_transfer_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        final TextView recipientTx = (TextView) activity.findViewById(R.id.transaction_recipient);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView amountTx = (TextView) activity.findViewById(R.id.transaction_amount);
        TextView feeTx = (TextView) activity.findViewById(R.id.transaction_fee);
        TextView attachmentTx = (TextView) activity.findViewById(R.id.transaction_attachment);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        float amountFloat = (float) amount/100000000;
        float feeFloat = (float) fee/100000000;

        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(amountFloat));
        feeTx.setText(String.valueOf(feeFloat));

        if (!attachment.equals("")) { attachmentTx.setText(attachment); }
        else { attachmentTx.setText("None"); }


        String time = new SimpleDateFormat("yyyy-MM-dd  HH:MM:SS")
                .format(new Timestamp(timestamp));
        timestampTx.setText(time + "\n" + TimeZone.getDefault().getDisplayName());

        final BigInteger timeBigInteger = BigInteger.valueOf(timestamp)
                .multiply(BigInteger.valueOf(1000000L));

        senderTx.setTag("MUTATED");
        senderTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderTx.getTag().equals("MUTATED")) {
                    senderTx.setText(sender.getAddress());
                    senderTx.setTag("COMPLETE");
                }
                else {
                    senderTx.setText(sender.getMutatedAddress());
                    senderTx.setTag("MUTATED");
                }
            }
        });

        recipientTx.setTag("MUTATED");
        recipientTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipientTx.getTag().equals("MUTATED")) {
                    recipientTx.setText(recipient);
                    recipientTx.setTag("COMPLETE");
                }
                else {
                    recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
                    recipientTx.setTag("MUTATED");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEETransaction transaction = VEETransaction.makeTransferTx(sender, recipient,
                        amount, assetId, fee, feeAssetId, attachment, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void createLeaseTxDialog(final Activity activity, final VEEAccount sender,
                                              final String recipient, final long amount,
                                              final long fee, long timestamp) {
        activity.setContentView(R.layout.custom_layout_lease_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        final TextView recipientTx = (TextView) activity.findViewById(R.id.transaction_recipient);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView amountTx = (TextView) activity.findViewById(R.id.transaction_amount);
        TextView feeTx = (TextView) activity.findViewById(R.id.transaction_fee);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        float amountFloat = (float) amount/100000000;
        float feeFloat = (float) fee/100000000;

        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(amountFloat));
        feeTx.setText(String.valueOf(feeFloat));

        String time = new SimpleDateFormat("yyyy-MM-dd  HH:MM:SS")
                .format(new Timestamp(timestamp));
        timestampTx.setText(time + "\n" + TimeZone.getDefault().getDisplayName());

        final BigInteger timeBigInteger = BigInteger.valueOf(timestamp)
                .multiply(BigInteger.valueOf(1000000L));

        senderTx.setTag("MUTATED");
        senderTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderTx.getTag().equals("MUTATED")) {
                    senderTx.setText(sender.getAddress());
                    senderTx.setTag("COMPLETE");
                }
                else {
                    senderTx.setText(sender.getMutatedAddress());
                    senderTx.setTag("MUTATED");
                }
            }
        });

        recipientTx.setTag("MUTATED");
        recipientTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipientTx.getTag().equals("MUTATED")) {
                    recipientTx.setText(recipient);
                    recipientTx.setTag("COMPLETE");
                }
                else {
                    recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
                    recipientTx.setTag("MUTATED");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEETransaction transaction = VEETransaction.makeLeaseTx(sender, recipient,
                        amount, fee, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void createCancelLeaseTxDialog(final Activity activity, final VEEAccount sender,
                                           final String txId, final long fee, long timestamp) {
        activity.setContentView(R.layout.custom_layout_cancel_lease_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView feeTx = (TextView)activity.findViewById(R.id.transaction_fee);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        float feeFloat = (float) fee/100000000;
        senderTx.setText(sender.getMutatedAddress());
        feeTx.setText(String.valueOf(feeFloat));

        String time = new SimpleDateFormat("yyyy-MM-dd  HH:MM:SS")
                .format(new Timestamp(timestamp));
        timestampTx.setText(time + "\n" + TimeZone.getDefault().getDisplayName());

        final BigInteger timeBigInteger = BigInteger.valueOf(timestamp)
                .multiply(BigInteger.valueOf(1000000L));

        senderTx.setTag("MUTATED");
        senderTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderTx.getTag().equals("MUTATED")) {
                    senderTx.setText(sender.getAddress());
                    senderTx.setTag("COMPLETE");
                }
                else {
                    senderTx.setText(sender.getMutatedAddress());
                    senderTx.setTag("MUTATED");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEETransaction transaction = VEETransaction.makeLeaseCancelTx(sender, txId, fee, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void createSignatureDialog(final Activity activity, VEETransaction transaction) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_signature);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView qrCode = (ImageView) dialog.findViewById(R.id.signature);
        Button done = (Button) dialog.findViewById(R.id.transaction_done);

        qrCode.setImageBitmap(QRCodeUtil.generateQRCode(transaction.getJson(), 800));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ColdWalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });

        dialog.setTitle("Export Signature");
        dialog.show();
    }

    public static void createAccountNumberDialog(final Activity activity, final String seed) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_account_number);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final MaterialNumberPicker np = (MaterialNumberPicker) dialog.findViewById(R.id.account_number_np);
        Button dialogButton = (Button) dialog.findViewById(R.id.account_number_confirm);
        TextView title = (TextView) dialog.findViewById(R.id.account_number_title);
        TextView subtitle = (TextView) dialog.findViewById(R.id.account_number_subtitle);

        title.setText(R.string.account_number_title);
        subtitle.setText(R.string.account_number_subtitle);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("SELECT_ACCOUNT_NUMBER");
                intent.putExtra("ACCOUNT_NUMBER", np.getValue());
                intent.putExtra("SEED", seed);
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        dialog.setTitle("Create Accounts");
        dialog.show();
    }

    public static void createAppendAccountsDialog(final Activity activity, final int max) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_account_number);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final MaterialNumberPicker np = (MaterialNumberPicker) dialog.findViewById(R.id.account_number_np);
        Button dialogButton = (Button) dialog.findViewById(R.id.account_number_confirm);
        TextView title = (TextView) dialog.findViewById(R.id.account_number_title);
        TextView subtitle = (TextView) dialog.findViewById(R.id.account_number_subtitle);

        title.setText(R.string.append_account_title);
        subtitle.setText(R.string.append_account_subtitle);

        np.setMaxValue(max);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("SELECT_APPEND_ACCOUNT_NUMBER");
                intent.putExtra("ACCOUNT_NUMBER", np.getValue());
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        dialog.setTitle("Append Accounts");
        dialog.show();
    }

    // Set adapter for account cards
    public static void setAccountCardsAdapter(Activity activity, RecyclerView accountCards,
                                              WalletFragment.AccountAdapter adapter,
                                              ArrayList<VEEAccount> accounts){
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (accounts.size() > 0 & accountCards != null) {
            accountCards.setAdapter(adapter);
        }
        accountCards.setLayoutManager(layoutManager);
    }
}
