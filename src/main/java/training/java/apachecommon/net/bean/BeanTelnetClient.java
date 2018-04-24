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
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String server = "";
			while (true) {
				System.out.print("Endereço IP: ");
				server = this.readLine(br);

				if (!server.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
					System.err.println(
							"Formato de \"Endereço IP\" incorreto (ex.: xxx.xxx.xxx.xxx (x = número inteiro)...");
				} else {
					break;
				}
			}

			String porta = "";

			while (true) {
				System.out.print("Porta: ");
				porta = this.readLine(br);

				if (!porta.matches("\\d+")) {
					System.err.println("Valor do item \"Porta\" necessita ser numérico...");
				} else {
					break;
				}
			}

			System.out.print("Usúario (senha): ");
			String usuario = this.readLine(br);

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
		}
	}

	private String readLine(BufferedReader br) throws IOException {
		String line = br.readLine();

		if (line.matches("(quit|exit)")) {
			System.out.println("Finalizando...");
			System.exit(0);
		}

		return line;
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
