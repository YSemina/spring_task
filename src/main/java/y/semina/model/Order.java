package y.semina.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import y.semina.constant.Status;
import y.semina.view.Views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @Column(name = "product_name")
    @JsonView(Views.UserDetails.class)
    private List<String> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JsonView(Views.UserDetails.class)
    private Status status;

    @JsonView(Views.UserDetails.class)
    private BigDecimal amount;

    public Order(User user, Status status, BigDecimal amount) {
        this.user = user;
        this.status = status;
        this.amount = amount;
    }

    public void addProduct(String product) {
        products.add(product);
    }

}
