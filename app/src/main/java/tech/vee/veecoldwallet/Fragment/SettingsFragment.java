package tech.vee.veecoldwallet.Fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.QRCodeUtil;
import tech.vee.veecoldwallet.Wallet.VEEWallet;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    ListPreference listPreference;
    PreferenceScreen preferenceScreen;
    VEEWallet wallet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_settings);
        listPreference = (ListPreference) findPreference("settings_language");
        // Set default language to English
        listPreference.setValueIndex(0);
        preferenceScreen = (PreferenceScreen) findPreference("settings_clone");
        preferenceScreen.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference){
        // custom dialog
        ColdWalletActivity activity = (ColdWalletActivity) getActivity();
        wallet = activity.getWallet();

        if(wallet != null){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.custom_dialog_clone);
            ImageView qrCode = (ImageView) dialog.findViewById(R.id.export_seed);
            TextView seed = (TextView) dialog.findViewById(R.id.export_seed_string);
            qrCode.setImageBitmap(QRCodeUtil.exportSeed(wallet,800));
            seed.setText(wallet.getSeed());
            dialog.setTitle("Clone Wallet");
            dialog.show();
        }


        /*
        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        */
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // remove dividers
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        list.setDivider(null);
    }
}

