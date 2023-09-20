package net.etfbl.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class DistributorObject implements DistributorInterface {
	private ArrayList<String> products;
	
	public DistributorObject(ArrayList<String> products) throws RemoteException {
		this.products = products;
	}
	
	@Override
	public ArrayList<String> getProducts() throws RemoteException {
		return products;
	}

	@Override
	public boolean purchase(String product, int quantity) throws RemoteException {
        return products.contains(product);
	}
}
