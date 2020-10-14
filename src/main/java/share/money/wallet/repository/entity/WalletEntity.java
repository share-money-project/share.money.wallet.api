package share.money.wallet.repository.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "wallet")
public class WalletEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "reserved", nullable = false)
    private Double reserved = 0.0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getReserved() {
        return reserved;
    }

    public void setReserved(Double reserved) {
        this.reserved = reserved;
    }
}
