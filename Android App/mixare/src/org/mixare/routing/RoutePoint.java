package org.mixare.routing;

import java.sql.Time;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class RoutePoint
{
	public GeoPoint location;
	
	/**
	 * Either:
	 * 		start
	 * 		walking
	 * 		bus
	 * 		rail
	 * 		destination
	 * 
	 * Based on value determine what overlay to add the RoutePoint to
	 */
	public String type;
	
	/**
	 * Title for map overlay
	 */
	public String title;
	
	/**
	 * Information to be display under overlay
	 */
	public String snippet;
	
	/**
	 * 
	 */
	public Time arrivalTime;
	
	/**
	 * 
	 */
	public RoutePoint nextRoutePoint;
	
	
	
	public RoutePoint(GeoPoint location, String type, String title,
						String snippet, Time arrivalTime, RoutePoint nextRoutePoint)
	{
		super();
		this.location = location;
		this.type = type;
		this.title = title;
		this.snippet = snippet;
		this.arrivalTime = arrivalTime;
		this.nextRoutePoint = nextRoutePoint;
	}
	
	public RoutePoint(double lat, double lng, String type, String title,
			String snippet, Time arrivalTime, RoutePoint nextRoutePoint)
	{
		//Fix
	}

	//FIX ME
	//Use all data and getMarker to create the overlay item for you
	public OverlayItem createOverlayItem()
	{
		return null;
	}
	
	//Based on "type"
	public Drawable getMarker()
	{
		return null;
	}
}