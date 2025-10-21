package com.vitalis.appointmentService.appointment;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepo) {
        this.appointmentRepository = appointmentRepo;
    }

    public List<AppointmentModel> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<AppointmentModel> getAppointmentsForDoctor(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    public List<AppointmentModel> getAppointmentsForUserWithDoctor(Long doctorId, Long userId) {
        return appointmentRepository.findByDoctorIdAndUserId(doctorId, userId);
    }

    public List<AppointmentModel> getAppointmentsForUser(Long userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public List<AppointmentModel> getAllAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public AppointmentModel createAppointment(AppointmentModel appointment) {
        boolean exists = appointmentRepository.existsByDoctorIdAndDateAndStartTime(
                appointment.getDoctorId(),
                appointment.getDate(),
                appointment.getStartTime()
        );
        if (exists) throw new RuntimeException("Slot already booked");

        appointment.setStatus("CONFIRMED");
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentModel> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    public Optional<AppointmentModel> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public AppointmentModel updateAppointmentStatus(Long id, String status) {
        AppointmentModel appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public AppointmentModel updateAppointment(Long id, AppointmentModel updatedAppointment) {
        return appointmentRepository.findById(id).map(existing -> {
            existing.setDoctorId(updatedAppointment.getDoctorId());
            existing.setUserId(updatedAppointment.getUserId());
            existing.setDate(updatedAppointment.getDate());
            existing.setStartTime(updatedAppointment.getStartTime());
            existing.setEndTime(updatedAppointment.getEndTime());
            existing.setStatus(updatedAppointment.getStatus());
            return appointmentRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public void deleteAllAppointment() {
        appointmentRepository.deleteAll();
    }


}
