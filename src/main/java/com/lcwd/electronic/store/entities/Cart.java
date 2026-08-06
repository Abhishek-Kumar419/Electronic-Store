package com.lcwd.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;

//import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    private String cartId;
    private Date  createdAt;

    @OneToOne
    private User user;

    //mapping cart-items
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> items = new ArrayList<>();


}
