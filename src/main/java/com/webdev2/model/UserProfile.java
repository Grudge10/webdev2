package com.webdev2.model;

import jakarta.persistence.*;

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    private String phone;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private User user;

    public UserProfile() {
    }

    public UserProfile(String bio, String phone) {
        this.bio = bio;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
