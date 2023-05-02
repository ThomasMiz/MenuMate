package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.Image;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateRestaurantForm {
    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 120)
    private String address;

    @NotNull
    @Size(max = 1024)
    private String description;

    @Image
    private MultipartFile logo;

    @Image
    private MultipartFile portrait1;

    @Image
    private MultipartFile portrait2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getLogo() {
        return logo;
    }

    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }

    public MultipartFile getPortrait1() {
        return portrait1;
    }

    public void setPortrait1(MultipartFile portrait1) {
        this.portrait1 = portrait1;
    }

    public MultipartFile getPortrait2() {
        return portrait2;
    }

    public void setPortrait2(MultipartFile portrait2) {
        this.portrait2 = portrait2;
    }
}