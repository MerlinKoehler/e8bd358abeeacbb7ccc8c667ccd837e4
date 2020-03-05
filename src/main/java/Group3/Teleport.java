package Group3;

public class Teleport extends StaticObject {

	private double tx;
	private double ty;
	
	public Teleport(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double tx, double ty) {
		this.setX1(x1);
		this.setX1(x2);
		this.setX1(x3);
		this.setX1(x4);

		this.setY1(y1);
		this.setY2(y2);
		this.setY3(y3);
		this.setY4(y4);

		this.setTx(tx);
		this.setTy(ty);
	}

	public void setTx(double tx) {
		this.tx = tx;
	}
	public double getTx() {return tx; }
	public void setTy(double ty) {
		this.ty = ty;
	}
	public double getTy() {
		return ty;
	}
}
