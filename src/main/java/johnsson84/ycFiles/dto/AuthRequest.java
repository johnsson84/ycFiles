package johnsson84.ycFiles.dto;
import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}