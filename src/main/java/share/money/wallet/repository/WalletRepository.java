package share.money.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import share.money.wallet.repository.entity.WalletEntity;

@Repository
public interface WalletRepository extends CrudRepository<WalletEntity, Long> {

    WalletEntity findByUserId(String userId);
}