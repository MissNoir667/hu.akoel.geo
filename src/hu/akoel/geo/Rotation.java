package hu.akoel.geo;

public class Rotation implements Cloneable{
	public double s;
	public Rotation( double s ){
		this.s = s;
	}
	
	public Rotation getInverseClone(){
		return new Rotation( -s );
	}
}
