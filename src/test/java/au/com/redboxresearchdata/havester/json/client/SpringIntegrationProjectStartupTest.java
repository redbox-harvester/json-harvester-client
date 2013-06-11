package au.com.redboxresearchdata.havester.json.client;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.redboxresearchdata.havester.json.client.SpringIntegrationUtils;

/**
 * Verify that the Spring Integration Application Context starts successfully.
 *
 * @author Shilo Banihit
 * @since 1.0
 *
 */
public class SpringIntegrationProjectStartupTest {

	@Test
	public void testStartupOfSpringInegrationContext() throws Exception{
		final ApplicationContext context
			= new ClassPathXmlApplicationContext("/META-INF/spring/integration/spring-integration-context.xml",
												  SpringIntegrationProjectStartupTest.class);
		SpringIntegrationUtils.displayDirectories(context);
		Thread.sleep(2000);
	}

}
