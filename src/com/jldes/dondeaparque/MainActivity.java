package com.jldes.dondeaparque;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Thread tiempo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.fondoabar));
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.titulo));
		setContentView(R.layout.activity_main);
		comprovarconexion();
		empezar();
	}

	private void comprovarconexion() {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialogo3 = new AlertDialog.Builder(this);
		dialogo3.setMessage(
				"Comprueva tu conexión de datos y vuelve a intentarlo")
				.setTitle("Sin conexión de red")
				.setPositiveButton("Volver a intentar",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								comprovarconexion();
							}
						})
				.setNegativeButton("Salir",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stublocationManager
								if (locationListener != null) {
									locationManager
											.removeUpdates(locationListener);
								}
								finish();
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = dialogo3.create();
		if (!isOnline()) {
			alertDialog.show();
		}
	}

	public boolean isOnline() {
		Context context = getApplicationContext();
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectMgr != null) {
			NetworkInfo[] netInfo = connectMgr.getAllNetworkInfo();
			if (netInfo != null) {
				for (NetworkInfo net : netInfo) {
					if (net.getState() == NetworkInfo.State.CONNECTED) {
						Log.d("Red", "Si");
						return true;
					}
				}
			}
		} else {
			Log.d("NETWORK", "No network available");
		}
		Log.d("Red", "No");
		return false;
	}

	private void empezar() {
		// TODO Auto-generated method stub
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
		RelativeLayout principal = (RelativeLayout) findViewById(R.id.principal);
		principal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				locationManager.removeUpdates(locationListener);
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

		AlertDialog dialog = null;
		switch (id) {
		case 0:
			AlertDialog.Builder dialogo0 = new AlertDialog.Builder(this);
			dialogo0.setMessage("No se puede encontrar la posición")
					.setPositiveButton("Ok",
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
			dialogo1.setMessage("¿Quieres salir?")
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
			dialogo2.setMessage("¿Ativar servicios de ubicaión?")
					.setTitle("Servicios de ubicación inhabilitados")
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
		case 3:

			break;
		}

		return dialog;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.ayuda:
			startActivity(new Intent(MainActivity.this, Ayuda.class));
			break;

		case R.id.puntuar:
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.jldes.dondeaparque")));

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(1);
	}

}
