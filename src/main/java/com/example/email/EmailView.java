package com.example.email;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.vaadin.flow.component.icon.VaadinIcon;

@PageTitle("Email")
@Route("email")


public class EmailView extends VerticalLayout {

    public EmailView(JavaMailSender mailSender) {
        EmailField to = new EmailField("Para");
        TextField subject = new TextField("Assunto");
        TextArea body = new TextArea("Mensagem");
        Button send = new Button("Enviar", e -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to.getValue());
                message.setSubject(subject.getValue());
                message.setText(body.getValue());
                mailSender.send(message);
                Notification.show("Email enviado com sucesso!");
            } catch (Exception ex) {
                Notification.show("Erro ao enviar: " + ex.getMessage());
            }
        });

        // Layout básico
        to.setWidthFull();
        subject.setWidthFull();
        body.setWidthFull();
        body.setHeight("250px");

        add(to, subject, body, send);
        setMaxWidth("700px");
        setAlignItems(Alignment.START);
    }
}
