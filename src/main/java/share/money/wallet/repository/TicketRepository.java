package share.money.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import share.money.wallet.repository.entity.TicketEntity;

@Repository
public interface TicketRepository extends CrudRepository<TicketEntity, Long> {
}