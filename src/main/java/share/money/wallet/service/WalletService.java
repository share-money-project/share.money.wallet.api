package share.money.wallet.service;

import share.money.wallet.service.dto.WalletDto;

public interface WalletService {
    WalletDto findByUserId(String userId);

    WalletDto topUpAccountByUserId(String userId, Double amount);

    WalletDto createWallet(WalletDto walletDto);
}
