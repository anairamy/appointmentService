package com.vitalis.appointmentService.appointment;

import com.vitalis.appointmentService.appointment.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    List<AppointmentModel> findByDoctorIdAndDate(Long doctor_id, LocalDate date);
    List<AppointmentModel> findByDoctorIdAndUserId(Long doctor_id, Long user_id);
    List<AppointmentModel> findByDoctorId(Long doctor_id);
    List<AppointmentModel> findByUserId(Long userId);
    boolean existsByDoctorIdAndDateAndStartTime(Long doctor_id, LocalDate date, LocalTime start_time);
}
