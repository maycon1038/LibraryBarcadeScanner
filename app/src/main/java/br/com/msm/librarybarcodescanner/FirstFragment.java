package br.com.msm.librarybarcodescanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
    }


    private void startScan() {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(getActivity())
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withCenterTracker()
                .withCenterTracker(R.drawable.bacground_ret_barcode, R.drawable.bacground_ret_barcode_update)
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode, String code) {
                        if (barcode == null) {

                            Log.d("Resultado", String.valueOf(code.matches("^(http|https|ftp)://.*$")));

                            Toast.makeText(getActivity(), code, Toast.LENGTH_SHORT).show();
                            if (code.matches("^(http|https|ftp)://.*$")) {
                                shareDeepLink(code);
                            }else{
                                //	Toast.makeText(MainActivity.this,code, Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Log.d("Resultado", String.valueOf(barcode.rawValue.matches("^(http|https|ftp)://.*$")));

                            if (barcode.rawValue.matches("^(http|https|ftp)://.*$")){
                                shareDeepLink(barcode.rawValue);
                            }else{
                                //	Toast.makeText(MainActivity.this, barcode.rawValue, Toast.LENGTH_SHORT).show();
                            }
                        }

					/*	barcodeResult = barcode;
						result.setText(barcode.rawValue); */
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }


    private void shareDeepLink(String deepLink) {


        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Nenhum aplicativo pode lidar com essa solicitação. Instale um navegador da web",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}