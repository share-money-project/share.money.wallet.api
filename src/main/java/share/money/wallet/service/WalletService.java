package share.money.wallet.service;

import share.money.wallet.controller.model.response.OperationStatusModel;
import share.money.wallet.service.dto.WalletDto;

public interface WalletService {
    WalletDto findByUserId(String userId) throws BusinessException;

    WalletDto topUpAccountByUserId(String userId, Double amount);

    WalletDto createWallet(WalletDto walletDto);

    OperationStatusModel deleteWallet(String userId);

}
