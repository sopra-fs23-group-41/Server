package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

public class UserPostDTO {

    private String username;
  private String password;
  private LocalDate birthdate;


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}
