package hu.akoel.geo;

/**
 * Latitude/longitude points on an ellipsoidal model earth, with ellipsoid parameters and methods
 * for converting between datums and to cartesian (ECEF) coordinates.
 */
public class LatLonEllipsoidal {
	double lat, lon, height;
	Datums datum;

	/**
     * Creates lat/lon (polar) point with latitude & longitude values, on a specified datum.
     *
     * @param {number}        lat - Geodetic latitude in degrees.
     * @param {number}        lon - Longitude in degrees.
     * @param {number}        [height=0] - Height above ellipsoid in metres.
     * @param {LatLon.datums} [datum=WGS84] - Datum this point is defined within.
     *
     * @example
     *   import LatLon from 'latlon-ellipsoidal';
     *   var p1 = new LatLon(51.4778, -0.0016, 0, LatLon.datums.WGS84);
     */
	public LatLonEllipsoidal( double lat, double lon, double height, Datums datum ){
		this.lat = lat;
		this.lon = lon;
		this.height = height;
		this.datum = datum;
	}

	public LatLonEllipsoidal( double lat, double lon ){
		this.lat = lat;
		this.lon = lon;
		this.height = 0;
		this.datum = Datums.WGS84;
	}
	
    /**
     * Converts ‘this’ lat/lon coordinate to new coordinate system.
     *
     * @param   {LatLon.datums} toDatum - Datum this coordinate is to be converted to.
     * @returns {LatLon} This point converted to new datum.
     *
     * @example
     *   var pWGS84 = new LatLon(51.4778, -0.0016, 0, LatLon.datums.WGS84);
     *   var pOSGB = pWGS84.convertDatum(LatLon.datums.OSGB36); // 51.4773°N, 000.0000°E
     */

    public LatLonEllipsoidal convertDatum( Datums toDatum ) {
        LatLonEllipsoidal oldLatLon = this;
        Transformation transform = null;

        if (oldLatLon.datum.equals( Datums.WGS84 ) ) {

            // converting from WGS84
            transform = toDatum.transformation;
        }

        if (toDatum.equals( Datums.WGS84 ) ) {

            // converting to WGS84; use inverse transform (don't overwrite original!)
        	transform = oldLatLon.datum.transformation.getInverseClone();

        }

        if ( null == transform ) {

            // neither this.datum nor toDatum are WGS84: convert this to WGS84 first
            oldLatLon = this.convertDatum( Datums.WGS84 );
            transform = toDatum.transformation;
        }

        Cartesian oldCartesian = oldLatLon.toCartesian();          // convert polar to cartesian...
        Cartesian newCartesian = oldCartesian.applyTransform(transform); // ...apply transform...
        LatLonEllipsoidal newLatLon = newCartesian.toLatLon(toDatum);            // ...and convert cartesian to polar

        return newLatLon;
    }
    
    /**
     * Converts ‘this’ point from (geodetic) latitude/longitude coordinates to (geocentric) cartesian
     * (x/y/z) coordinates.
     *
     * @returns {Cartesian} Cartesian point equivalent to lat/lon point, with x, y, z in metres from
     *   earth centre.
     */
    public Cartesian toCartesian() {

        double φ = Math.toRadians( this.lat );
        double λ = Math.toRadians( this.lon );
        double h = this.height; // height above ellipsoid

        double a = this.datum.ellipsoid.a;
        double f = this.datum.ellipsoid.f;

        double sinφ = Math.sin(φ);
        double cosφ = Math.cos(φ);
        double sinλ = Math.sin(λ);
        double cosλ = Math.cos(λ);

        double eSq = 2*f - f*f;                      // 1st eccentricity squared ≡ (a²-b²)/a²
        double ν = a / Math.sqrt(1 - eSq*sinφ*sinφ); // radius of curvature in prime vertical

        double x = (ν+h) * cosφ * cosλ;
        double y = (ν+h) * cosφ * sinλ;
        double z = (ν*(1-eSq)+h) * sinφ;

        Cartesian p = new Cartesian(x, y, z);

        return p;
    }
       
    /**
     * Returns a string representation of ‘this’ point, formatted as degrees, degrees+minutes, or
     * degrees+minutes+seconds.
     *
     * @param   {string} [format=dms] - Format point as 'd', 'dm', 'dms'.
     * @param   {number} [dp=0|2|4] - Number of decimal places to use: default 0 for dms, 2 for dm, 4 for d.
     * @param   {number} [heightDp=null] - Number of decimal places to use for height; default is no height display.
     * @returns {string} Comma-separated formatted latitude/longitude.
     */
    public String toString(Dms.Form format, int dp, int heightDp) {
        String height = (this.height>0 ? " + " : "") + Dms.getDecimalFormated( this.height, heightDp) + 'm';
        return Dms.toLat(this.lat, format, dp) + ", " + Dms.toLon(this.lon, format, dp) + height;
    }

    
}
