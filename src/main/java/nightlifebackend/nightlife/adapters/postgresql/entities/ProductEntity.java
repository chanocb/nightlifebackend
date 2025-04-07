package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Product;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.BeanUtils;

@Builder
@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double price;
    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;

    public ProductEntity(Product product) {
        BeanUtils.copyProperties(product, this);

    }

    public Product toProduct() {
        Product product = new Product();
        BeanUtils.copyProperties(this, product);
        return product;
    }
}
