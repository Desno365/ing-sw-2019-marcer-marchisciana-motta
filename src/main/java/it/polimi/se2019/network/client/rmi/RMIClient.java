package it.polimi.se2019.network.client.rmi;

import it.polimi.se2019.network.client.ConnectionToServerInterface;
import it.polimi.se2019.network.client.MessageReceiverInterface;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.server.rmi.RMIServerSkeletonInterface;
import it.polimi.se2019.utils.ServerConfigParser;
import it.polimi.se2019.utils.Utils;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class is the bridge between the server and the client.
 * The server calls methods of this class in order to communicate with the client.
 * And the client does the same in order to communicate with the server.
 * @author Desno365
 */
public class RMIClient implements ConnectionToServerInterface, RMIClientInterface {

	private RMIServerSkeletonInterface rmiServerSkeleton;
	private MessageReceiverInterface messageReceiver;
	private RMIClientInterface stub;
	private boolean active;


	/**
	 * Creates a new instance of a RMIClient and starts the connection with the server.
	 * @param messageReceiver the interface on which messages will be forwarded.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public RMIClient(MessageReceiverInterface messageReceiver) throws RemoteException, NotBoundException {
		this.messageReceiver = messageReceiver;

		// Get Server remote object.
		Registry registry = LocateRegistry.getRegistry(ServerConfigParser.getHost(), ServerConfigParser.getRmiPort());
		rmiServerSkeleton = (RMIServerSkeletonInterface) registry.lookup("Server");

		// Create stub from client.
		stub = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);

		// Register client's stub to the server.
		rmiServerSkeleton.registerClient(stub);

		// Starts listener for connection interruption.
		startConnectionListener();

		Utils.logInfo("Client remote object is ready.");
		active = true;
	}


	/**
	 * Sends a message to the server.
	 * @param message the message to send.
	 */
	@Override // Of ConnectionToServerInterface.
	public void sendMessage(Message message) {
		try {
			rmiServerSkeleton.receiveMessage(stub, message);
		} catch (RemoteException e) {
			Utils.logError("Error in RMIClient: sendMessage()", e);
		}
	}

	/**
	 * Returns true if and only if the connection is active.
	 * @return true if and only if the connection is active.
	 */
	@Override // Of ConnectionToServerInterface.
	public boolean isConnectionActive() {
		return active;
	}

	/**
	 * Closes the connection with the server.
	 */
	@Override // Of ConnectionToServerInterface.
	public void closeConnection() {
		active = false;
		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			Utils.logError("Error in RMIClient: closeConnection()", e);
		}
	}

	/**
	 * Called by the RMI server in order to send a message.
	 * @param message the message sent by the server.
	 * @throws RemoteException
	 */
	@Override // Of RMIClientInterface.
	public void receiveMessage(Message message) throws RemoteException {
		messageReceiver.processMessage(message);
	}

	/**
	 * Called by the RMI server to check for a connection lost.
	 * When this method interrupts the server knows the connection has been lost.
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	@Override // Of RMIClientInterface.
	public synchronized void connectionListenerSubjectInClient() throws RemoteException, InterruptedException {
		while(isConnectionActive())
			wait();
	}


	/**
	 * Starts the thread that listens for a connection lost with the server.
	 * If a lost of connection is found reports it to the message receiver.
	 */
	private void startConnectionListener() {
		new Thread(() -> {
			try {
				rmiServerSkeleton.connectionListenerSubjectInServer();
			} catch (Exception e) {
				Utils.logError("Connection closed by the server.", e);
			} finally {
				closeConnection();
				messageReceiver.lostConnection();
			}
		}, "CUSTOM: RMI Connection Listener").start();
	}
}
