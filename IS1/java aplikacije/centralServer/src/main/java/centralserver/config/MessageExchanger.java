/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centralserver.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 * @author Petar
 */
public class MessageExchanger {

    public static String razmeniPoruke(JMSContext context, Queue queue1, ObjectMessage objMsg, Queue queue1nazad) {
        sendMessage(context, queue1, objMsg);
        return receiveMessage(context, queue1nazad);
    }

    private static void sendMessage(JMSContext context, Queue queue1, ObjectMessage objMsg) {
        JMSProducer producer = context.createProducer();
        producer.send(queue1, objMsg);
        System.out.println("Message sent");
    }

    private static String receiveMessage(JMSContext context, Queue queue1nazad) {
        try (JMSConsumer consumer = context.createConsumer(queue1nazad)) {

            System.out.println("Waiting to receive a reply");
            Message msg = consumer.receive(10000);
            if (msg == null) {
                return "TIMEOUT: no reply received.";
            }

            System.out.println("Reply received, type==" + msg.getClass().getName());

            if (msg instanceof TextMessage) {
                return ((TextMessage) msg).getText();
            }
            if (msg instanceof ObjectMessage) {
                Object o = ((ObjectMessage) msg).getObject();
                return "OBJECT_REPLY: " + String.valueOf(o);
            }
            return "ERROR: reply is neither TextMessage nor ObjectMessage.";

        } catch (Exception ex) {
            Logger.getLogger(MessageExchanger.class.getName()).log(Level.SEVERE, null, ex);
            return "Error in MessageExchanger.receiveMessage.";
        }
    }

}
