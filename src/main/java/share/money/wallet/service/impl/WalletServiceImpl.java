package share.money.wallet.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import share.money.wallet.repository.WalletRepository;
import share.money.wallet.repository.entity.WalletEntity;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public WalletDto findByUserId(String userId) {
        WalletEntity walletEntity = walletRepository.findByUserId(userId);
        if (walletEntity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", userId));
        return new ModelMapper().map(walletEntity, WalletDto.class);
    }

    @Override
    public WalletDto topUpAccountByUserId(String userId, Double amount) {
        WalletEntity entity = walletRepository.findByUserId(userId);
        if (entity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", userId));
        entity.setTotal(entity.getTotal() + amount);
        WalletEntity walletEntity = walletRepository.save(entity);
        return new ModelMapper().map(walletEntity, WalletDto.class);
    }

    @Override
    public WalletDto createWallet(WalletDto walletDto) {
        WalletEntity entity = walletRepository.findByUserId(walletDto.getUserId());

        if (entity != null) throw new RuntimeException(String.format("Record with such id [%s] already exists", walletDto.getUserId()));

        WalletEntity walletEntity = new ModelMapper().map(walletDto, WalletEntity.class);
        WalletEntity saved = walletRepository.save(walletEntity);
        return new ModelMapper().map(saved, WalletDto.class);
    }
}
