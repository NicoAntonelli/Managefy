package nicoAntonelli.managefy.entities;

import org.springframework.lang.Nullable;
import java.util.Date;

public class Client {
    private Long id;
    private String name;
    private String description;
    private String mail;
    private String phone;
    @Nullable
    private Date detelionDate;

    public Client() { }

    public Client(Long id, String name, String description, String mail,
                  String phone, @Nullable Date detelionDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.detelionDate = detelionDate;
    }

    public Client(String name, String description, String mail,
                  String phone, @Nullable Date detelionDate) {
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.detelionDate = detelionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Nullable
    public Date getDetelionDate() {
        return detelionDate;
    }

    public void setDetelionDate(@Nullable Date detelionDate) {
        this.detelionDate = detelionDate;
    }
}
