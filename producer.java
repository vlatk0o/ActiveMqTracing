package com.example.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import brave.Tracing;
import brave.jms.JmsTracing;
import brave.sampler.Sampler;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

public class producer {

	private static String url = "tcp://IP:61616";
	public static JmsTracing jmsTracing;
	public static Tracing tracing;

	public static void main(String[] args) throws JMSException {

		
		URLConnectionSender sender = URLConnectionSender.create("http://IP:9411/api/v1/spans");
		AsyncReporter asyncReport = AsyncReporter.builder(sender).build();
		tracing = Tracing.newBuilder().localServiceName("producer").reporter(asyncReport)
		    .sampler(Sampler.ALWAYS_SAMPLE).build();
		jmsTracing = JmsTracing.newBuilder(tracing)
				.remoteServiceName("my-broker")
				.build();
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		ConnectionFactory tracingConnectionFactory = jmsTracing.connectionFactory(connectionFactory);
		Connection connection = tracingConnectionFactory.createConnection();
		connection.start();

		// JMS messages are sent and received using a Session. We will
		// create here a non-transactional session object. If you want
		// to use transactions you should set the first parameter to 'true'
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic topic = session.createTopic("testt");
		

		MessageProducer producer = session.createProducer(topic);

		// We will send a small text message saying 'Hello'

		TextMessage message = session.createTextMessage();

		// Here we are sending the message!
		for (int i = 0; i <= 1000; i++) {
			message.setText("HELLO JMS WORLD"+i);
			producer.send(message);
			System.out.println("Sent message '" + message.getText() + "'");
		}
		asyncReport.flush();
		connection.close();
		connection.close();
	}
	

}
