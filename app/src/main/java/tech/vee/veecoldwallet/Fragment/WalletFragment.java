package tech.vee.veecoldwallet.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;

import tech.vee.veecoldwallet.Activity.ScannerActivity;
import tech.vee.veecoldwallet.R;

public class WalletFragment extends Fragment implements View.OnClickListener {
    private Button importQrCode;
    private Button signTx;
    private ImageView qrCodeView;

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
        //addr = "3N3LRioDiFkPrQvbuRP3tBUmYDq5Ro4g8ho";
        //pubKey = "Foh3cBN2Mmgy2oCC27KJ32LiJVfNfuYdBUqev14toa9B";
        //priKey = "EXSu2hma58fD662tcTY8Jy4xnrPjEMy9xk5Sd6uwiuws";
        //domain = "https://vee.tech";
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
