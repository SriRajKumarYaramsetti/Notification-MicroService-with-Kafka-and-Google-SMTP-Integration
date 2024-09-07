package com.sriraj.Notification.Service.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sriraj.Notification.Service.dtos.sendEmailMessageDto;
import com.sriraj.Notification.Service.utilities.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailConsumer {
    private ObjectMapper objectMapper;
    private EmailUtil emailUtil;


    @Autowired

    public SendEmailConsumer(ObjectMapper objectMapper,
                             EmailUtil emailUtil){
        this.objectMapper=objectMapper;
        this.emailUtil=emailUtil;
    }

    @KafkaListener(topics="sendEmail",groupId = "emailService")
    public void handleSendEmail(String message){

        try {
            sendEmailMessageDto emailMessage = objectMapper.readValue(message, sendEmailMessageDto.class);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("srirajkumaryaramsetti10@gmail.com", "put your mail password");
                }
            };
            Session session = Session.getInstance(props, auth);
            emailUtil.sendEmail(session,emailMessage.getTo(),emailMessage.getSubject(),emailMessage.getBody());
        }
        catch (Exception e){
            System.out.println("Something went wrong");
        }


    }

}
