package com.example.notification_service.service;

import com.example.notification_service.dto.User;
import com.example.notification_service.entity.Notifications;
import com.example.notification_service.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.example.notification_service.entity.Notifications;
import com.example.notification_service.dto.AppointmentMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;  // To fetch doctor’s email from the User Microservice

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;  // To send emails

    @Autowired
    private ObjectMapper objectMapper; // To deserialize the incoming message

    @RabbitListener(queues = "appointmentQueue")
    public void listen(String message) {
        try {
            // Deserialize the incoming JSON message
            AppointmentMessage appointmentMessage = objectMapper.readValue(message, AppointmentMessage.class);
            System.out.println(message);
            Long doctorId = appointmentMessage.getDoctorId();
            String doctorEmail = null;
            try {
                String url = "http://user-management-service/api/admin/id/" + doctorId;
                ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, null, User.class);
                doctorEmail = response.getBody().getEmail();
                //System.out.println(response.getBody().getEmail());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to fetch doctor email for ID: " + doctorId);
                return; // Exit the method to prevent further processing
            }
            // Handle the notification based on the action type

            switch (appointmentMessage.getStatus()) {
                case BOOKED:
                    System.out.println("You have a new booked appointment");
                    sendCreateNotification(doctorEmail, appointmentMessage);
                    break;
                case RESCHEDULED:
                    System.out.println("You have a Rescheduled appointment");
                    sendUpdateNotification(doctorEmail, appointmentMessage);
                    break;
                case CANCELLED:
                    System.out.println("You have a Cancelled appointment");
                    sendDeleteNotification(doctorEmail, appointmentMessage);
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendCreateNotification(String doctorEmail, AppointmentMessage appointmentMessage) {
        String message = "You have a new appointment with patient ID: " + appointmentMessage.getPatientId() + " on " + appointmentMessage.getAppointmentDate();
        sendEmailToDoctor(doctorEmail, message);

        // Save notification in the database
        Notifications notification = new Notifications();
        notification.setUserId(appointmentMessage.getDoctorId());
        notification.setMessage(message);
        notificationRepository.save(notification);

    }

    private void sendUpdateNotification(String doctorEmail, AppointmentMessage appointmentMessage) {
        String message = "Your appointment with patient ID: " + appointmentMessage.getPatientId() + " has been updated to " + appointmentMessage.getAppointmentDate();
        sendEmailToDoctor(doctorEmail, message);

        // Save notification in the database
        Notifications notification = new Notifications();
        notification.setUserId(appointmentMessage.getDoctorId());
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    private void sendDeleteNotification(String doctorEmail, AppointmentMessage appointmentMessage) {
        String message = "Your appointment with patient ID: " + appointmentMessage.getPatientId()+" on " + appointmentMessage.getAppointmentDate() + " has been deleted.";
        sendEmailToDoctor(doctorEmail, message);

        // Save notification in the database
        Notifications notification = new Notifications();
        notification.setUserId(appointmentMessage.getDoctorId());
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    private void sendEmailToDoctor(String doctorEmail, String message) {
        // Send email using JavaMailSender or other email services
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(doctorEmail);
        simpleMessage.setSubject("Appointment Notification");
        simpleMessage.setText(message);
        mailSender.send(simpleMessage);
    }
}
