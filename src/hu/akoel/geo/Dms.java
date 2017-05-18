package hu.akoel.geo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Geodesy representation conversion functions                       (c) Chris Veness 2002-2016  */
/*                                                                                   MIT Licence  */
/* www.movable-type.co.uk/scripts/latlong.html                                                    */
/* www.movable-type.co.uk/scripts/geodesy/docs/module-dms.html                                    */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

/**
 * Latitude/longitude points may be represented as decimal degrees, or subdivided into sexagesimal
 * minutes and seconds.
 *
 * @module dms
 */
/**
 * Functions for parsing and representing degrees / minutes / seconds.
 */
public class Dms {
	
	public enum Form{
		dms,
		dm,
		d,
		deg_min_sec,
		deg_min,
		deg
	}
	
	public enum Precision{
		CARDINAL,
		INTERCARDINAL,
		SECONDARY_INTERCARDINAL;		
	}

    // note Unicode Degree = U+00B0. Prime = U+2032, Double prime = U+2033
    /**
     * Parses string representing degrees/minutes/seconds into numeric degrees.
     *
     * This is very flexible on formats, allowing signed decimal degrees, or deg-min-sec optionally
     * suffixed by compass direction (NSEW). A variety of separators are accepted (eg 3° 37′ 09″W).
     * Seconds and minutes may be omitted.
     *
     * @param   {string|number} dmsStr - Degrees or deg/min/sec in variety of formats.
     * @returns {number} Degrees as decimal number.
     *
     * @example
     *   var lat = Dms.parseDMS('51° 28′ 40.12″ N');
     *   var lon = Dms.parseDMS('000° 00′ 05.31″ W');
     *   var p1 = new LatLon(lat, lon); // 51.4778°N, 000.0015°W
     */
	
    public static double parseDMS( String dmsStr ) {

        // strip off any sign or compass dir'n & split out separate d/m/s
        String[] dms = dmsStr.trim().replaceAll("^-", "").replaceAll("[NSEW]$/i", "").split("[^0-9.,]+");

        //if (dms[dms.length-1]== "") dms.splice(dms.length-1);  // from trailing symbol
        //if (dms == '') return NaN;

        // and convert to decimal degrees...
        double deg;
        switch (dms.length) {
            case 3:  // interpret 3-part result as d/m/s
                deg = Double.parseDouble( dms[0] ) + Double.parseDouble( dms[1] ) / 60 + Double.parseDouble(  dms[2] ) / 3600;
                break;
            case 2:  // interpret 2-part result as d/m
                deg = Double.parseDouble( dms[0] ) + Double.parseDouble( dms[1] ) / 60;
                break;
            case 1:  // just d (possibly decimal) or non-separated dddmmss
                deg = Double.parseDouble( dms[0] );
               break;
            default:
                return 0.0;
        }

        String negativePattern = "^[-].*|.*[WS]$"; //"^-|[WS]$/i";
        Pattern pattern = Pattern.compile(negativePattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dmsStr); 
        if( matcher.matches() ) deg = -deg;	// take '-', west and south as -ve 

        return deg;
    }
    
    /**
     * Converts decimal degrees to deg/min/sec format
     *  - degree, prime, double-prime symbols are added, but sign is discarded, though no compass
     *    direction is added.
     *
     * @private
     * @param   {number} deg - Degrees to be formatted as specified.
     * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
     * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
     * @returns {string} Degrees formatted as deg/min/secs according to specified format.
     */
    
    private static String toDMS( double deg ){
    	return toDMS( deg, Form.dms, -1 );
    }
    
    private static String toDMS( double deg, Dms.Form format, int dp) {

        // default values

    	if (dp < 0 ) {
            switch (format) {
                case d:    case deg:         dp = 4; break;
                case dm:   case deg_min:     dp = 2; break;
                case dms:  case deg_min_sec: dp = 0; break;
                default:    format = Form.dms; dp = 0;  // be forgiving on invalid format
            }
        }

        deg = Math.abs(deg);  // (unsigned result ready for appending compass dir'n)
        String dms;
        double d, m, s;
        switch (format) {
            default: // invalid format spec!
            case d:    case deg:
                
            	dms = getDecimalFormated( deg, dp ) + '°';
            	//d = deg.toFixed(dp);    // round degrees
                //if (d<100) d = '0' + d; // pad with leading zeros
                //if (d<10) d = '0' + d;
                //dms = d + '°';
                break;

            case dm:   case deg_min:
                
            	dms = getDecimalFormated( deg * 60, dp );
            	
            	
            	//double min = (deg*60).toFixed(dp); // convert degrees to minutes & round
            	double min = ( deg * 60 );		// convert degrees to minutes
                d = Math.floor(min / 60);       // get component deg/min
                //m = (min % 60).toFixed(dp);     // pad with trailing zeros
                m = min % 60; 
                //if (d<100) d = '0' + d;         // pad with leading zeros
                //if (d<10) d = '0' + d;
                //if (m<10) m = '0' + m;
                //dms = d + '°' + m + '′';
                
                dms = d + '°' + getDecimalFormated( m, dp ) + '′';            	
                break;

            case dms:  case deg_min_sec:

                //double sec = (deg*3600).toFixed(dp); // convert degrees to seconds & round
                double sec = deg * 3600; 			// convert degrees to seconds & round
                
                d = Math.floor(sec / 3600);       // get component deg/min/sec
                m = Math.floor(sec/60) % 60;

                //s = (sec % 60).toFixed(dp);       // pad with trailing zeros
                s = sec % 60;       // pad with trailing zeros

                //if (d<100) d = '0' + d;           // pad with leading zeros
                //if (d<10) d = '0' + d;
                //if (m<10) m = '0' + m;
                //if (s<10) s = '0' + s;

                //dms = d + '°' + m + '′' + s + '″';
                dms = getDecimalFormated( d, 0 ) + '°' + getDecimalFormated( m, 0 ) + '′' + getDecimalFormated( s, dp ) + '″';
                break;

        }

        return dms;
    }
    
    private static String toDeg( double deg, int dp ){
    	deg = Math.abs(deg);
    	return getDecimalFormated( deg, dp);
    }
    
    /**
     * Converts numeric degrees to deg/min/sec latitude (2-digit degrees, suffixed with N/S).
     *
     * @param   {number} deg - Degrees to be formatted as specified.
     * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
     * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
     * @returns {string} Degrees formatted as deg/min/secs according to specified format.
     */
    public static String toLat( double deg, Dms.Form format, int dp) {
        String lat = Dms.toDMS(deg, format, dp);
        return lat + (deg<0 ? 'S' : 'N');  // knock off initial '0' for lat!
    }
    
    public static String toLat( double deg, int dp ){
    	String lat = Dms.toDeg(deg, dp);
    	return lat + (deg<0 ? 'S' : 'N'); 
    }
    
    /**
     * Convert numeric degrees to deg/min/sec longitude (3-digit degrees, suffixed with E/W)
     *
     * @param   {number} deg - Degrees to be formatted as specified.
     * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
     * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
     * @returns {string} Degrees formatted as deg/min/secs according to specified format.
     */
    public static String toLon( double deg, Dms.Form format, int dp) {
        String lon = Dms.toDMS(deg, format, dp);
        return lon + (deg<0 ? 'W' : 'E');
    }
    
    public static String toLon( double deg, int dp ){
    	String lon = Dms.toDeg(deg, dp);
    	return lon + (deg<0 ? 'W' : 'E'); 
    }
    
    public static String toAlt( double alt, int dp ){
    	return Dms.getDecimalFormated(alt, dp) + "m";
    }
    
    /**
     * Converts numeric degrees to deg/min/sec as a bearing (0°..360°)
     *
     * @param   {number} deg - Degrees to be formatted as specified.
     * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
     * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
     * @returns {string} Degrees formatted as deg/min/secs according to specified format.
     */
    public static String toBrng( double deg, Dms.Form format, int dp) {
        deg = (deg + 360) % 360;  // normalise -ve values to 180°..360°
        String brng =  Dms.toDMS(deg, format, dp);
        return brng.replace("360", "0");  // just in case rounding took us up to 360°!
    }
    
    /**
     * Returns compass point (to given precision) for supplied bearing.
     *
     * @param   {number} bearing - Bearing in degrees from north.
     * @param   {number} [precision=3] - Precision (1:cardinal / 2:intercardinal / 3:secondary-intercardinal).
     * @returns {string} Compass point for supplied bearing.
     *
     * @example
     *   var point = Dms.compassPoint(24);    // point = 'NNE'
     *   var point = Dms.compassPoint(24, 1); // point = 'N'
     */
    public static String compassPoint(double bearing, Precision precision) {
        // note precision = max length of compass point; it could be extended to 4 for quarter-winds
        // (eg NEbN), but I think they are little used
        bearing = ( ( bearing % 360 ) + 360 ) % 360; // normalise to 0..360
        String point = null;
        switch (precision) {
            
    		default:
        	case CARDINAL: // 4 compass points
                switch ((int)( Math.round( bearing * 4 / 360 ) % 4 )) {
                    case 0: point = "N"; break;
                    case 1: point = "E"; break;
                    case 2: point = "S"; break;
                    case 3: point = "W"; break;
                }
                break;

            case INTERCARDINAL: // 8 compass points
                switch ((int)( Math.round( bearing * 8 / 360 ) % 8 )) {
                    case 0: point = "N";  break;
                    case 1: point = "NE"; break;
                    case 2: point = "E";  break;
                    case 3: point = "SE"; break;
                    case 4: point = "S";  break;
                    case 5: point = "SW"; break;
                    case 6: point = "W";  break;
                    case 7: point = "NW"; break;
                }

                break;

            case SECONDARY_INTERCARDINAL: // 16 compass points
                switch ((int)( Math.round( bearing * 16 / 360 ) % 16 )) {
                    case  0: point = "N";   break;
                    case  1: point = "NNE"; break;
                    case  2: point = "NE";  break;
                    case  3: point = "ENE"; break;
                    case  4: point = "E";   break;
                    case  5: point = "ESE"; break;
                    case  6: point = "SE";  break;
                    case  7: point = "SSE"; break;
                    case  8: point = "S";   break;
                    case  9: point = "SSW"; break;
                    case 10: point = "SW";  break;
                    case 11: point = "WSW"; break;
                    case 12: point = "W";   break;
                    case 13: point = "WNW"; break;
                    case 14: point = "NW";  break;
                    case 15: point = "NNW"; break;
                }
                break;
        }
        return point;

    }
    
    public static String getDecimalFormated(double value, int dp){
    	return String.format("%.0" +String.valueOf(dp)+"f", value);
    }
}
