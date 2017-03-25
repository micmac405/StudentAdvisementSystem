/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.teamfreelabor;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 *
 * @author alebel
 */
public class Email {
    
    public void sendEmail(Session session) throws Exception {
        InitialContext ctx = new InitialContext();
        session = (Session) ctx.lookup("mail/WSP");
        session.setDebug(true);
        Message msg = new MimeMessage(session);
        msg.setSubject("Validate your Email for UCO Advisement!");
        msg.setRecipient(MimeMessage.RecipientType.TO, 
                new InternetAddress("wsptermproject@gmail.com", "wsptermproject@gmail.com"));
        msg.setFrom(new InternetAddress("wsptermproject@gmail.com", 
                "Team Free Labor"));
        msg.setText("HEre is the code!");
        System.out.print("Inside email class");
        //Body Text.
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Here is the code: " + "1234" + 
                "\nEnter this back on the advisement site.");
        
        //send email
        Transport.send(msg);
    }
}
