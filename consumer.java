package si.iskratel.consumer;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class consumer {
    // URL of the JMS server
    private static String url = "tcp://IP:61616";

    // Name of the topic from which we will receive messages from = " testt"

    public static void main(String[] args) throws JMSException {
        // Getting JMS connection from the server

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();

        // need to setClientID value, any string value you wish
        connection.setClientID("12345");

        try{
        connection.start();
        }catch(Exception e){
            System.err.println("NOT CONNECTED!!!");
        }
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic("testt");

        //need to use createDurableSubscriber() method instead of createConsumer() for topic
        // MessageConsumer consumer = session.createConsumer(topic);
        MessageConsumer consumer = session.createDurableSubscriber(topic,
                "SUB1234");

        MessageListener listner = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message"
                                + textMessage.getText() + "'");
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
            }
        };

        consumer.setMessageListener(listner);
        //connection.close();

    }
}
