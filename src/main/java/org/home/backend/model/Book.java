package org.home.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString()
@Entity
@Table(name="BOOK", schema = "DATA")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "YEAR")
    private Integer year;
    @Column(name = "AUTHOR")
    private String author;
    @ManyToOne
    private BookSage sage;
    @Column(name = "ORDER_SAGE")
    private Integer orderSage;
    @Column(name = "FILE_YEAR")
    private Integer fileYear;
    @Column(name = "DIGITAL")
    private Boolean digital;
    @Column(name = "NOTES")
    private String notes;
    @CreationTimestamp
    @Column(name = "CREATION_DATE")
    @Temporal (TemporalType.TIMESTAMP)
    private Date creationDate = new Date();


    public Book(String title) {
        this.title = title;
    }

    public Book(BookDAO bookDAO){
        this.id = bookDAO.getId();
        this.title = bookDAO.getTitle();
        this.year = bookDAO.getYear();
        this.author = bookDAO.getAuthor();
        BookSage sageDAO = null;
        if(bookDAO.getSageId() != null && bookDAO.getSageId() != 0) {
            sageDAO = new BookSage(bookDAO.getSageId());
        }
        this.sage = sageDAO;
        this.orderSage = bookDAO.getOrderSage();
        this.fileYear = bookDAO.getFileYear();
        this.digital = bookDAO.getDigital();
        this.notes = bookDAO.getNotes();
        this.creationDate = (bookDAO.getCreationDate() != null)? bookDAO.getCreationDate(): new Date();
    }

}
