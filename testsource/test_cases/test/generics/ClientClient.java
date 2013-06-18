package test.generics;

public class ClientClient {
	
	public List<Client<String>> clients() {
		return null;
	}
	
	public String someClient() {
		return clients().get(0).someX();
	}
	
}