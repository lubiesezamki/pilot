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
	double szerokosc; //szeroko�� geograficzna
	double dlugosc; //d�ugo�� geograficzna
	
	//minimalny dystans po kt�rym nast�pi aktualizacja (metry)
	private static final long MIN_DIST_UPDATE = 10;
	
	//minimalny czas po kt�rym nast�pi aktualizacja (milisekundy)
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
				//�aden "prowajder" nie zosta� odnaleziony
			}else{
				this.canGetLocation = true; //je�li zosta� odnaleziony to zmienna zmienia warto��
				
				//najpierw pobieramy dane z sieci
				if(NetEnabled){
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DIST_UPDATE, this);
					Log.d("Sie�", "Sie�");
					if(locationManager != null){
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location != null){
							szerokosc = location.getLatitude();
							dlugosc = location.getLongitude();
						}
					}
				}
				//Je�li GPS jest w��czony pobierzemy dane za jego pomoc�
				if(GPSEnabled){
					if(location == null){
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DIST_UPDATE, this);
						Log.d("GPS w��czony", "GPS w��czony");
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
	
	//ta funkcja wy��czy GPS
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(DaneGPS.this);
		}
	}

	//funkcja do otrzymywania szeroko�ci geograficznej
	public double Szerokosc(){
		if(location != null){
			szerokosc = location.getLatitude();
		}
		return szerokosc;
	}
	
	//funkcja zwracaj�ca d�ugo�� geograficzn�
	public double Dlugosc(){
		if(location != null){
			dlugosc = location.getLongitude();
		}
		return dlugosc;
	}
	
	//funkcja sprawdzajaca czy GPS/Sie� s� uruchomione
	public boolean canGetLocation(){
		return this.canGetLocation;
	}
	
	//funkcja odpowiadajaca za w��czanie okna ustawie�
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		
		//Tytu� okienka dialogowego
		alertDialog.setTitle("Ustawienia GPS");
		//Wiadomo�� do cz�owieka
		alertDialog.setMessage("Chcia�by� w��czy� GPS, bo akurat nie dzia�a?");
		//Gdy wci�nie przycisk ustawie� stanie si� to
		alertDialog.setPositiveButton("Ustawienia", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);	
			}
		});
		//a je�li naci�nie przycisk Anuluj to
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.cancel();
			}
		});
		
		//pokazywanie ostrze�enia
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
