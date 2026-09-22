package com.example.AgenciaViajes.servicios.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailServicio {

    @Autowired
    private JavaMailSender mailEnviar; 

    @Autowired
    private SpringTemplateEngine templateMotor; 

    @Value("${spring.mail.username}")
    private String correoRemitente; 

    @Async
    public void enviarCorreoReserva(String destino, String asunto, String nombrePlantilla, 
                                    Map<String, Object> variablesReserva, 
                                    byte[] documentoAdjunto, String nombreAdjunto) {
        try {
            Context context = new Context();
            if (variablesReserva != null) {
                context.setVariables(variablesReserva);
            }
            String html = templateMotor.process(nombrePlantilla, context); 

            MimeMessage message = mailEnviar.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); 

            helper.setTo(destino);
            helper.setSubject(asunto);
            helper.setText(html, true);
            helper.setFrom(correoRemitente); 

            if (documentoAdjunto != null && documentoAdjunto.length > 0) {
                helper.addAttachment(nombreAdjunto, new ByteArrayResource(documentoAdjunto));
            }

            mailEnviar.send(message);
            System.out.println("Correo de reserva enviado a: " + destino);

        } catch (Exception e) {
            System.err.println("Fallo al enviar correo de reserva a " + destino + ": " + e.getMessage());
        }
    }
}