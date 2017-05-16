package hu.akoel.geo;

public class Translation{
	public double x, y, z;
	public Translation( double x, double y, double z ){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Translation getInverseClone(){
		return new Translation( -x, -y, -z);
	}
}
