
public class Finger {
	public int start;
	public int stop;
	public Node node;
	
	public Finger(int n, int k) {
		int m = Constants.KEY_BITS;
		this.start = (n + (1 << (k-1))) % m;
		this.stop = (n + (1 << k)) % m;
	}
}
