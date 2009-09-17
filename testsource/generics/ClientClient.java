package test;

public class ClientClient {
	
	public List<Client<String>> clients() {
		return null;
	}
	
	public Client someClient() {
		return clients().get(0).someX();
	}
	
}