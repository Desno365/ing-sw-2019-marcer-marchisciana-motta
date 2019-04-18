package it.polimi.se2019.network.server.rmi;

import it.polimi.se2019.network.ConnectionInterface;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.server.ServerMessageHandler;
import it.polimi.se2019.utils.Utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implements the RMI server
 * @author DEsno365
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerSkeletonInterface {

	private transient ServerMessageHandler serverMessageHandler;

	/**
	 * Create a new instance of a RMIServer and start it.
	 * @param serverMessageHandler the MessageHandler to which all messages are sent
	 * @throws RemoteException
	 */
	public RMIServer(ServerMessageHandler serverMessageHandler) throws RemoteException {
		super();
		this.serverMessageHandler = serverMessageHandler;
		startRMIServer();
	}

	/**
	 * Register a new client that is connected to the server. This method is called remotely by the client.
	 * @param client the RMI implementation of the client.
	 * @throws RemoteException
	 */
	@Override
	public void registerClient(ConnectionInterface client) throws RemoteException {
		serverMessageHandler.onClientRegistration(client);
	}

	/**
	 * Receives a message from the client and handles it. This method is called remotely by the client.
	 * @param message the message received from the client.
	 * @throws RemoteException
	 */
	@Override
	public void receiveMessage(ConnectionInterface client, Message message) throws RemoteException {
		new Thread(() -> {
			serverMessageHandler.onMessageReceived(client, message);
		}, "CUSTOM: RMI Message Reception").start();
	}

	/**
	 * Start the RMI server and register it on the RMI registry,
	 * @throws RemoteException
	 */
	private void startRMIServer() throws RemoteException {
		// Register server.
		System.setProperty("java.rmi.server.hostname", "localhost");
		Registry registry = LocateRegistry.createRegistry(1099);
		registry.rebind("Server", this);

		Utils.logInfo("RMI server is ready.");
	}
}
