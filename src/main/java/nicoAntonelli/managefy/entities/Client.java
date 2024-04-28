package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @SequenceGenerator(
            name = "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )
    @Column(updatable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String mail;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = true)
    private Date deletionDate;

    public Client() { }

    public Client(Long id, String name, String description, String mail,
                  String phone, Date detelionDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.deletionDate = detelionDate;
    }

    public Client(String name, String description, String mail,
                  String phone, Date detelionDate) {
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.deletionDate = detelionDate;
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

    public Date getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", deletionDate=" + deletionDate +
                '}';
    }
}
