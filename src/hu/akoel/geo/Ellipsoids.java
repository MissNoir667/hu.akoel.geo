package hu.akoel.geo;

public enum Ellipsoids{
	
	WGS84( 6378137, 6356752.314245, 1/298.257223563 ),
	GRS80(6378137, 6356752.314140, 1/298.257222101 ),
	Airy1830( 6377563.396, 6356256.909, 1/299.3249646 ),
	AiryModified( 6377340.189, 6356034.448, 1/299.3249646 ),
	Bessel1841( 6377397.155, 6356078.962818, 1/299.1528128 ),
	Clarke1866( 6378206.4, 6356583.8, 1/294.978698214 ),
	Intl1924( 6378388, 6356911.946, 1/297 ), // aka Hayford
	WGS72( 6378135, 6356750.5, 1/298.26 );
	
	public double a, b, f;
	private Ellipsoids( double a, double b, double f){
		this.a = a;
		this.b = b;
		this.f = f;
	}
	
	
}
