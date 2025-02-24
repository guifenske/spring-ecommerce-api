package com.ecommerce.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product    {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 3, max = 100, message = "Product name must be within 3 - 100 characters")
    private String name;

    @Size(min = 6, max = 10000, message = "Product description must be within 6 - 10000 characters")
    private String description;

    private String image;

    @NotNull(message = "Product quantity cannot be Null")
    private Integer quantity;

    @NotNull(message = "Product price cannot be Null")
    private Double price;

    private Double discount;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
