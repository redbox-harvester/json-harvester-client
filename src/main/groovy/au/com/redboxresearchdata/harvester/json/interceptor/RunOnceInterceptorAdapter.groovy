package au.com.redboxresearchdata.harvester.json.interceptor

import java.util.concurrent.atomic.AtomicInteger
import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.MessageChannel
import org.springframework.integration.channel.interceptor.ChannelInterceptorAdapter
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware
import org.springframework.context.support.AbstractApplicationContext

/**
 * Interceptor class to ensure only one processing cycle gets through.
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
class RunOnceInterceptorAdapter extends ChannelInterceptorAdapter implements ApplicationContextAware  {
	static final Logger log = Logger.getLogger(ChannelInterceptorAdapter.class)	 
	AbstractApplicationContext applicationContext
	 
	final AtomicInteger sendCount = new AtomicInteger()
	 
	void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		def curCount = sendCount.decrementAndGet()
		if (curCount == 0) {
			log.info("All messages sent, shutting down...")
			applicationContext.close()
		}
	 }	 

	 Message<?> preSend(Message<?> message, MessageChannel channel) {
		sendCount.incrementAndGet()
		return message
	 }
	 
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
			applicationContext = (AbstractApplicationContext) arg0
	}
}
