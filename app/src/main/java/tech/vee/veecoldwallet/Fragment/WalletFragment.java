package tech.vee.veecoldwallet.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.QRCodeUtil;

public class WalletFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
        //addr = "3N3LRioDiFkPrQvbuRP3tBUmYDq5Ro4g8ho";
        //pubKey = "Foh3cBN2Mmgy2oCC27KJ32LiJVfNfuYdBUqev14toa9B";
        //priKey = "EXSu2hma58fD662tcTY8Jy4xnrPjEMy9xk5Sd6uwiuws";
        //domain = "https://vee.tech";
    }
}
