package org.mixare.routing;

import com.google.android.maps.*;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.sql.Time;
import java.util.*;

import org.mixare.MixView;
import org.mixare.R;
import org.mixare.maps.*;

public class RouteActivity extends MapActivity {
    
	LocationManager locationManager;
	
	public static Context mCtx;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	mCtx = this;
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkingmap);
        Intent startIntent = getIntent();
        Location destLoc = startIntent.getExtras().getParcelable("location");
        Location startLoc = MixView.dataView.getContext().getCurrentLocation();
        
        // Starting Location
        double startLat = startLoc.getLatitude();
        double startLng = startLoc.getLongitude();
        
        // Ending Location
        double endLat = destLoc.getLatitude();
        double endLng = destLoc.getLongitude();
        
        /* Set zoom capability */
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        /* Create map overlays */
        List<Overlay> mapOverlays = mapView.getOverlays();
        
        //Library starting location
        //startLat = 33.775366;
        //startLng = -84.39517;
        
        //Kroger ending location
        //endLat = 33.803186;
        //endLng = -84.41328;
        
                
        
        /*MartaRouting mr = new MartaRouting(startLat, startLng, endLat, endLng);
        
        //TODO: turn off debug mode
        if (MartaRouting.DEBUG_MODE == true)
        {
        	mr = MartaRouting.MartaRoutingTestShort();
        }
        
        List<RoutePoint> route = mr.getRoute();*/
        RoutingSearchProblem sp = new RoutingSearchProblem(this, destLoc, startLoc);
        RoutingSearch search = new RoutingSearch(sp);
        
        List<RoutePoint> route = search.search();
        
        Log.i("ROUTE_LEN", "" + route.size());
        
        Routing router = new Routing();
        
        
        //Bus locations
        double bus1Lat = 33.781197;
        double bus1Lng = -84.398003;
        double bus2Lat = 33.80031;
        double bus2Lng = -84.415716;
        
        RoutePoint rp2 = new RoutePoint(bus2Lat, bus2Lng, "bus", "title 2",
						"snippet 2", Time.valueOf("01:30:00"), null);
        RoutePoint rp1 = new RoutePoint(bus1Lat, bus1Lng, "bus", "title 1",
						"snippet 1", Time.valueOf("01:00:00"), rp2);
       
    	//route = Arrays.asList(new RoutePoint[]
		//{
        //	rp1,
        //	rp2        				
		//});
        
        // ICON SETUP
        // Walking icons 
        Drawable walkingdrawable = this.getResources().getDrawable(R.drawable.walkingman);
        walkingdrawable.setBounds(0, 0, walkingdrawable.getIntrinsicWidth(), walkingdrawable.getIntrinsicHeight());
        
        // Bus icons
        Drawable busdrawable = this.getResources().getDrawable(R.drawable.bus);
        busdrawable.setBounds(0, 0, busdrawable.getIntrinsicWidth(), busdrawable.getIntrinsicHeight());
        
        // Star icons
        Drawable stardrawable = this.getResources().getDrawable(R.drawable.star);
        stardrawable.setBounds(0, 0, stardrawable.getIntrinsicWidth(), stardrawable.getIntrinsicHeight());
        
        HelloItemizedOverlay mainOverlay = new HelloItemizedOverlay(walkingdrawable, this);
        
        /* *
         * Draw Start to Bus 
         * */
        // Walk to the stop.
        RoutePoint first = route.get(0);
        GeoPoint firstStopLoc = first.location;
        ArrayList<GeoPoint> startPoints = (ArrayList<GeoPoint>) router.getRouteGeoPoints(startLat, startLng, firstStopLoc.getLatitudeE6() / 1e6, firstStopLoc.getLongitudeE6() / 1e6, "walking");
        RouteOverlay walkToBus = new RouteOverlay(mapView, Color.GREEN, startPoints);
        mapOverlays.add(walkToBus);
        
        // Walk Start Icon
        GeoPoint start = new GeoPoint((int) (startLat * 1e6), (int) (startLng*1e6));
        OverlayItem startOverlay = new OverlayItem(start, "Walk to First Stop", "Approximately " + distance(startLat, startLng, firstStopLoc.getLatitudeE6() / 1e6, firstStopLoc.getLongitudeE6() / 1e6));
        startOverlay.setMarker(walkingdrawable);
        mainOverlay.addOverlay(startOverlay);
        
        /* *
         * Bus Routing
         * */
        for(int i = 0; i < route.size(); i++)
        {
        	// Add on bus start point overlay
        	RoutePoint rp = route.get(i);
        	mainOverlay.addOverlay(rp.createOverlayItem());
        	
        	
        	// Add route map portion
        	if(rp.nextRoutePoint != null)
        	{
        		GeoPoint startPoint = rp.location;
        		GeoPoint endPoint = rp.nextRoutePoint.location;
        		String type = rp.type;
        		ArrayList<GeoPoint> rpPoints;
        		
        		if(!type.equals("rail"))
        		{
	        		String rtype = type.equals("walking") ? "walking" : "bus";
	        		rpPoints = (ArrayList<GeoPoint>)router.getRouteGeoPoints
	        		(
	        			startPoint.getLatitudeE6() / 1e6, 
	        			startPoint.getLongitudeE6() / 1e6, 
	    				endPoint.getLatitudeE6() / 1e6, 
	    				endPoint.getLongitudeE6() / 1e6, 
	    				rtype
	        		);
        		}
        		else
        		{
        			rpPoints = new ArrayList<GeoPoint>();
        			rpPoints.add(startPoint);
        			rpPoints.add(endPoint);
        		}
        		
        		RouteOverlay busCourse = new RouteOverlay(mapView, Color.GREEN, rpPoints);
        		mapOverlays.add(busCourse);
        	}
        }
        
        /* *
         * Draw Bus to End
         * */
        // Walk to the stop.
        RoutePoint last = route.get(route.size() - 1);
        GeoPoint lastStopLoc = last.location;
        ArrayList<GeoPoint> endPoints = (ArrayList<GeoPoint>) router.getRouteGeoPoints(lastStopLoc.getLatitudeE6() / 1e6, lastStopLoc.getLongitudeE6() / 1e6, endLat, endLng, "walking");
        RouteOverlay walkFromBus = new RouteOverlay(mapView, Color.GREEN, endPoints);
        mapOverlays.add(walkFromBus);
        
        // Walk Start Icon
        GeoPoint end = new GeoPoint((int) (endLat * 1e6), (int) (endLng * 1e6));
        OverlayItem destOverlay = new OverlayItem(end, "Arrive At Destination", "");
        destOverlay.setMarker(stardrawable);
        mainOverlay.addOverlay(destOverlay);
        
        /* *
         * Finalize all overlays
         * */
        //Add all itemized overlays
        mapOverlays.add(mainOverlay);
        /* End Drawing Station */
    }
    
    @Override
    protected boolean isRouteDisplayed() //change if route displayed
    {
        return true;
    }
    
    private double distance(double lat, double lng, double lat2, double lng2)
	{
		double cLat = lat2;
		double cLng = lng2;
		final int R = 6371; // Radious of the earth in meters
		
		Double latDistance = Math.toRadians(cLat-lat);
		Double lonDistance = Math.toRadians(cLng-lng);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
				   Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(cLat)) * 
				   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double distance = R * c;
		
		return distance;
	}
    
    //*********** Implement location updates here
    //requires implementing locationlistener, adding several methods 
//    
//    //Stop location updates if map not visible
//    @Override
//    protected void onPause() {
//        //remove the listener
//        locationManager.removeUpdates(this);
//        super.onPause();
//    }
// 
//@Override
//    protected void onResume() {
//        //add the listener again
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 5000, this);
//        super.onResume();
//    }
//	//end stop/resume location updates

	//****** End location update code
}
