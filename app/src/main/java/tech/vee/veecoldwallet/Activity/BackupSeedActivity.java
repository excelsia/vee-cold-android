package tech.vee.veecoldwallet.Activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tech.vee.veecoldwallet.R;

public class BackupSeedActivity extends AppCompatActivity {
    private BackupSeedActivity activity;
    private ActionBar actionBar;

    private TextView clear;

    private String seed;
    private String[] words;
    private String[] seedWords;
    private int[] cardInputs;
    private int[] cardWords;
    private String[] addedWords;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_seed);

        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_seed);
        icon.mutate();
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        actionBar.setTitle(R.string.title_backup_seed);

        Intent intent = getIntent();
        seed = intent.getStringExtra("SEED");
        seedWords = seed.split(" ");
        words = reshuffle(Arrays.copyOf(seedWords, seedWords.length));
        counter = -1;

        addedWords = new String[seedWords.length];

        cardWords = new int[]{R.id.backup_seed_word_1, R.id.backup_seed_word_2, R.id.backup_seed_word_3,
                R.id.backup_seed_word_4, R.id.backup_seed_word_5, R.id.backup_seed_word_6,
                R.id.backup_seed_word_7, R.id.backup_seed_word_8, R.id.backup_seed_word_9,
                R.id.backup_seed_word_10, R.id.backup_seed_word_11, R.id.backup_seed_word_12,
                R.id.backup_seed_word_13, R.id.backup_seed_word_14, R.id.backup_seed_word_15};

        cardInputs = new int[]{R.id.backup_seed_input_1, R.id.backup_seed_input_2, R.id.backup_seed_input_3,
                R.id.backup_seed_input_4, R.id.backup_seed_input_5, R.id.backup_seed_input_6,
                R.id.backup_seed_input_7, R.id.backup_seed_input_8, R.id.backup_seed_input_9,
                R.id.backup_seed_input_10, R.id.backup_seed_input_11, R.id.backup_seed_input_12,
                R.id.backup_seed_input_13, R.id.backup_seed_input_14, R.id.backup_seed_input_15};

        for(int i=0; i<words.length; i++) {
            addWord(cardWords[i], words[i]);
        }

        clear = findViewById(R.id.confirm_backup_seed_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    private String[] reshuffle(String[] words) {
        List<String> wordsList = Arrays.asList(words);
        Collections.shuffle(wordsList);
        return wordsList.toArray(new String[wordsList.size()]);
    }

    private void addWord(int card, final String word) {
        final TextView wordCard = findViewById(card);
        wordCard.setText(word);

        wordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordCard.setEnabled(false);
                wordCard.setTextColor(getResources().getColor(R.color.lightGray));
                wordCard.setBackground(getResources().getDrawable(R.drawable.round_corners_dotted_border));
                counter++;
                addedWords[counter] = word;
                TextView input = findViewById(cardInputs[counter]);
                input.setVisibility(View.VISIBLE);
                input.setText(word);
                if (addedWords[addedWords.length - 1] != null){
                    List<String> a = Arrays.asList(seedWords);
                    List<String> b = Arrays.asList(addedWords);
                    Log.d("Winston", a + "\n" + b);
                    if (Arrays.equals(addedWords, seedWords)) {
                        Toast.makeText(activity, "Seed phrase confirmed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(activity, SetPasswordActivity.class);
                        intent.putExtra("SEED", seed);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(activity, "Order incorrect, try again", Toast.LENGTH_LONG).show();
                        clear();
                    }
                }

            }
        });
    }

    private void clear(){
        for (int i=0; i<cardInputs.length; i++) {
            TextView input = findViewById(cardInputs[i]);
            input.setVisibility(View.GONE);

            TextView wordCard = findViewById(cardWords[i]);
            wordCard.setEnabled(true);
            wordCard.setText(words[i]);
            wordCard.setTextColor(getResources().getColor(R.color.white));
            wordCard.setBackground(getResources().getDrawable(R.drawable.rounded_corners_orange));

            counter = -1;
            addedWords = new String[seedWords.length];
        }
    }
}
