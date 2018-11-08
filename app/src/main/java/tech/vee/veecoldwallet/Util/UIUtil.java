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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.ConfirmTxActivity;
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

            qrCode.setImageBitmap(QRCodeUtil.exportPubKeyAddr(account,800));
            address.setText(account.getAddress());
            title.setText("Account " + String.valueOf(account.getNonce() + 1));

            dialog.setTitle("Export Address");
            dialog.show();
        }
        else {
            Toast.makeText(activity, "No account found", Toast.LENGTH_LONG).show();
        }
    }

    public static void createAboutUsDialog(final Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.activity_about_us);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.setTitle("About");
            dialog.show();
    }

    public static void setPaymentTx(final Activity activity, final VEEAccount sender,
                                              final String recipient, final long amount,
                                              final long fee, final short feeScale, final String attachment, long timestamp) {
        activity.setContentView(R.layout.custom_layout_payment_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        final TextView recipientTx = (TextView) activity.findViewById(R.id.transaction_recipient);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView amountTx = (TextView) activity.findViewById(R.id.transaction_amount);
        TextView feeTx = (TextView) activity.findViewById(R.id.transaction_fee);
        TextView attachmentTx = (TextView) activity.findViewById(R.id.transaction_attachment);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        Log.d("Winston", amount + " " + fee);
        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(convert(amount)));
        feeTx.setText(String.valueOf(convert(fee)));

        String time = new SimpleDateFormat("yyyy-MM-dd  HH:MM:SS")
                .format(new Timestamp(timestamp));
        timestampTx.setText(time + "\n" + TimeZone.getDefault().getDisplayName());

        if (!attachment.equals("")) { attachmentTx.setText(attachment); }
        else { attachmentTx.setText("None"); }

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
                        amount, fee, feeScale, attachment, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void setTransferTx(final Activity activity, final VEEAccount sender,
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

        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(convert(amount)));
        feeTx.setText(String.valueOf(convert(fee)));

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

    public static void setLeaseTx(final Activity activity, final VEEAccount sender,
                                              final String recipient, final long amount,
                                              final long fee, final short feeScale, long timestamp) {
        activity.setContentView(R.layout.custom_layout_lease_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        final TextView recipientTx = (TextView) activity.findViewById(R.id.transaction_recipient);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView amountTx = (TextView) activity.findViewById(R.id.transaction_amount);
        TextView feeTx = (TextView) activity.findViewById(R.id.transaction_fee);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        senderTx.setText(sender.getMutatedAddress());
        recipientTx.setText(VEEAccount.getMutatedAddress(recipient));
        amountTx.setText(String.valueOf(convert(amount)));
        feeTx.setText(String.valueOf(convert(fee)));

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
                        amount, fee, feeScale, timeBigInteger);
                createSignatureDialog(activity, transaction);
            }
        });
    }

    public static void setCancelLeaseTx(final Activity activity, final VEEAccount sender,
                                           final String txId, final long fee, final short feeScale, long timestamp) {
        activity.setContentView(R.layout.custom_layout_cancel_lease_tx);

        final TextView senderTx = (TextView) activity.findViewById(R.id.transaction_sender);
        TextView timestampTx = (TextView) activity.findViewById(R.id.transaction_timestamp);
        TextView feeTx = (TextView)activity.findViewById(R.id.transaction_fee);
        Button confirm = (Button) activity.findViewById(R.id.transaction_confirm);

        senderTx.setText(sender.getMutatedAddress());
        feeTx.setText(String.valueOf(convert(fee)));

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
                VEETransaction transaction = VEETransaction.makeLeaseCancelTx(sender, txId, fee, feeScale, timeBigInteger);
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

        qrCode.setImageBitmap(QRCodeUtil.generateQRCode(transaction.getJson(), 500));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ColdWalletActivity.class);
                intent.putExtra("WALLET", ((ConfirmTxActivity) activity).getWalletStr());
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

    public static void createFirstRunWarningDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_first_run_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialog.findViewById(R.id.first_run_warning_continue);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("First Run Warning");
        dialog.show();
    }

    public static void createUpdateAppDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_update_app);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialog.findViewById(R.id.update_app_ok);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("Update App");
        dialog.show();
    }

    public static void createWrongTransactionDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_wrong_transaction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialog.findViewById(R.id.update_app_ok);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("Wrong Transaction");
        dialog.show();
    }

    public static void createPasswordWarningDialog(final Activity activity, final String seed) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_password_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button positiveButton = (Button) dialog.findViewById(R.id.password_warning_positive);
        Button negativeButton = (Button) dialog.findViewById(R.id.password_warning_negative);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.createAccountNumberDialog(activity, seed);
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("Password Warning");
        dialog.show();
    }

    public static void createRequestPasswordDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_request_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialog.findViewById(R.id.request_password_confirm);
        final EditText input = (EditText) dialog.findViewById(R.id.request_password_input);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                Intent intent = new Intent("CONFIRM_PASSWORD");
                intent.putExtra("PASSWORD", input.getText().toString());
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        dialog.setTitle("Request Password");
        dialog.show();
    }

    public static void createMonitorConnectivityDialog(final Activity activity, boolean wifi,
                                                       boolean data, boolean bluetooth) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_monitor_connectivity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        final Button dialogButton = (Button) dialog.findViewById(R.id.monitor_connectivity_continue);
        final ImageView icon = (ImageView) dialog.findViewById(R.id.monitor_connectivity_icon);
        final ImageView circle1 = (ImageView) dialog.findViewById(R.id.monitor_connectivity_circle_1);
        final ImageView circle2 = (ImageView) dialog.findViewById(R.id.monitor_connectivity_circle_2);
        final ImageView circle3 = (ImageView) dialog.findViewById(R.id.monitor_connectivity_circle_3);
        final TextView text1 = (TextView) dialog.findViewById(R.id.monitor_connectivity_text_1);
        final TextView text2 = (TextView) dialog.findViewById(R.id.monitor_connectivity_text_2);
        final TextView text3 = (TextView) dialog.findViewById(R.id.monitor_connectivity_text_3);

        if (!wifi) {
           circle1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
           text1.setText(activity.getResources().getText(R.string.monitor_connectivity_checked_1));
        }

        if (!data) {
            circle2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
            text2.setText(activity.getResources().getText(R.string.monitor_connectivity_checked_2));
        }

        if (!bluetooth) {
            circle3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
            text3.setText(activity.getResources().getText(R.string.monitor_connectivity_checked_3));
        }

        if (!wifi && !data && !bluetooth) {
            dialogButton.setText(R.string.monitor_connectivity_continue);
            icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_check));
        }

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogButton.getText().equals(activity.getResources()
                        .getString(R.string.monitor_connectivity_continue))) {
                    UIUtil.createRequestPasswordDialog(activity);
                    dialog.dismiss();
                }
                else {
                    UIUtil.refreshNetworkConnection(activity, dialogButton, icon, circle1, circle2,
                            circle3, text1, text2, text3);
                }
            }
        });

        Toast.makeText(activity, "Please disconnect all networks", Toast.LENGTH_LONG).show();
        dialog.setTitle("Monitor Connectivity");
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

    public static void refreshNetworkConnection(Activity activity, Button dialogButton, ImageView icon,
                                          ImageView wifiState, ImageView dataState, ImageView bluetoothState,
                                          TextView wifiText, TextView dataText, TextView bluetoothText) {
        Boolean wifi = false, data = false, bluetooth;
        NetworkUtil.NetworkType type;

        bluetooth = NetworkUtil.bluetoothIsConnected();

        switch (NetworkUtil.isConnectedType(activity)) {
            case NoConnect:
                break;

            case Wifi:
                wifi = true;
                break;

            case Mobile:
                data = true;
        }

        if(wifi){
            wifiText.setText(R.string.monitor_connectivity_unchecked_1);
            wifiState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_gray));
        }
        else {
            wifiText.setText(R.string.monitor_connectivity_checked_1);
            wifiState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
        }

        if(data){
            dataText.setText(R.string.monitor_connectivity_unchecked_2);
            dataState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_gray));
        }
        else {
            dataText.setText(R.string.monitor_connectivity_checked_2);
            dataState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
        }

        if(bluetooth){
            bluetoothText.setText(R.string.monitor_connectivity_unchecked_3);
            bluetoothState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_gray));
        }
        else {
            bluetoothText.setText(R.string.monitor_connectivity_checked_3);
            bluetoothState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_circle_green));
        }

        if(!wifi && !data && !bluetooth) {
            icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_check));
            dialogButton.setEnabled(true);
            dialogButton.setText(R.string.monitor_connectivity_continue);
        }
    }

    private static String convert(long num) {
        String res = String.valueOf(num);
        String whole, decimal;

        if (res.equals("0")) {
            return "0";
        }
        else if (res.length() <= 8) {
            decimal = res.replaceAll("0+$", "");
            for (int i=0; i<(8 - res.length()); i++) {
                decimal = "0" + decimal;
            }
            return "0." + decimal;
        }
        else {
            decimal = res.substring(res.length() - 8, res.length()).replaceAll("0+$", "");
            whole = res.substring(0, res.length() - 8);
            if (decimal.equals("")) {
                return whole;
            }
            return whole + "." + decimal;
        }
    }
}
