package tech.vee.veecoldwallet.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.ScannerActivity;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.QRCodeUtil;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class WalletFragment extends Fragment implements View.OnClickListener {
    private Button importQrCode;
    private Button signTx;
    private ImageView qrCodeView;

    private VEEWallet wallet;
    private ArrayList<VEEAccount> accounts;
    private VEEAccount account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        qrCodeView = (ImageView)view.findViewById(R.id.qr_code);
        importQrCode = (Button)view.findViewById(R.id.importQRCode);
        signTx = (Button)view.findViewById(R.id.signTx);
        importQrCode.setOnClickListener(this);
        signTx.setOnClickListener(this);

        ColdWalletActivity activity = (ColdWalletActivity) getActivity();
        wallet = activity.getWallet();
        if (wallet != null){
            accounts = wallet.generateAccounts();
            account = accounts.get(0);
            //Toast.makeText(this, account.toString(), Toast.LENGTH_LONG).show();
            Bitmap exportQRCode = QRCodeUtil.exportPubKeyAddr(account, 800);
            qrCodeView.setImageBitmap(exportQRCode);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getActivity(),"Button Clicked!",Toast.LENGTH_LONG).show();
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(ScannerActivity.class);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    public ImageView getQrCodeView(){
        return qrCodeView;
    }

}
