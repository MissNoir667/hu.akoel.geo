package hu.akoel.geo;

import hu.akoel.geo.Datums;

 /**
 * Converts ECEF (earth-centered earth-fixed) cartesian coordinates to LatLon points, applies
 * Helmert transformations.
 */
public class Cartesian {
	private double x, y, z;
	
    /**
     * Creates cartesian coordinate representing ECEF (earth-centric earth-fixed) point.
     *
     * @param {number} x - x coordinate in metres (=> 0°N,0°E).
     * @param {number} y - y coordinate in metres (=> 0°N,90°E).
     * @param {number} z - z coordinate in metres (=> 90°N).
     *
     * @example
     *   import { Cartesian } from 'latlon-ellipsoidal';
     *   var coord = new Cartesian(3980581.210, -111.159, 4966824.522);
     */
    public Cartesian( double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Converts ‘this’ (geocentric) cartesian (x/y/z) coordinate to (ellipsoidal geodetic)
     * latitude/longitude point on specified datum.
     *
     * Uses Bowring’s (1985) formulation for μm precision in concise form.
     *
     * @param {LatLon.datums} [datum=WGS84] - Datum to use when converting point.
     */
    public LatLonEllipsoidal toLatLon( Datums datum ) {

        double x = this.x;
        double y = this.y;
        double z = this.z;

        double a = datum.ellipsoid.a;
        double b = datum.ellipsoid.b;
        double f = datum.ellipsoid.f;

        double e2 = 2*f - f*f;   // 1st eccentricity squared ≡ (a²-b²)/a²
        double ε2 = e2 / (1-e2); // 2nd eccentricity squared ≡ (a²-b²)/b²

        double p = Math.sqrt(x*x + y*y); // distance from minor axis
        double R = Math.sqrt(p*p + z*z); // polar radius

        // parametric latitude (Bowring eqn 17, replacing tanβ = z·a / p·b)
        Double tanβ = (b*z)/(a*p) * (1+ε2*b/R);
        Double sinβ = tanβ / Math.sqrt(1+tanβ*tanβ);
        Double cosβ = sinβ / tanβ;

        // geodetic latitude (Bowring eqn 18: tanφ = z+ε²bsin³β / p−e²cos³β)
        double φ = cosβ.isNaN() ? 0 : Math.atan2(z + ε2*b*sinβ*sinβ*sinβ, p - e2*a*cosβ*cosβ*cosβ);

        // longitude
        double λ = Math.atan2(y, x);

        // height above ellipsoid (Bowring eqn 7)
        double sinφ = Math.sin(φ);
        double cosφ = Math.cos(φ);

        double ν = a / Math.sqrt(1-e2*sinφ*sinφ); // length of the normal terminated by the minor axis
        double h = p*cosφ + z*sinφ - (a*a/ν);

        LatLonEllipsoidal point = new LatLonEllipsoidal(Math.toDegrees(φ), Math.toDegrees(λ), h, datum);

        return point;
    }
    
    /**
     * Applies Helmert (seven-parameter) transformation to ‘this’ coordinate using transform
     * parameters t.
     *
     * @param {LatLon.datums.transform} t - Transformation to apply to this coordinate.
     */
    public Cartesian applyTransform( Transformation t)   {

        double x1 = this.x;
        double y1 = this.y;
        double z1 = this.z;

        double tx = t.tTranslation.x; 
     	double ty = t.tTranslation.y;
     	double tz = t.tTranslation.z;

        double rx = Math.toRadians(t.rTranslation.x/3600); // normalise seconds to radians
        double ry = Math.toRadians(t.rTranslation.y/3600); // normalise seconds to radians
        double rz = Math.toRadians(t.rTranslation.z/3600); // normalise seconds to radians

        double s1 = t.rotation.s/1e6 + 1;             // normalise ppm to (s+1)

        // apply transform
        double x2 = tx + x1*s1 - y1*rz + z1*ry;
        double y2 = ty + x1*rz + y1*s1 - z1*rx;
        double z2 = tz - x1*ry + y1*rx + z1*s1;
        Cartesian point = new Cartesian(x2, y2, z2);

        return point;

    }
    
    /**
     * Returns a string representation of ‘this’ cartesian point.
     *
     * @param   {number} [dp=0] - Number of decimal places to use.
     * @returns {string} Comma-separated latitude/longitude.
     */
    public String toString( int dp ) {        	
        return '[' + Dms.getDecimalFormated(this.x, dp) +','+ Dms.getDecimalFormated( this.y, dp ) +',' + Dms.getDecimalFormated( this.z, dp ) + ']';
    }
    


}