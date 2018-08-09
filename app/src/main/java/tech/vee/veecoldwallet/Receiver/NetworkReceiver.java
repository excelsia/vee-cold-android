package tech.vee.veecoldwallet.Receiver;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.NetworkUtil;
import tech.vee.veecoldwallet.Util.UIUtil;

public class NetworkReceiver extends BroadcastReceiver {
    private ImageView icon;
    private ImageView wifiState;
    private ImageView dataState;
    private ImageView bluetoothState;
    private TextView wifiText;
    private TextView dataText;
    private TextView bluetoothText;

    private Button dialogButton;
    private Dialog dialog;
    private Activity activity;

    public NetworkReceiver() {}

    public NetworkReceiver(Activity activity, Dialog dialog, Button dialogButton, ImageView icon,
                           ImageView wifiState, ImageView dataState, ImageView bluetoothState,
                           TextView wifiText, TextView dataText, TextView bluetoothText) {
        this.icon = icon;
        this.wifiState = wifiState;
        this.dataState = dataState;
        this.bluetoothState = bluetoothState;
        this.wifiText = wifiText;
        this.dataText = dataText;
        this.bluetoothText = bluetoothText;
        this.dialogButton = dialogButton;
        this.dialog = dialog;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
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

        if (notNull()) {
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
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.createRequestPasswordDialog(activity);
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    private boolean notNull() {
        return wifiText != null && wifiState != null && dataText != null && dataState != null &&
                bluetoothText != null && bluetoothState != null;
    }
}
