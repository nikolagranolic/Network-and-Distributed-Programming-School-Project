package net.etfbl.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DistributorInterface extends Remote {
	ArrayList<String> getProducts() throws RemoteException;
	boolean purchase(String product, int quantity) throws RemoteException;
}
