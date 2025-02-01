package johnsson84.ycFiles.dto;

import jakarta.validation.constraints.NotBlank;
import johnsson84.ycFiles.models.Role;

import java.util.Set;

public class RegisterRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Set<Role> roles;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
