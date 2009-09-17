package test;

public class Client<X> {
	
	public List<Client> clients() {
		return null;
	}
	
	public Client someClient() {
		return clients().get(0);
	}
	
	public List<X> xs() {
		return null;
	}
	
	public X someX() {
		return xs().get(0);
	}
	
	public SubList<java.util.List> subs() {
		return null;
	}
	
	public String someSub() {
		return subs().get(0);
	}
	
}