package hu.akoel.geo;

import hu.akoel.geo.Ellipsoids;

public enum Datums{
	
	/* eslint key-spacing: 0, comma-dangle: 0 */
	WGS84(Ellipsoids.WGS84, new Transformation( new Translation(0.0, 0.0, 0.0), new Translation(0.0, 0.0, 0.0), new Rotation( 0.0 ) ) ),

	// (2009); functionally ≡ WGS84 - www.uvm.edu/giv/resources/WGS84_NAD83.pdf
	// note: if you *really* need to convert WGS84<->NAD83, you need more knowledge than this!
	NAD83(Ellipsoids.GRS80, new Transformation( new Translation(1.004, -1.910, -0.515), new Translation(0.0267, 0.00034, 0.011), new Rotation( -0.0015 ) ) ),
	
	// www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
	OSGB36(Ellipsoids.Airy1830, new Transformation( new Translation(-446.448, 125.157, -542.060), new Translation(-0.1502, -0.2470, -0.8421), new Rotation( 20.4894 ) ) ),
	
	// og.decc.gov.uk/en/olgs/cms/pons_and_cop/pons/pon4/pon4.aspx
	OED50(Ellipsoids.Intl1924, new Transformation( new Translation(89.5, 93.8,  123.1), new Translation(0.0,  0.0,  0.156), new Rotation( -1.2 ) ) ),

	// osi.ie/OSI/media/OSI/Content/Publications/transformations_booklet.pdf
	// TODO: many sources have opposite sign to rotations - to be checked!
	Irl1975(Ellipsoids.AiryModified, new Transformation( new Translation(-482.530,  130.596,  -564.557), new Translation(-1.042,  -0.214,  -0.631), new Rotation( -8.150) ) ),
	
	// www.geocachingtoolbox.com?page=datumEllipsoidDetails
	TokyoJapan(Ellipsoids.Bessel1841, new Transformation( new Translation(148, -507, -685), new Translation(0, 0, 0), new Rotation( 0 ) ) ),
	
	// en.wikipedia.org/wiki/Helmert_transformation
	NAD27(Ellipsoids.Clarke1866, new Transformation( new Translation(8, -160, -176), new Translation(0, 0, 0), new Rotation( 0 ) ) ),
	
	// www.icao.int/safety/pbn/documentation/eurocontrol/eurocontrol wgs 84 implementation manual.pdf
	WGS72(Ellipsoids.WGS72, new Transformation( new Translation(0, 0, -4.5), new Translation(0, 0, 0.554), new Rotation( -0.22 ) ) );

	public Ellipsoids ellipsoid;
	public Transformation transformation;
	
	private Datums( Ellipsoids ellipsoid, Transformation transformation ){
		this.ellipsoid = ellipsoid;
		this.transformation = transformation;
	}
}	

