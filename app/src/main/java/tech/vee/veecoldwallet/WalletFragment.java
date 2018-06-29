package tech.vee.veecoldwallet;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class WalletFragment extends Fragment {
    String addr;
    String priKey;
    String pubKey;
    String domain;
    String qrContents;
    ImageView qrCode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        addr = "3N3LRioDiFkPrQvbuRP3tBUmYDq5Ro4g8ho";
        pubKey = "Foh3cBN2Mmgy2oCC27KJ32LiJVfNfuYdBUqev14toa9B";
        priKey = "EXSu2hma58fD662tcTY8Jy4xnrPjEMy9xk5Sd6uwiuws";
        domain = "https://vee.tech";
        qrCode = (ImageView)view.findViewById(R.id.qr_code);
        qrContents = Tools.generatePubKeyAddrMsg(domain, addr, pubKey);
        Toast.makeText(getActivity(),qrContents, Toast.LENGTH_LONG).show();
        qrCode.setImageBitmap(Tools.generateQRCode(qrContents, 400));
        return view;
    }


}
