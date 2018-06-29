package tech.vee.veecoldwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScannerActivity extends AppCompatActivity {
    DecoratedBarcodeView scannerView;
    CaptureManager capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannerView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Toast.makeText(this,"Scanner initiated",Toast.LENGTH_LONG).show();
        capture = new CaptureManager(this,scannerView);
        capture.initializeFromIntent(getIntent(),savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume(){
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);
        capture.onSaveInstanceState(outstate);
    }
}
