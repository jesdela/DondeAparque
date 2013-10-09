package com.jldes.dondeaparque;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Mapa extends android.support.v4.app.FragmentActivity {
	private GoogleMap mapa = null;
	private LocationManager locationManager;
	private LocationListener locationListener;
	static double lat;
	static double lon;
	static double lat2;
	static double lon2;
	private String a = LocationManager.GPS_PROVIDER;

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mapas);
		SharedPreferences preferences = getSharedPreferences("opciones",
				MODE_PRIVATE);
		lat = preferences.getFloat("latitud", 0);
		lon = preferences.getFloat("longitud", 0);
		mapa = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		actualizarposicion(loc);

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				actualizarposicion(location);
			}

			public void onProviderDisabled(String provider) {
//				showDialog(3);
			}

			public void onProviderEnabled(String provider) {

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

		};
		locationManager.requestLocationUpdates(a, 0, 0, locationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.menu_yo) {
			CameraPosition campos2 = new CameraPosition(new LatLng(lat2, lon2),
					18, 0, 0);
			CameraUpdate camUpd2 = CameraUpdateFactory
					.newCameraPosition(campos2);
			mapa.animateCamera(camUpd2);
		} else if (item.getItemId() == R.id.menu_coche) {
			CameraPosition camPos3 = new CameraPosition(new LatLng(lat, lon),
					18, 0, 0);
			mapa.animateCamera(CameraUpdateFactory.newCameraPosition(camPos3));
		} else if (item.getItemId() == R.id.guardar) {
			showDialog(0);
		} else if (item.getItemId() == R.id.ayuda) {
			startActivity(new Intent(Mapa.this, Ayuda.class));
		}

		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.menu_yo:
	// CameraPosition campos2 = new CameraPosition(new LatLng(lat2, lon2),
	// 18, 0, 0);
	// // Centramos el mapa en España y con nivel de zoom 5
	// CameraUpdate camUpd2 = CameraUpdateFactory
	// .newCameraPosition(campos2);
	// mapa.animateCamera(camUpd2);
	// break;
	// case R.id.menu_coche:
	// CameraPosition camPos3 = new CameraPosition(new LatLng(lat, lon),
	// 18, 0, 0);
	// mapa.animateCamera(CameraUpdateFactory.newCameraPosition(camPos3));
	// break;
	// case R.id.guardar:
	// showDialog(0);
	// break;
	// case R.id.ayuda:
	// startActivity(new Intent(Mapa.this, Ayuda.class));
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}

	private void mostrarMarcador(double lat, double lng, int i) {
		switch (i) {
		case 0:
			mapa.addMarker(new MarkerOptions()
					.position(new LatLng(lat, lng))
					.title("Coche")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.indicador_coche)));
			break;

		case 1:
			mapa.addMarker(new MarkerOptions()
					.position(new LatLng(lat, lng))
					.title("Yo")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.indicador_persona)));
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
									try {
										File archivo1 = new File(getFilesDir()
												+ "/habilitado.txt");
										archivo1.delete();
										File archivo2 = new File(getFilesDir()
												+ "/posicion.txt");
										archivo2.delete();
										locationManager
												.removeUpdates(locationListener);
										startActivity(new Intent(Mapa.this,
												MainActivity.class));
										finish();
									} catch (Exception ex) {
										Log.e("Ficheros",
												"Error al escribir fichero a memoria interna");
									}
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
			ruta();
			mostrarMarcador(lat, lon, 0);
			mostrarMarcador(lat2, lon2, 1);
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
		mapa.clear();
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

}
