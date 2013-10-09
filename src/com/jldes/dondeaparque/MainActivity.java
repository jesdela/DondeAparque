package com.jldes.dondeaparque;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private String a = LocationManager.GPS_PROVIDER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		SharedPreferences preferences = getSharedPreferences("opciones",
				MODE_PRIVATE);
		if (preferences.getBoolean("habil", false)) {
			startActivity(new Intent(MainActivity.this, Mapa.class));
			finish();
		}
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				showDialog(2);
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

			}
		};
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		ImageView ayuda = (ImageView) findViewById(R.id.boton_ayuda);
		ayuda.setColorFilter(Color.parseColor("#84C225"));
		ayuda.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, Ayuda.class));
			}
		});
		RelativeLayout principal = (RelativeLayout) findViewById(R.id.principal);
		principal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actualizarPosicion(location);

			}
		});

	}

	private void actualizarPosicion(Location location) {
		// Obtenemos una referencia al LocationManager
		if (location != null) {
			SharedPreferences preferences = getSharedPreferences("opciones",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("habil", true);
			editor.putFloat("latitud", (float) location.getLatitude());
			editor.putFloat("longitud", (float) location.getLongitude());
			editor.commit();
			locationManager.removeUpdates(locationListener);
			startActivity(new Intent(MainActivity.this, Mapa.class));
			finish();
		} else {
			showDialog(0);
		}

	}

	public Dialog onCreateDialog(int id) {
		// Use the Builder class for convenient dialog construction

		Dialog dialog = null;
		switch (id) {
		case 0:
			AlertDialog.Builder dialogo0 = new AlertDialog.Builder(this);
			dialogo0.setMessage("NO SE PUEDE ENCONTRAR LA POSICIÓN")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = dialogo0.create();
			// Create the AlertDialog object and return i
			break;
		case 1:
			AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
			dialogo1.setMessage("¿QUIERES SALIR?")
					.setPositiveButton("SÍ",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									locationManager
											.removeUpdates(locationListener);
									finish();
								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			dialog = dialogo1.create();
			// Create the AlertDialog object and return i
			break;
		case 2:
			AlertDialog.Builder dialogo2 = new AlertDialog.Builder(this);
			dialogo2.setMessage("¿Usar Wi-Fi para obtener posición?")
					.setTitle("SIN DATOS WI-FI")
					.setPositiveButton("Aceptar",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent actividad = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(actividad);
									dialog.cancel();
								}
							})
					.setNegativeButton("Cancelar",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method
									// stublocationManager
									locationManager
											.removeUpdates(locationListener);
									finish();
									dialog.cancel();
								}
							});
			dialog = dialogo2.create();
			break;
		}

		return dialog;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(1);
		super.onBackPressed();
	}

}
