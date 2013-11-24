package com.example.pilotmpk;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


public class DaneGPS extends Service implements LocationListener {
	
	private final Context mContext;
	
	//Status GPS
	boolean GPSEnabled = false;
	
	//Status sieci
	boolean NetEnabled = false;
	
	boolean canGetLocation = false;
	
	Location location; //lokalizacja
	double szerokosc; //szerokoœæ geograficzna
	double dlugosc; //d³ugoœæ geograficzna
	
	//minimalny dystans po którym nast¹pi aktualizacja (metry)
	private static final long MIN_DIST_UPDATE = 10;
	
	//minimalny czas po którym nast¹pi aktualizacja (milisekundy)
	private static final long MIN_TIME_UPDATE = 1000 * 60;
	
	//Location Manager
	protected LocationManager locationManager;
	
	public DaneGPS(Context context){
		this.mContext = context;
		getLocation();
	}
	
	public Location getLocation(){
		try{
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);
			
			//otrzymywanie statusu GPS
			GPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			//otrzymywanie statusu sieci
			NetEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if(!GPSEnabled && !NetEnabled){
				//¿aden "prowajder" nie zosta³ odnaleziony
			}else{
				this.canGetLocation = true; //jeœli zosta³ odnaleziony to zmienna zmienia wartoœæ
				
				//najpierw pobieramy dane z sieci
				if(NetEnabled){
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DIST_UPDATE, this);
					Log.d("Sieæ", "Sieæ");
					if(locationManager != null){
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location != null){
							szerokosc = location.getLatitude();
							dlugosc = location.getLongitude();
						}
					}
				}
				//Jeœli GPS jest w³¹czony pobierzemy dane za jego pomoc¹
				if(GPSEnabled){
					if(location == null){
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DIST_UPDATE, this);
						Log.d("GPS w³¹czony", "GPS w³¹czony");
						if(locationManager != null){
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if(location != null){
								szerokosc = location.getLatitude();
								dlugosc = location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return location;
	}
	
	//ta funkcja wy³¹czy GPS
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(DaneGPS.this);
		}
	}

	//funkcja do otrzymywania szerokoœci geograficznej
	public double Szerokosc(){
		if(location != null){
			szerokosc = location.getLatitude();
		}
		return szerokosc;
	}
	
	//funkcja zwracaj¹ca d³ugoœæ geograficzn¹
	public double Dlugosc(){
		if(location != null){
			dlugosc = location.getLongitude();
		}
		return dlugosc;
	}
	
	//funkcja sprawdzajaca czy GPS/Sieæ s¹ uruchomione
	public boolean canGetLocation(){
		return this.canGetLocation;
	}
	
	//funkcja odpowiadajaca za w³¹czanie okna ustawieñ
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		
		//Tytu³ okienka dialogowego
		alertDialog.setTitle("Ustawienia GPS");
		//Wiadomoœæ do cz³owieka
		alertDialog.setMessage("Chcia³byœ w³¹czyæ GPS, bo akurat nie dzia³a?");
		//Gdy wciœnie przycisk ustawieñ stanie siê to
		alertDialog.setPositiveButton("Ustawienia", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);	
			}
		});
		//a jeœli naciœnie przycisk Anuluj to
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.cancel();
			}
		});
		
		//pokazywanie ostrze¿enia
		alertDialog.show();
	}
	
	@Override
    public void onLocationChanged(Location location) {
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
