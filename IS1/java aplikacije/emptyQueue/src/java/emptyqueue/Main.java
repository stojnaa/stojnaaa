/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emptyqueue;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 *
 * @author Lenovo
 */
public class Main {
         @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup = "jms/podsistem1Queue")
    private static Queue podsistem1Queue;
    
    @Resource(lookup = "jms/centralReplyPS1")
    private static Queue centralReplyPS1;
    @Resource(lookup = "jms/podsistem2Queue")
    private static Queue podsistem2Queue;
    
    @Resource(lookup = "jms/centralReplyPS2")
    private static Queue centralReplyPS2;
    @Resource(lookup = "jms/podsistem3Queue")
    private static Queue podsistem3Queue;
    
    @Resource(lookup = "jms/centralReplyPS3")
    private static Queue centralReplyPS3;
    
    
    
    private static void isprazniRed(Queue queue) {
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(queue);
        
        while(consumer.receiveNoWait() != null);
        for(int i = 0; i< 3; i++) {
            System.out.println("i =" + i);
            consumer.receive(1000);
            System.out.println("i =" + i);
        }
        
    }
    
    public static void main(String[] args) {
        isprazniRed(podsistem1Queue);
        isprazniRed(centralReplyPS1);
        isprazniRed(podsistem2Queue);
        isprazniRed(centralReplyPS2);
        isprazniRed(podsistem3Queue);
        isprazniRed(centralReplyPS3);
    }
    
    
    
}
