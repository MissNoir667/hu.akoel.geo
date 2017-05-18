package hu.akoel.geo;

public class WGS84Form {
    public Double latitude;
    public Double longitude;
    public Double altitude = null;

	public WGS84Form( Double latitude, Double longitude, Double altitude ){
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public WGS84Form( Double latitude, Double longitude ){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	* @example
    *   ('51° 28′ 40.12″ N');
    *   ('000° 00′ 05.31″ W');
    *   ('51.4778°N');
    *   (000.0015°W );
    */
	public WGS84Form( String latitudeDms, String longitudeDms, Double altitude ){
		this.latitude = Dms.parseDMS( latitudeDms );
		this.longitude = Dms.parseDMS( longitudeDms );
		this.altitude = altitude;
	}

	/**
	* @example
    *   ('51° 28′ 40.12″ N');
    *   ('000° 00′ 05.31″ W');
    *   ('51.4778°N');
    *   (000.0015°W );
    */
	public WGS84Form( String latitudeDms, String longitudeDms ){
		this.latitude = Dms.parseDMS( latitudeDms );
		this.longitude = Dms.parseDMS( longitudeDms );
	}

	public String toDegree(){
		int dp = 6;
		return new String( 
				Dms.toLat( latitude, dp) + "° " +
		        Dms.toLon( longitude, dp) + "°" +
		        ( ( null == altitude ) ? "" : " alt: " + Dms.toAlt(altitude, dp) + "m" ) );
	}
	
	public String toDms(){
		int dp = 2;
		return new String( 
				Dms.toLat(latitude, Dms.Form.dms, dp) + " " +
		        Dms.toLon(longitude, Dms.Form.dms, dp) + "" +
		        ( ( null == altitude ) ? "" : "alt: " + Dms.toAlt(altitude, dp) ) );
	}
}
