package tech.vee.veecoldwallet.Activity;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import tech.vee.veecoldwallet.R;

public class ScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {
    DecoratedBarcodeView scannerView;
    CaptureManager capture;
    ImageView torch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannerView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        torch = (ImageView) findViewById(R.id.torch);
        torch.setTag("OFF");
        torch.setColorFilter(getResources().getColor(R.color.white));

        scannerView.setTorchListener(this);
        if (!hasFlash()) {
            torch.setVisibility(View.GONE);
        }
        //Toast.makeText(this, "Scanner initiated", Toast.LENGTH_LONG).show();
        capture = new CaptureManager(this, scannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        capture.onSaveInstanceState(outstate);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void onTorchOn() {
        torch.setTag("ON");
        torch.setColorFilter(getResources().getColor(R.color.orange));

    }

    @Override
    public void onTorchOff() {
        torch.setTag("OFF");
        torch.setColorFilter(getResources().getColor(R.color.white));
    }

    public void toggleTorch(View view) {
        if ("OFF".equals(torch.getTag())) {
            scannerView.setTorchOn();
        }
        else {
            scannerView.setTorchOff();
        }
    }
}
