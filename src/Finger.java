
public class Finger {
	public int start;
	public int stop;
	public Node node;
	
	public long twoPow(int index) {
		return ((long) 1) << index;
	}

	public Finger(int n, int k) {
		int m = Constants.KEY_BITS;
		
		this.start = (int) ((int) ((n % twoPow(m) + twoPow(k-1)) % twoPow(m)) % twoPow(m));
		this.stop = (int) ((n % twoPow(m) + twoPow(k) % twoPow(m)) % twoPow(m));
	}
}
