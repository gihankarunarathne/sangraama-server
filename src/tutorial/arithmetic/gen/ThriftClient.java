package tutorial.arithmetic.gen;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftClient {
	 private void invoke() {
		 TTransport transport;
		 transport = new TSocket("localhost", 7911);
		 TProtocol protocol = new TBinaryProtocol(transport);
		 ArithmeticService.Client client = new ArithmeticService.Client(protocol);
		 try {
			transport.open();
			long addResult = client.add(100, 200);
			System.out.println("Add result: " + addResult);
			long multiplyResult = client.multiply(10, 40);
			System.out.println("Multiply result: " + multiplyResult);
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		 
		 
	 }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThriftClient c = new ThriftClient();
		 c.invoke();
	}
}
