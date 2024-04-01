package org.home.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDAO {

    private long id;
    private String title;
    private Integer year;
    private String author;
    private Integer sageId;
    private Integer orderSage;
    private Integer fileYear;
    private Boolean digital;
    private String notes;
    private Date creationDate = new Date();
}
