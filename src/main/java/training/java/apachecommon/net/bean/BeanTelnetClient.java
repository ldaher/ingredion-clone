package training.java.apachecommon.net.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

public class BeanTelnetClient {
	private TelnetClient client;
	private InputStream in;
	private PrintStream out;
	private char prompt = '>';

	public void pesar() {
		try {
			while (true) {
				this.client = new TelnetClient();
				connect();
				double result = readData();
				System.out.printf("Resultado da pesagem: %.2f\n", result);
				disconnect();
			}

		} catch (Exception ex) {
			System.err.printf("error pesando\n%s", ex);
			throw new RuntimeException(ex);
		}
	}

	public void connect() {
	
		BufferedReader br = null;
	
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
	
			System.out.print("Endereço IP: ");
			String server = br.readLine();
	
			if (server.equals("quit") || server.equals("exit")) {
				System.exit(0);
			}
	
			System.out.print("Porta: ");
			String porta = br.readLine();
	
			String usuario = "user admin";
	
			this.client.connect(server, Integer.parseInt(porta));
			System.out.println("Connectado!");
	
			this.in = this.client.getInputStream();
			this.out = new PrintStream(this.client.getOutputStream());
	
			System.out.println("Aguardando usuário...");
	
			readUntil("Ready for user");
			write(usuario);
	
			System.out.println("esperando ok");
	
			readUntil("Access OK");
		} catch (IOException ex) {
			ex.printStackTrace();
		} /*finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}

	@SuppressWarnings("unused")
	private String readUntil(String pattern) {
		try {
			char charAt = pattern.charAt(pattern.length() - 1);
			StringBuilder sb = new StringBuilder();
			boolean found = false;
			char ch = (char) this.in.read();
	
			System.out.print("Imprimindo letra por letra: " + ch);
			
			while (true) {
				sb.append(ch);
				if (ch == charAt && sb.toString().endsWith(pattern)) {
					System.err.println(ch + "\nLido: " + sb.toString());
					return sb.toString();
				}
				
				System.out.print(ch);
				
				ch = (char) this.in.read();
			}
		} catch (Exception ex) {
			System.err.printf("error reading from telnet %s\n%s", pattern, ex);
			throw new RuntimeException(ex);
		}
	}

	private void write(String value) {
		System.out.println("Escrevendo: " + value);
		this.out.println(value);
		this.out.flush();
	}

	private double readData() {
		String paramCantidad = "wt0101";
		String paramUnidad = "wt0103";
		String cmd = "read " + paramCantidad + " " + paramUnidad;
		write(cmd);

		String result = readUntil("~kg");
		readUntil(this.prompt + "");
		String[] resultado = result.split("~");
		if (resultado.length != 3) {
			System.err.println("error reading from telnet " + cmd + " result " + result);
			throw new RuntimeException("error reading from telnet " + cmd + " result " + result);
		}

		return Double.parseDouble(resultado[1].trim());
	}

	private void disconnect() {
		try {
			this.client.disconnect();
		} catch (Exception ex) {
			System.err.println("error disconnecting to telnet ");
			ex.printStackTrace();
		}
	}
}
