package bigprojects.dto;

public class VerifyOtpRequest {
    private String email;
    private String otp;

    // Constructor
    public VerifyOtpRequest() {}

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
