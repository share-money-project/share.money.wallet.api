package share.money.wallet.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import share.money.wallet.controller.model.response.OperationStatusModel;
import share.money.wallet.repository.WalletRepository;
import share.money.wallet.repository.entity.WalletEntity;
import share.money.wallet.service.BusinessException;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;
import share.money.wallet.shared.ModelMapper;


@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public WalletDto findByUserId(String userId) throws BusinessException {
        WalletEntity walletEntity = walletRepository.findByUserId(userId);
        if (walletEntity == null) throw new BusinessException(String.format("Wallet account with such userId [%s] does not exist", userId));
        return ModelMapper.map(walletEntity, WalletDto.class);
    }

    @Override
    public WalletDto topUpAccountByUserId(String userId, Double amount) {
        WalletEntity entity = walletRepository.findByUserId(userId);
        if (entity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", userId));
        entity.setTotal(entity.getTotal() + amount);
        WalletEntity walletEntity = walletRepository.save(entity);
        return ModelMapper.map(walletEntity, WalletDto.class);
    }

    @Override
    public WalletDto createWallet(WalletDto walletDto) {
        WalletEntity entity = walletRepository.findByUserId(walletDto.getUserId());

        if (entity != null) throw new RuntimeException(String.format("Record with such id [%s] already exists", walletDto.getUserId()));

        WalletEntity walletEntity = ModelMapper.map(walletDto, WalletEntity.class);
        WalletEntity saved = walletRepository.save(walletEntity);
        return ModelMapper.map(saved, WalletDto.class);
    }

    @Override
    public OperationStatusModel deleteWallet(String userId) {
        WalletEntity entity = walletRepository.findByUserId(userId);
        if (entity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", userId));
        if (entity.getTotal() > 0.0) return new OperationStatusModel("Failed. Not zero-sum account cant' be deleted", "Delete");
        walletRepository.delete(entity);
        return new OperationStatusModel("Success", "Delete");
    }
}
