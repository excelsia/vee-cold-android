package tech.vee.veecoldwallet.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.ImportSeedActivity;
import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.UIUtil;
import tech.vee.veecoldwallet.Util.QRCodeUtil;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class WalletFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Winston";

    private Activity activity;

    private RecyclerView accountCards;
    private LinearLayout linearLayout;
    private AccountAdapter adapter;

    private FloatingActionMenu menu;
    private FloatingActionButton importSeed;
    private FloatingActionButton generateSeed;

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

        activity = getActivity();

        accountCards = view.findViewById(R.id.cards_account);
        linearLayout = view.findViewById(R.id.start);

        menu = view.findViewById(R.id.menu);
        importSeed = view.findViewById(R.id.importSeed);
        generateSeed = view.findViewById(R.id.generateSeed);

        menu.setTag("OFF");
        menu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("OFF".equals(menu.getTag())) {
                    displayAccounts(false);
                    accountCards.setLayoutFrozen(true);
                    menu.setTag("ON");
                    menu.open(true);
                }
                else {
                    displayAccounts(true);
                    accountCards.setLayoutFrozen(false);
                    menu.setTag("OFF");
                    menu.close(true);
                }
            }
        });

        importSeed.setOnClickListener(this);
        generateSeed.setOnClickListener(this);

        ColdWalletActivity activity = (ColdWalletActivity) getActivity();
        VEEWallet activityWallet = activity.getWallet();
        if (activityWallet != null) { setWallet(activity.getWallet()); }

        // Display wallet if exists, otherwise display start page
        refreshAccounts(accounts);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importSeed:
                menu.setTag("OFF");
                menu.close(true);
                Intent intent = new Intent(activity, ImportSeedActivity.class);
                startActivity(intent);
                break;

            case R.id.generateSeed:
                wallet = VEEWallet.generate();
                UIUtil.createExportSeedDialog(getActivity(), wallet);
        }
    }

    public void setWallet(VEEWallet wallet) {
        this.wallet = wallet;
        this.accounts = wallet.generateAccounts();
    }

    public void displayAccounts(Boolean flag){
        if (!flag) { adapter.setFlag(false); }
        else { adapter.setFlag(true); }
        adapter.notifyDataSetChanged();
    }

    public void refreshAccounts(ArrayList<VEEAccount> accounts){
        adapter = new AccountAdapter(accounts);

        if (wallet != null && accounts != null){
            linearLayout.setVisibility(View.GONE);
            UIUtil.setAccountCardsAdapter(activity, accountCards, adapter, accounts);
            displayAccounts(true);
        }
        else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Used to hold the contents of an account
     */
    public class AccountHolder extends RecyclerView.ViewHolder {
        public TextView accountName;
        public TextView mutatedAddress;
        public ImageView qrCode;

        public AccountHolder(View v) {
            super(v);
            accountName = (TextView) v.findViewById(R.id.account_name);
            mutatedAddress = (TextView) v.findViewById(R.id.account_mutated_address);
            qrCode = (ImageView) v.findViewById(R.id.account_qr_code);
            qrCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    /**
     * Used to display all existing accounts in a scrollable recycler view
     */
    public class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {
        private ArrayList<VEEAccount> accounts;
        private boolean flag = true;

        public AccountAdapter(ArrayList<VEEAccount> accounts) {
            this.accounts = accounts;
        }

        public Boolean getFlag() { return flag; }
        public void setFlag(Boolean flag) { this.flag = flag; }

        @Override
        public AccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_account, parent, false);
            AccountHolder holder = new AccountHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final AccountHolder holder, int position) {
            final VEEAccount account = accounts.get(position);
            holder.accountName.setText(account.getAccountName());
            holder.mutatedAddress.setText(account.getMutatedAddress());

            if(flag) {
                holder.qrCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.createExportAddressDialog(activity, account);
                    }
                });
            }
            else {
                holder.qrCode.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }
    }
}
