package com.edwardvanraak.materialbarcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaterialBarcodeScanner {

    /**
     * Request codes
     */
    public static final int RC_HANDLE_CAMERA_PERM = 2;

    /**
     * Scanner modes
     */
    public static final int SCANNER_MODE_FREE = 1;
    public static final int SCANNER_MODE_CENTER = 2;

    protected final MaterialBarcodeScannerBuilder mMaterialBarcodeScannerBuilder;

    private FrameLayout mContentView; //Content frame for fragments

    private OnResultListener onResultListener;

    private String code;

    public void setCode(String code) {
        this.code = code;
    }

    public MaterialBarcodeScanner(@NonNull MaterialBarcodeScannerBuilder materialBarcodeScannerBuilder) {
        this.mMaterialBarcodeScannerBuilder = materialBarcodeScannerBuilder;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBarcodeScannerResult(Barcode barcode){

        onResultListener.onResult(barcode, this.code);
        Log.d(" Testebarcode ",barcode.rawValue);

        EventBus.getDefault().removeStickyEvent(barcode);
        EventBus.getDefault().unregister(this);
        mMaterialBarcodeScannerBuilder.clean();
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnResultListener {
        void onResult(Barcode barcode, String code);
    }

    /**
     * Start a scan for a barcode
     *
     * This opens a new activity with the parameters provided by the MaterialBarcodeScannerBuilder
     */
    public void startScan(){
        EventBus.getDefault().register(this);
        if(mMaterialBarcodeScannerBuilder.getActivity() == null){
            throw new RuntimeException("Não foi possível iniciar a varredura: referência de atividade perdida (reconstrua o Scanner antes de chamar startScan)");
        }
        int mCameraPermission = ActivityCompat.checkSelfPermission(mMaterialBarcodeScannerBuilder.getActivity(), Manifest.permission.CAMERA);
        if (mCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }else{
            //Open activity
            EventBus.getDefault().postSticky(this);
            Intent intent = new Intent(mMaterialBarcodeScannerBuilder.getActivity(), MaterialBarcodeScannerActivity.class);
            mMaterialBarcodeScannerBuilder.getActivity().startActivity(intent);
        }
    }

    private void requestCameraPermission() {
        final String[] mPermissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(mMaterialBarcodeScannerBuilder.getActivity(), Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(mMaterialBarcodeScannerBuilder.getActivity(), mPermissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(mMaterialBarcodeScannerBuilder.getActivity(), mPermissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mMaterialBarcodeScannerBuilder.mRootView, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, listener)
                .show();
    }

    public MaterialBarcodeScannerBuilder getMaterialBarcodeScannerBuilder() {
        return mMaterialBarcodeScannerBuilder;
    }

}
