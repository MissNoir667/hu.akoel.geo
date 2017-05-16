package hu.akoel.geo;

public class Utm extends LatLonEllipsoidal{

	public Utm( double lat, double lon, double height, Datums datum ) {
		super(lat, lon, height, datum);
	}

	/**
	 * Converts latitude/longitude to UTM coordinate.
	 *
	 * Implements Karney’s method, using Krüger series to order n^6, giving results accurate to 5nm for
	 * distances up to 3900km from the central meridian.
	 *
	 * @returns {Utm}   UTM coordinate.
	 * @throws  {Error} If point not valid, if point outside latitude range.
	 *
	 * @example
	 *   var latlong = new LatLon(48.8582, 2.2945);
	 *   var utmCoord = latlong.toUtm(); // utmCoord.toString(): '31 N 448252 5411933'
	 */
	public void toUtm() {
	    
	    if (!(-80 <= this.lat && this.lat <= 84 ) ) throw new Error("Outside UTM limits");

	    double falseEasting = 500e3, falseNorthing = 10000e3;

	    double zone = Math.floor( ( this.lon + 180 ) / 6 ) + 1; // longitudinal zone
	    double λ0 = Math.toRadians( ( zone - 1 ) * 6 - 180 + 3 ); // longitude of central meridian

	    // ---- handle Norway/Svalbard exceptions
	    // grid zones are 8° tall; 0°N is offset 10 into latitude bands array
	    String mgrsLatBands = "CDEFGHJKLMNPQRSTUVWXX"; // X is repeated for 80-84°N
	    char latBand = mgrsLatBands.charAt((int) Math.floor(this.lat / 8 + 10));
	    // adjust zone & central meridian for Norway
	    if (zone==31 && latBand=='V' && this.lon>= 3) { zone++; λ0 += Math.toRadians(6); }
	    // adjust zone & central meridian for Svalbard
	    if (zone==32 && latBand=='X' && this.lon<  9) { zone--; λ0 -= Math.toRadians(6); }
	    if (zone==32 && latBand=='X' && this.lon>= 9) { zone++; λ0 += Math.toRadians(6); }
	    if (zone==34 && latBand=='X' && this.lon< 21) { zone--; λ0 -= Math.toRadians(6); }
	    if (zone==34 && latBand=='X' && this.lon>=21) { zone++; λ0 += Math.toRadians(6); }
	    if (zone==36 && latBand=='X' && this.lon< 33) { zone--; λ0 -= Math.toRadians(6); }
	    if (zone==36 && latBand=='X' && this.lon>=33) { zone++; λ0 += Math.toRadians(6); }

	    double φ = Math.toRadians( this.lat );      // latitude ± from equator
	    double λ = Math.toRadians( this.lon ) - λ0; // longitude ± from central meridian

	    double a = this.datum.ellipsoid.a;
	    double f = this.datum.ellipsoid.f;
	    
	    // WGS 84: a = 6378137, b = 6356752.314245, f = 1/298.257223563;

	    double k0 = 0.9996; // UTM scale on the central meridian

	    // ---- easting, northing: Karney 2011 Eq 7-14, 29, 35:

	    double e = Math.sqrt( f * ( 2 - f ) ); // eccentricity
	    double n = f / (2 - f);        // 3rd flattening
	    double n2 = n*n;
	    double n3 = n*n2;
	    double n4 = n*n3;
	    double n5 = n*n4;
	    double n6 = n*n5; // TODO: compare Horner-form accuracy?

	    double cosλ = Math.cos(λ);
	    double sinλ = Math.sin(λ);
	    double tanλ = Math.tan(λ);

	    double τ = Math.tan(φ); // τ ≡ tanφ, τʹ ≡ tanφʹ; prime (ʹ) indicates angles on the conformal sphere
    
	    double σ = Math.sinh( e * atanh( e * τ / Math.sqrt( 1 + τ * τ ) ) );

	    double τʹ = τ * Math.sqrt( 1 + σ * σ ) - σ * Math.sqrt( 1 + τ * τ );

	    double ξʹ = Math.atan2(τʹ, cosλ);
	    double ηʹ = asinh( sinλ / Math.sqrt( τʹ * τʹ + cosλ * cosλ ) );

	    double A = a/(1+n) * (1 + 1/4*n2 + 1/64*n4 + 1/256*n6); // 2πA is the circumference of a meridian

	    Double[] α = { null, // note α is one-based array (6th order Krüger expressions)
	        1/2*n - 2/3*n2 + 5/16*n3 +   41/180*n4 -     127/288*n5 +      7891/37800*n6,
	              13/48*n2 -  3/5*n3 + 557/1440*n4 +     281/630*n5 - 1983433/1935360*n6,
	                       61/240*n3 -  103/140*n4 + 15061/26880*n5 +   167603/181440*n6,
	                               49561/161280*n4 -     179/168*n5 + 6601661/7257600*n6,
	                                                 34729/80640*n5 - 3418889/1995840*n6,
	                                                              212378941/319334400*n6 };

	    double ξ = ξʹ;
	    for (int j=1; j<=6; j++){ 
	    	ξ += α[j] * Math.sin(2*j*ξʹ) * Math.cosh(2*j*ηʹ);
	    }

	    double η = ηʹ;
	    for (int j=1; j<=6; j++){
	    	η += α[j] * Math.cos(2*j*ξʹ) * Math.sinh(2*j*ηʹ);
	    }

	    double x = k0 * A * η;
	    double y = k0 * A * ξ;

	    // ---- convergence: Karney 2011 Eq 23, 24

	    double pʹ = 1;
	    for (int j=1; j<=6; j++) pʹ += 2*j*α[j] * Math.cos(2*j*ξʹ) * Math.cosh(2*j*ηʹ);
	    double qʹ = 0;
	    for (int j=1; j<=6; j++) qʹ += 2*j*α[j] * Math.sin(2*j*ξʹ) * Math.sinh(2*j*ηʹ);

	    double γʹ = Math.atan(τʹ / Math.sqrt(1+τʹ*τʹ)*tanλ);
	    double γʺ = Math.atan2(qʹ, pʹ);

	    double γ = γʹ + γʺ;

	    // ---- scale: Karney 2011 Eq 25

	    double sinφ = Math.sin(φ);
	    double kʹ = Math.sqrt(1 - e*e*sinφ*sinφ) * Math.sqrt(1 + τ*τ) / Math.sqrt(τʹ*τʹ + cosλ*cosλ);
	    double kʺ = A / a * Math.sqrt(pʹ*pʹ + qʹ*qʹ);

	    double k = k0 * kʹ * kʺ;

	    // ------------

	    // shift x/y to false origins
	    x = x + falseEasting;             // make x relative to false easting
	    if (y < 0) y = y + falseNorthing; // make y in southern hemisphere relative to false northing

	    // round to reasonable precision
	    //x = Number(x.toFixed(6)); // nm precision
	    //y = Number(y.toFixed(6)); // nm precision
	    //var convergence = Number(γ.toDegrees().toFixed(9));
	    //var scale = Number(k.toFixed(12));
	    
	    double convergence = Math.toDegrees( γ );
	    double scale = k;

	    String h = this.lat>=0 ? "N" : "S"; // hemisphere

	    System.err.println( zone + " - " + latBand + " - " + h + " - " + x + " - " + y + " - " + this.datum + " - " + convergence + " - " + scale);
	    //return new Utm(zone, h, x, y, this.datum, convergence, scale);
	};
	
	double atanh(double x){
		return 0.5*Math.log( (x + 1.0) / (1.0 - x));
	}
	
	double asinh(double x){
		return Math.log(x + Math.sqrt(x*x + 1.0));
	}

}
