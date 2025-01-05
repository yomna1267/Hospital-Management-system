package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.UserNotFoundException;
import com.example.userManagementService.exceptions.PatientNotFoundException;
import com.example.userManagementService.exceptions.DoctorNotFoundException;
import com.example.userManagementService.models.Doctor;
import com.example.userManagementService.models.Patient;
import com.example.userManagementService.models.Role;
import com.example.userManagementService.exceptions.userNotFoundException;
import com.example.userManagementService.models.Users;
import com.example.userManagementService.repository.DoctorRepository;
import com.example.userManagementService.repository.PatientRepository;
import com.example.userManagementService.repository.UserRepository;
import com.example.userManagementService.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WorkingHoursService workingHoursService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(DoctorRepository doctorRepository, PatientRepository patientRepository, UserRepository userRepository, RoleRepository roleRepository, WorkingHoursService workingHoursService, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.workingHoursService = workingHoursService;
        this.passwordEncoder = passwordEncoder;
    }

    //ADMIN
    @Transactional
    public Map<String,String> addAdmin(Users newUser){
        Role adminRole = (Role) roleRepository.findByname("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role 'Admin' not found"));
        newUser.setRole(adminRole);
        String password = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
        Map<String, String> response = new HashMap<>();
        response.put("Username", String.valueOf(newUser.getUsername()));
        response.put("Password", password);
        return response;
    }

    @Transactional
<<<<<<< HEAD
    public Map<String,String> updateAdmin(Long id, Users updatedUser) {
        Users existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user with id " + updatedUser.getId() + " not found"));
=======
    public Users updateAdmin(Users updatedUser) {
        Users existingUser = userRepository.findById(updatedUser.getId()).orElseThrow(() ->new userNotFoundException("user with id " + updatedUser.getId() + " not found"));
>>>>>>> parent of b1a71f2 (some enhancements & seka Redis)
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setGender(updatedUser.getGender());
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
            Map<String, String> response = new HashMap<>();
            response.put("status", "Success");
            response.put("message", "User details updated successfully");
            return response;
        }
        return null;
    }

    @Transactional
    public Users getUserById(long id) {
<<<<<<< HEAD
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
=======
        Optional<Users> user= userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        else{
            throw new userNotFoundException("user with ID " + id + " not found");
        }

>>>>>>> parent of b1a71f2 (some enhancements & seka Redis)
    }

    @Transactional
    public void deleteAdmin(Users adminToDelete) {
        userRepository.delete(adminToDelete);
    }

    @Transactional
    public List<Users> getAllAdmins() {
        List<Users> admins = userRepository.findByRoleName("ADMIN");
        if (admins.isEmpty()) {
            throw new UserNotFoundException("No admins found.");
        }
        return admins;
    }

    @Transactional
    public List<Users> getAllUsers(){
        List<Users> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }
        return users;
    }


    //DOCTOR
    @Transactional
    public Map<String,String> addDoctor(Doctor newDoctor) {
        Role doctorRole = (Role) roleRepository.findByname("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Role 'Doctor' not found"));
        newDoctor.getUser().setRole(doctorRole);
        String password = UUID.randomUUID().toString();
        newDoctor.getUser().setPassword(passwordEncoder.encode(password));
        Users savedUser = userRepository.save(newDoctor.getUser());
        newDoctor.setUser(savedUser);
        newDoctor.setId(savedUser.getId());
        doctorRepository.save(newDoctor);
        Map<String,String> response = new HashMap<>();
        response.put("Username", newDoctor.getUser().getUsername());
        response.put("Password", password);
        return response;
    }

    @Transactional
    public Map<String, String> updateDoctor(Long doctorId, Doctor updatedDoctor) {
        Doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        Users existingUser = existingDoctor.getUser();
        Users updatedUser = updatedDoctor.getUser();

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setAge(updatedUser.getAge());

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        existingDoctor.setSpecialty(updatedDoctor.getSpecialty());
        existingDoctor.setExperienceYears(updatedDoctor.getExperienceYears());
        workingHoursService.updateWorkingHours(existingDoctor, updatedDoctor.getWorkingHours());
        Map<String, String> response = new HashMap<>();
        response.put("status", "Success");
        response.put("message", "User details updated successfully");
        return response;
    }


    @Transactional
    public void deleteDoctor(Doctor deletedDoctor) {
        doctorRepository.delete(deletedDoctor);
    }

    @Transactional
    public List<Doctor> getAllDoctor() {
        List<Doctor> doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) {
            throw new DoctorNotFoundException("No doctors found.");
        }
        return doctors;    }

    @Transactional
    public Doctor getDoctorById(long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + id + " not found"));
    }


    //PATIENT
    @Transactional
    public Map<String,String> addPatient(Patient newPatient) {
        Role patientRole = (Role) roleRepository.findByname("PATIENT")
                .orElseThrow(() -> new RuntimeException("Role 'Patient' not found"));

        newPatient.getUser().setRole(patientRole);
        String password = UUID.randomUUID().toString();
        newPatient.getUser().setPassword(passwordEncoder.encode(password));
        Users savedUser = userRepository.save(newPatient.getUser());
        newPatient.setUser(savedUser);
        newPatient.setId(savedUser.getId());
        patientRepository.save(newPatient);
        Map<String, String> response = new HashMap<>();
        response.put("Username", newPatient.getUser().getUsername());
        response.put("Password", password);
        return response;
    }

    @Transactional
    public Map<String, String> updatePatient(Long id, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found"));

        Users existingUser = existingPatient.getUser();
        Users updatedUser = updatedPatient.getUser();

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setAge(updatedUser.getAge());

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        existingPatient.setMedicalHistory(updatedPatient.getMedicalHistory());

        Map<String, String> response = new HashMap<>();
        response.put("status", "Success");
        response.put("message", "User details updated successfully");
        return response;
    }


    @Transactional
    public void deletePatient(Patient deletedPatient) {
        patientRepository.delete(deletedPatient);
    }

    @Transactional
    public List<Patient> getAllPatient() {
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            throw new PatientNotFoundException("No Patients found.");
        }
        return patients;
    }

    @Transactional
    public Patient getPatientById(long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found"));
    }
    
    @Transactional
    public Users getUserByUsername(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username " + userName + " not found"));
    }
}
