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
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			String lo = "" + location.getLatitude();
		}
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					String lo = "" + location.getLatitude();
				}
			}

			public void onProviderDisabled(String provider) {
				showDialog(2);
			}

			public void onProviderEnabled(String provider) {

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.i("LocAndroid", "Provider Status: " + status);

			}
		};
		locationManager.requestLocationUpdates(a, 500, 1, locationListener);
		int hab = 0;
		try {
			BufferedReader habil = new BufferedReader(new FileReader(
					getFilesDir() + "/habilitado.txt"));
			hab = Integer.parseInt(habil.readLine());
			habil.close();
			Log.d("a", "" + hab);
			if (hab == 1) {
				locationManager.removeUpdates(locationListener);
				startActivity(new Intent(MainActivity.this, Mapa.class));
				finish();
			} else {
				Log.d("a", "" + hab);
			}

		} catch (Exception e) {
			Log.d("TAG", "TAG: Error al leer ");
			e.printStackTrace();

		}
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
				actualizarPosicion();

			}
		});

	}

	private void actualizarPosicion() {
		// Obtenemos una referencia al LocationManager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Obtenemos la última posición conocida
		Location location = locationManager.getLastKnownLocation(a);

		// Mostramos la última posición conocida
		muestraPosicion(location);

		// Nos registramos para recibir actualizaciones de la posición
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {

			}

			public void onProviderDisabled(String provider) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.i("LocAndroid", "Provider Status: " + status);

			}
		};

	}

	private void muestraPosicion(Location loc) {
		if (loc != null) {
			try {
				BufferedWriter fichero = new BufferedWriter(new FileWriter(
						getFilesDir() + "/posicion.txt"));
				String pos = "" + loc.getLatitude();
				fichero.write((String) pos);
				fichero.newLine();
				pos = "" + loc.getLongitude();
				fichero.write((String) pos);
				fichero.close();
				BufferedWriter habil = new BufferedWriter(new FileWriter(
						getFilesDir() + "/habilitado.txt"));
				String a = "1";
				habil.write(a);
				habil.close();
				locationManager.removeUpdates(locationListener);
				startActivity(new Intent(MainActivity.this, Mapa.class));
				finish();
			} catch (Exception ex) {
				Log.e("Ficheros", "Error al escribir fichero a memoria interna");
			}

		} else {
				showDialog(0);

			// buscar.setEnabled(false);
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
			dialogo2.setMessage("¿Ir a ajustes del GPS?")
					.setTitle("GPS APAGADO")
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(1);
		}
		return true;
	}

}
