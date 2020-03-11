package Group3;

public class Quartet<S1, S2, S3, S4> {

	private S1 content1;
	private S2 content2;
	private S3 content3;
	private S4 content4;
	
	public Quartet (S1 content1, S2 content2, S3 content3, S4 content4) {
		this.content1 = content1;
		this.content2 = content2;
		this.content3 = content3;
		this.content4 = content4;
	}
	
	
	public S1 getContent1() {
		return content1;
	}
	public void setContent1(S1 content1) {
		this.content1 = content1;
	}
	
	public S2 getContent2() {
		return content2;
	}
	public void setContent2(S2 content2) {
		this.content2 = content2;
	}
	
	public S3 getContent3() {
		return content3;
	}
	public void setContent3(S3 content3) {
		this.content3 = content3;
	}
	
	public S4 getContent4() {
		return content4;
	}

	public void setContent4(S4 content4) {
		this.content4 = content4;
	}
}
