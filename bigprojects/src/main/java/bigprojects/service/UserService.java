package bigprojects.service;// No package declaration, this class is in the default package
import bigprojects.model.User;
import bigprojects.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public UserService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    // Generate OTP
    public String generateOtp() {
        Random rand = new Random();
        int otp = rand.nextInt(999999 - 100000 + 1) + 100000; // 6-digit OTP
        return String.valueOf(otp);
    }

    // Send OTP to email
    public void sendOtpEmail(String toEmail) {
        String otp = generateOtp();  // Generate a random 6-digit OTP
        User user = userRepository.findByEmail(toEmail);

        if (user != null) {
            user.setOtp(otp);  // Save OTP in database
            userRepository.save(user);  // Save the updated user object with OTP

            // Prepare email message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp);

            emailSender.send(message);  // Send the email
        }
    }
    // Add this method inside UserService class
    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }



    // Verify OTP
    public boolean verifyOtp(String email, String enteredOtp) {
        User user = userRepository.findByEmail(email);
        return user != null && user.getOtp().equals(enteredOtp);  // Validate OTP from DB
    }

    // Sign up a new user
    public String signup(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "User already exists";
        }
        userRepository.save(user);
        return "User signed up successfully";
    }

    // User login
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return "Login successful";
        }
        return "Invalid email or password";
    }
}
