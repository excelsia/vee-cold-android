package tech.vee.veecoldwallet.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import org.w3c.dom.Text;

import tech.vee.veecoldwallet.R;
import tech.vee.veecoldwallet.Util.FileUtil;
import tech.vee.veecoldwallet.Util.JsonUtil;
import tech.vee.veecoldwallet.Util.UIUtil;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class SetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "Winston";
    private static final String WALLET_FILE_NAME = "wallet.dat";

    private Activity activity;
    private ActionBar actionBar;

    private TextInputLayout setLayout, confirmLayout;
    private TextInputEditText setEdit, confirmEdit;
    private TextView passwordStrength;
    private Button confirm;

    private View passwordStrength1;
    private View passwordStrength2;
    private View passwordStrength3;
    private View passwordStrength4;
    private View passwordStrength5;

    private int strength1, strength2, strength3, strength4, strength5, baseColor;

    private String walletFilePath;
    private String backupWalletFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        activity = this;
        walletFilePath = getFilesDir().getPath() + "/" + WALLET_FILE_NAME;

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_lock);
        Bitmap b = ((BitmapDrawable)icon).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 120, 120, false);
        icon = new BitmapDrawable(getResources(), bitmapResized);
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        actionBar.setTitle(R.string.title_set_password);

        strength1 = getResources().getColor(R.color.passwordStrength1);
        strength2 = getResources().getColor(R.color.passwordStrength2);
        strength3 = getResources().getColor(R.color.passwordStrength3);
        strength4 = getResources().getColor(R.color.passwordStrength4);
        strength5 = getResources().getColor(R.color.passwordStrength5);
        baseColor = getResources().getColor(R.color.textHint);

        setLayout= (TextInputLayout) findViewById(R.id.set_password_layout);
        setEdit = (TextInputEditText) findViewById(R.id.set_password_edit);
        confirmLayout= (TextInputLayout) findViewById(R.id.confirm_password_layout);
        confirmEdit = (TextInputEditText) findViewById(R.id.confirm_password_edit);
        passwordStrength1 = findViewById(R.id.password_strength_1);
        passwordStrength2 = findViewById(R.id.password_strength_2);
        passwordStrength3 = findViewById(R.id.password_strength_3);
        passwordStrength4 = findViewById(R.id.password_strength_4);
        passwordStrength5 = findViewById(R.id.password_strength_5);
        passwordStrength = (TextView) findViewById(R.id.password_strength_text);
        confirm = (Button) findViewById(R.id.set_password_confirm);

        setStrength0();

        Intent intent = getIntent();
        final String seed = intent.getStringExtra("SEED");
        final Zxcvbn zxcvbn = new Zxcvbn();

        setEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // Change password meter color and text according to password strength
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Strength strength = zxcvbn.measure(s.toString());
                int strengthVal = strength.getScore() + 1;
                if (s.length() == 0) { strengthVal = 0; }

                switch (strengthVal) {
                    case 0:
                        setStrength0();
                        break;

                    case 1:
                        setStrength1();
                        break;

                    case 2:
                        setStrength2();
                        break;

                    case 3:
                        setStrength3();
                        break;

                    case 4:
                        setStrength4();
                        break;

                    case 5:
                        setStrength5();
                }
                Log.d("Winston", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = setEdit.getText().toString();
                if(password.equals(confirmEdit.getText().toString())){
                    Toast.makeText(activity, "Password: " + password, Toast.LENGTH_LONG).show();
                    UIUtil.createAccountNumberDialog(activity, seed);
                }
                else {
                    setEdit.setText("");
                    setEdit.clearFocus();
                    confirmEdit.setText("");
                    confirmEdit.clearFocus();
                    Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setError(TextInputLayout layout, TextInputEditText editText) {
        if(editText.getText().toString().isEmpty()){
            layout.setError("Missing required field");
        }else{
            layout.setError(null);
        }
    }

    private void setStrength0() {
        passwordStrength1.setBackgroundColor(baseColor);
        passwordStrength2.setBackgroundColor(baseColor);
        passwordStrength3.setBackgroundColor(baseColor);
        passwordStrength4.setBackgroundColor(baseColor);
        passwordStrength5.setBackgroundColor(baseColor);

        passwordStrength.setTextColor(baseColor);
        passwordStrength.setText(R.string.password_strength_0);
    }

    private void setStrength1() {
        passwordStrength1.setBackgroundColor(strength1);
        passwordStrength2.setBackgroundColor(baseColor);
        passwordStrength3.setBackgroundColor(baseColor);
        passwordStrength4.setBackgroundColor(baseColor);
        passwordStrength5.setBackgroundColor(baseColor);

        passwordStrength.setTextColor(strength1);
        passwordStrength.setText(R.string.password_strength_1);
    }

    private void setStrength2() {
        passwordStrength1.setBackgroundColor(strength2);
        passwordStrength2.setBackgroundColor(strength2);
        passwordStrength3.setBackgroundColor(baseColor);
        passwordStrength4.setBackgroundColor(baseColor);
        passwordStrength5.setBackgroundColor(baseColor);

        passwordStrength.setTextColor(strength2);
        passwordStrength.setText(R.string.password_strength_2);
    }

    private void setStrength3() {
        passwordStrength1.setBackgroundColor(strength3);
        passwordStrength2.setBackgroundColor(strength3);
        passwordStrength3.setBackgroundColor(strength3);
        passwordStrength4.setBackgroundColor(baseColor);
        passwordStrength5.setBackgroundColor(baseColor);

        passwordStrength.setTextColor(strength3);
        passwordStrength.setText(R.string.password_strength_3);
    }

    private void setStrength4() {
        passwordStrength1.setBackgroundColor(strength4);
        passwordStrength2.setBackgroundColor(strength4);
        passwordStrength3.setBackgroundColor(strength4);
        passwordStrength4.setBackgroundColor(strength4);
        passwordStrength5.setBackgroundColor(baseColor);

        passwordStrength.setTextColor(strength4);
        passwordStrength.setText(R.string.password_strength_4);
    }

    private void setStrength5() {
        passwordStrength1.setBackgroundColor(strength5);
        passwordStrength2.setBackgroundColor(strength5);
        passwordStrength3.setBackgroundColor(strength5);
        passwordStrength4.setBackgroundColor(strength5);
        passwordStrength5.setBackgroundColor(strength5);

        passwordStrength.setTextColor(strength5);
        passwordStrength.setText(R.string.password_strength_5);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "SELECT_ACCOUNT_NUMBER") {
                int accountNum = intent.getIntExtra("ACCOUNT_NUMBER", 1);
                String seed = intent.getStringExtra("SEED");

                //Toast.makeText(activity, "Seed: " + seed
                //        + "\nAccount Number " + accountNum, Toast.LENGTH_LONG).show();

                VEEWallet wallet = VEEWallet.recover(seed, accountNum);
                FileUtil.save(wallet.getJson(), walletFilePath);
                FileUtil.backup(activity, wallet, WALLET_FILE_NAME);
                Log.d(TAG, wallet.getJson());
                intent = new Intent(activity, ColdWalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register receiver for account number
        registerReceiver(receiver, new IntentFilter("SELECT_ACCOUNT_NUMBER"));
        super.onResume();
    }
}

