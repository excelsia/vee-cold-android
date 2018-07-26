package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class DialogUtil {

    public static void createExportSeedDialog(Activity activity, VEEWallet wallet) {
        if (wallet != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.custom_dialog_export_seed);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView qrCode = (ImageView) dialog.findViewById(R.id.export_seed);
            TextView seed = (TextView) dialog.findViewById(R.id.export_seed_string);
            qrCode.setImageBitmap(QRCodeUtil.exportSeed(wallet, 800));
            seed.setText(wallet.getSeed());

            dialog.setTitle("Export Seed");
            dialog.show();
        }
        else {
                Toast.makeText(activity, "No seed found", Toast.LENGTH_LONG).show();
            }
    }

    public static void createExportAddressDialog(Activity activity, VEEAccount account) {
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
}
