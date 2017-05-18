package hu.akoel.geo;

import java.util.Locale;

import hu.akoel.geo.Datums;

public class Start {

	public static void main( String[] args ){
		/*LatLonEllipsoidal el = new LatLonEllipsoidal( 19, 54, 90, Datums.WGS84);
		Cartesian c = el.toCartesian();
		System.err.println(c.toString(3));
		
		LatLonEllipsoidal el2 = c.toLatLon(Datums.WGS84);
		System.out.println( el2.toString(Dms.Form.dms, 2, 2));
		*/
		
		//Utm utm = new Utm( 47.51292, 19.51728, 200.8, Datums.WGS84);
		//utm.toUtm();
	

		//Deg2UTM utm = new Deg2UTM(47.51292, 19.51728);
		//System.err.println(utm.Zone + " - " + utm.Letter + " - " + utm.Easting + " - " + utm.Northing   );
		
		//UTM2Deg utm2 = new UTM2Deg( "34 T 388360 5263231" );
		//System.err.println( utm2.latitude + " - " + utm2.longitude );
		
		
		//double s = Dms.parseDMS("51° 28′ 40.12″ S");
		//System.out.println(s);
		
		//String s = Dms.toDMS(193.5798, Dms.Form.dms, 2);
		//System.out.println(s);
		
		WGS84Form w = WGS84UTM.getUTM2WGS84( new UTMForm(34, 'T', 388360.123, 5263231.456 ) );
		System.err.println( w.toDegree());
		System.err.println( w.toDms() );
		
		UTMForm u = WGS84UTM.getWGS842UTM( w );
		System.out.println( u.toString() );
		
		WGS84Form ww = WGS84UTM.getUTM2WGS84( u );
		System.err.println( ww.toDegree());
		System.err.println( ww.toDms() );
	}
	
}