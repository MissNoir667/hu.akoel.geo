package hu.akoel.geo;

public class Transformation{
	public Translation tTranslation, rTranslation;
	public Rotation rotation;
	public Transformation( Translation tTranslation, Translation rTranslation, Rotation rotation ){
		this.tTranslation = tTranslation;
		this.rTranslation = rTranslation;
		this.rotation = rotation;
	}
	
	public Transformation getInverseClone(){
		
		return new Transformation( 
				tTranslation.getInverseClone(),
				rTranslation.getInverseClone(),
				rotation.getInverseClone() );
		
	}
}
