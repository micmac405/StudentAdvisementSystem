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
    
    public void sendEmail(Session session, String email, String message) throws Exception {
        InitialContext ctx = new InitialContext();
        session = (Session) ctx.lookup("mail/WSP");
        session.setDebug(true);
        Message msg = new MimeMessage(session);
        msg.setSubject("Validate your Email for UCO Advisement!");
        msg.setRecipient(MimeMessage.RecipientType.TO, 
                new InternetAddress(email, "wsptermproject@gmail.com"));
        msg.setFrom(new InternetAddress("wsptermproject@gmail.com", 
                "Team Free Labor"));
        msg.setText(message);
        
        System.out.print("Inside email class");
        Transport.send(msg);
    }
}
