package hu.akoel.geo;

public class UTMForm {
	public int zone;
	public char letter;
	public double easting;
	public double northing;

	public UTMForm( int zone, char letter, double easting, double northing ){
		this.zone = zone;
		this.letter = letter;
		this.easting = easting;
		this.northing = northing;
		
	}
	
	public String toString(){
		return new String( zone + " " + letter + " " + easting + "m E " + northing + "m N" );
	}
}
