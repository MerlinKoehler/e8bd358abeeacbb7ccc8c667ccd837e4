package Group3;

public class MapSize extends StaticObject{
	
	private int height;
	private int getHeight() {
		return height;
	}

	private int width;
	private int getWidth() {
		return width;
	}
	
	public MapSize(int height, int width) {
		this.height = height;
		this.width = width;
	}
}
