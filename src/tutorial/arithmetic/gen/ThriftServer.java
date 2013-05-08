package tutorial.arithmetic.gen;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftServer {
	public void start() {
		try {
			TServerSocket serverTransport = new TServerSocket(7911);
			ArithmeticService.Processor processor = new ArithmeticService.Processor(new ArithmeticServiceImpl());
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
			System.out.println("Starting server on port 7911 ...");
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		
	}
}
