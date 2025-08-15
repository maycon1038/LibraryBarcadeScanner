package br.com.msm.librarybarcodescanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.msm.themes.BaseActivity;
import com.msm.themes.ThemeUtil;

import static com.msm.themes.ThemeUtil.THEME_BLUE;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.setMyTheme(this, THEME_BLUE);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startScan();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	private void startScan() {
		final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
				.withActivity(MainActivity.this)
				.withEnableAutoFocus(true)
				.withBleepEnabled(true)
				.withBackfacingCamera()
				.withText("Scanning...")
				.withCenterTracker()
				.withCenterTracker(R.drawable.bacground_ret_barcode, R.drawable.bacground_ret_barcode_update)
				.withResultListener((barcode, code) -> {
                    if (barcode == null) {

                        Log.d("Resultado", String.valueOf(code.matches("^(http|https|ftp)://.*$")));

                        Toast.makeText(MainActivity.this, code, Toast.LENGTH_SHORT).show();
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
                })
				.build();
		materialBarcodeScanner.startScan();
	}


	private void shareDeepLink(String deepLink) {


		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
			startActivity(myIntent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Nenhum aplicativo pode lidar com essa solicitação. Instale um navegador da web",  Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}

/*barcodeResult = barcode;
						result.setText(barcode.rawValue);*/
