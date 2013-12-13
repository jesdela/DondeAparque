package com.jldes.dondeaparque;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Mapa extends SherlockFragmentActivity implements LocationListener {
	private GoogleMap mapa = null;
	private MarkerOptions coche;
	private MarkerOptions yo;
	private LocationManager locationManager;
	static double lat;
	static double lon;
	static double lat2;
	static double lon2;

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_mapas);
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.titulo));

		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.fondoabar));
		// StrictMode.setThreadPolicy(policy);
		ImageView imageView = (ImageView) findViewById(R.id.boton_pos);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CameraPosition campos2 = new CameraPosition(new LatLng(lat2,
						lon2), 18, 0, 0);
				CameraUpdate camUpd2 = CameraUpdateFactory
						.newCameraPosition(campos2);
				mapa.animateCamera(camUpd2);
			}
		});
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
								locationManager.removeUpdates(Mapa.this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		menu.findItem(R.id.menu_coche).getIcon()
				.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.compartir:
			Geocoder geocoder = new Geocoder(Mapa.this, Locale.getDefault());
			try {
				List<Address> addresses = geocoder.getFromLocation(
						coche.getPosition().latitude,
						coche.getPosition().longitude, 1);
				if (addresses.size() > 0) {
					Social.share(this,
							getResources().getString(R.string.app_name),
							addresses.get(0).getAddressLine(0));
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.menu_coche:
			CameraPosition camPos3 = new CameraPosition(new LatLng(lat, lon),
					18, 0, 0);
			mapa.animateCamera(CameraUpdateFactory.newCameraPosition(camPos3));
			break;
		case R.id.guardar:
			showDialog(0);
			break;
		case R.id.ayuda:
			startActivity(new Intent(Mapa.this, Ayuda.class));
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

	private void empezar() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("opciones",
				MODE_PRIVATE);
		lat = preferences.getFloat("latitud", 0);
		lon = preferences.getFloat("longitud", 0);
		coche = new MarkerOptions()
				.position(new LatLng(lat, lon))
				.title("Coche")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.indicador_coche));
		mapa = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mapa.addMarker(coche);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
		final Location loc = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		yo = new MarkerOptions().title("Yo").icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.indicador_persona));
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				actualizarposicion(loc);
			}
		};
		Thread tiempo = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					handler.post(runnable);
				} catch (Exception e) {
				}
				// TODO: handle exception
			}
		};
		tiempo.start();
	}

	private void mostrarMarcador(double lat, double lng, int i) {
		switch (i) {
		case 0:
			mapa.addMarker(coche);
			break;

		case 1:
			mapa.addMarker(yo);
			break;
		}
	}

	public Dialog onCreateDialog(int id) {
		// Use the Builder class for convenient dialog construction

		Dialog dialog = null;
		switch (id) {
		case 0:
			AlertDialog.Builder dialogo0 = new AlertDialog.Builder(this);
			dialogo0.setMessage(
					"¿SEGURO QUE QUIERES BORRAR LA POSICIÓN ACTUAL?")
					.setPositiveButton("SÍ",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SharedPreferences preferences = getSharedPreferences(
											"opciones", MODE_PRIVATE);
									SharedPreferences.Editor editor = preferences
											.edit();
									editor.putBoolean("habil", false);
									editor.commit();
									startActivity(new Intent(Mapa.this,
											MainActivity.class));
									finish();

								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
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
									locationManager.removeUpdates(Mapa.this);
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
			dialogo2.setMessage("NO SE PUEDE ENCONTRAR LA POSICIÓN")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = dialogo2.create();
			break;
		case 3:
			AlertDialog.Builder dialogo3 = new AlertDialog.Builder(this);
			dialogo3.setMessage("¿Usar Wi-Fi para obtener posición?")
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
							});
			dialog = dialogo3.create();
			break;

		}

		return dialog;
	}

	public void actualizarposicion(Location location) {
		if (location != null) {
			lat2 = location.getLatitude();
			lon2 = location.getLongitude();
			mapa.clear();
			mapa.addMarker(coche);
			yo.position(new LatLng(lat2, lon2));
			mapa.addMarker(yo);
			// ruta();
			CameraPosition campos2 = new CameraPosition(new LatLng(lat2, lon2),
					18, 60, location.getBearing());
			CameraUpdate camUpd2 = CameraUpdateFactory
					.newCameraPosition(campos2);
			mapa.animateCamera(camUpd2);

		} else {
			showDialog(2);
		}
	}

	private void mostrarRuta(LatLng inicio, LatLng fin) {
		// TODO Auto-generated method stub
		PolylineOptions lineas = new PolylineOptions().add(inicio).add(fin);

		lineas.width(8);
		lineas.color(Color.parseColor("#84C225"));

		mapa.addPolyline(lineas);

	}

	public void onBackPressed() {
		showDialog(1);
	}

	private void ruta() {
		JSONObject json = this.rutaEntreDosPuntos();
		try {
			ArrayList<LatLng> puntosRuta = new ArrayList<LatLng>();
			JSONArray ruta = json.getJSONArray("routes").getJSONObject(0)
					.getJSONArray("legs").getJSONObject(0)
					.getJSONArray("steps");

			int numTramos = ruta.length();
			LatLng inicio;
			LatLng fin;
			for (int i = 0; i < numTramos; i++) {
				String puntosCodificados = ruta.getJSONObject(i)
						.getJSONObject("polyline").getString("points");
				ArrayList<LatLng> puntosTramo = obtenPuntosTramo(puntosCodificados);
				puntosRuta.addAll(puntosTramo);
				inicio = new LatLng(Double.parseDouble(ruta.getJSONObject(i)
						.getJSONObject("start_location").getString("lat")),
						Double.parseDouble(ruta.getJSONObject(i)
								.getJSONObject("start_location")
								.getString("lng")));
				fin = new LatLng(
						Double.parseDouble(ruta.getJSONObject(i)
								.getJSONObject("end_location").getString("lat")),
						Double.parseDouble(ruta.getJSONObject(i)
								.getJSONObject("end_location").getString("lng")));
				mostrarRuta(inicio, fin);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ArrayList<LatLng> obtenPuntosTramo(String puntosCodificados) {
		ArrayList<LatLng> puntosDecodificados = new ArrayList<LatLng>();
		puntosCodificados = puntosCodificados.replace("\\\\",
				String.valueOf('\\'));

		int i = 0;
		int latitud = 0;
		int longitud = 0;
		while (i < puntosCodificados.length()) {
			int c;
			int desplazamiento = 0;
			int resultado = 0;
			do {
				c = puntosCodificados.charAt(i++) - 63;
				resultado |= (c & 0x1f) << desplazamiento;
				desplazamiento += 5;
			} while (c >= 0x20);
			int auxLat = ((resultado & 1) != 0 ? ~(resultado >> 1)
					: (resultado >> 1));
			latitud += auxLat;

			desplazamiento = 0;
			resultado = 0;
			do {
				c = puntosCodificados.charAt(i++) - 63;
				resultado |= (c & 0x1f) << desplazamiento;
				desplazamiento += 5;
			} while (c >= 0x20);
			int auxLng = ((resultado & 1) != 0 ? ~(resultado >> 1)
					: (resultado >> 1));
			longitud += auxLng;

			LatLng p = new LatLng((int) (((double) latitud / 1E5) * 1E6),
					(int) (((double) longitud / 1E5) * 1E6));
			puntosDecodificados.add(p);
		}
		return puntosDecodificados;
	}

	private JSONObject rutaEntreDosPuntos() {
		String url = "http://maps.google.com/maps/api/directions/json?origin=";
		url = url.concat(String.valueOf(lat2));
		url = url.concat(",");
		url = url.concat(String.valueOf(lon2));
		url = url.concat("&destination=");
		url = url.concat(String.valueOf(lat));
		url = url.concat(",");
		url = url.concat(String.valueOf(lon));
		url = url.concat("&mode=walking&unitsystem=metric&sensor=false");

		HttpGet httpGet = new HttpGet(url);
		HttpClient cliente = new DefaultHttpClient();
		HttpResponse respuesta;
		StringBuilder cons = new StringBuilder();
		try {
			respuesta = cliente.execute(httpGet);
			HttpEntity entidad = respuesta.getEntity();
			InputStream stream = entidad.getContent();
			int i;
			while ((i = stream.read()) != -1) {
				cons.append((char) i);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject json = new JSONObject();
		try {
			json = new JSONObject(cons.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		comprovarconexion();
		actualizarposicion(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		showDialog(3);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
