package training.java.apachecommon.net;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import training.java.apachecommon.net.bean.BeanTelnetClient;

public class TelnetClientApp {

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("context-spring.xml");

		BeanTelnetClient bean = (BeanTelnetClient) context.getBean("beanTelnetClient");

		System.out.println(
				"Para finalizar a aplicação basta digitar \"exit\" ou \"quit\" em qualquer prompt...");

		bean.pesar();
	}

}
