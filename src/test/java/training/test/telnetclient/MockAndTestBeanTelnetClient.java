package training.test.telnetclient;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import training.java.apachecommon.net.bean.BeanTelnetClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BeanTelnetClient.class)
public class MockAndTestBeanTelnetClient {

	@Mock
	private BeanTelnetClient mock;

	@Mock
	private TelnetClient mockTelnet;

	private static final String READDATA = "readData";
	private static final String DISCONNETC = "disconnect";
	private static final String READULTIL = "readUntil";
	private static final String WRITE = "write";

	@Test
	public void testPesar() throws Exception {
		BeanTelnetClient spy = PowerMockito.spy(mock);

		PowerMockito.doNothing().when(mockTelnet).connect("192.168.0.1", 22);

		PowerMockito.doReturn("").when(spy, READULTIL, "");
		PowerMockito.doNothing().when(spy, WRITE, "");

		PowerMockito.doReturn(143.3).when(spy, READDATA);

		// método disconnect() mockado
		PowerMockito.doAnswer(new Answer<Object>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				System.exit(1);
				return null;
			}
		}).when(spy, DISCONNETC);

		spy.pesar();
	}

}
