package tech.vee.veecoldwallet.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.ScannerActivity;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.DialogUtil;
import tech.vee.veecoldwallet.Util.QRCodeUtil;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class WalletFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Winston";

    private FloatingActionButton importSeed;
    private FloatingActionButton generateSeed;
    private ImageView qrCodeView;
    private BoomMenuButton bmb;

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
        //qrCodeView = (ImageView)view.findViewById(R.id.qr_code);
        //importQrCode = (Button)view.findViewById(R.id.importQRCode);
        //signTx = (Button)view.findViewById(R.id.signTx);
        importSeed = view.findViewById(R.id.importSeed);
        generateSeed = view.findViewById(R.id.generateSeed);
        //bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
        //bmb.setButtonEnum(ButtonEnum.Ham);
        //bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        //bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);
        //Log.d(TAG, "Pieces " + bmb.getPiecePlaceEnum().pieceNumber());

        importSeed.setOnClickListener(this);
        generateSeed.setOnClickListener(this);

        ColdWalletActivity activity = (ColdWalletActivity) getActivity();
        wallet = activity.getWallet();
        if (wallet != null){
            accounts = wallet.generateAccounts();
            account = accounts.get(0);
            //Toast.makeText(this, account.toString(), Toast.LENGTH_LONG).show();
            Bitmap exportQRCode = QRCodeUtil.exportPubKeyAddr(account, 800);
            //qrCodeView.setImageBitmap(exportQRCode);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importSeed:
                QRCodeUtil.scan(getActivity());
                break;

            case R.id.generateSeed:
                wallet = VEEWallet.generate();
                DialogUtil.createExportSeedDialog(getActivity(), wallet);
        }
    }

    public ImageView getQrCodeView(){
        return qrCodeView;
    }

    /*
    private void configureBmB(){
        if(bmb != null) {
            bmb.setButtonEnum(ButtonEnum.Ham);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);

            bmb.addBuilder(getHamButtonBuilder());

            ListView listView = (ListView) findViewById(R.id.list_view);
            assert listView != null;
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,
                    BuilderManager.getHamButtonData(piecesAndButtons)));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bmb.setPiecePlaceEnum((PiecePlaceEnum) piecesAndButtons.get(position).first);
                    bmb.setButtonPlaceEnum((ButtonPlaceEnum) piecesAndButtons.get(position).second);
                    bmb.clearBuilders();
                    for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
                        bmb.addBuilder(BuilderManager.getHamButtonBuilder());
                }
            });
        }
        else {
            Log.d(TAG, "Boom menu button cannot be found");
        }
    } */
}
