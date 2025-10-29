package com.vitalis.appointmentService;

import com.vitalis.appointmentService.appointment.AppointmentModel;
import com.vitalis.appointmentService.appointment.AppointmentRepository;
import com.vitalis.appointmentService.appointment.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class Controller {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public Controller(AppointmentService appointmentService,
                      AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping()
    public List<AppointmentModel> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/doctors/{doctorId}")
    public List<AppointmentModel> getAppointmentsForDoctor(@PathVariable Long doctorId, @RequestParam(required = false) String date) {
        if (date != null) {
            LocalDate parsedDate = LocalDate.parse(date);
            return appointmentService.getAppointmentsForDoctor(doctorId, parsedDate);
        } else {
            return appointmentService.getAllAppointmentsForDoctor(doctorId);
        }
    }

    @GetMapping("/allAppointments")
    public List<AppointmentModel> getSlotsByDoctorAndDate(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    @GetMapping("/date")
    public List<AppointmentModel> getAppointmentsByDoctorAndDate(@RequestParam Long doctorId, @RequestParam String date) {
        LocalDate dateSlot = LocalDate.parse(date);
        return appointmentService.getAppointmentsByDoctorAndDate(doctorId, dateSlot);
    }

    @GetMapping("/doctors/{doctorId}/users/{userId}")
    public List<AppointmentModel> getAppointmentsForUserWithDoctor(@PathVariable Long doctorId, @PathVariable Long userId) {
        return appointmentService.getAppointmentsForUserWithDoctor(doctorId, userId);
    }

    @PostMapping("/createAppointment")
    public AppointmentModel createAppointment(@RequestBody AppointmentModel appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AppointmentModel>> getAppointmentsForUser(@PathVariable Long userId) {
        List<AppointmentModel> appointments = appointmentRepository.findByUserId(userId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentModel> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentModel updatedAppointment) {
        try {
            AppointmentModel saved = appointmentService.updateAppointment(id, updatedAppointment);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public AppointmentModel updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        AppointmentModel updated = appointmentService.updateAppointmentStatus(id, status);

        if ("CANCELED".equalsIgnoreCase(status)) {
            try {
                String refundUrl = "http://payment-service:8088/payment/refund/appointment/" + id;
                restTemplate.postForObject(refundUrl, null, Void.class);
                System.out.println("Refund triggered automatically for appointment " + id);
            } catch (Exception e) {
                System.err.println("Failed to trigger refund for appointment " + id + ": " + e.getMessage());
            }
        }

        return updated;
    }

    @GetMapping("/{id}")
    public Optional<AppointmentModel> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }

    @DeleteMapping("/delete")
    public void deleteAllAppointment() {
        appointmentService.deleteAllAppointment();
    }


}
