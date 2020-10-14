package share.money.wallet.service;

import share.money.wallet.controller.model.response.OperationStatusModel;
import share.money.wallet.externalservice.dto.TicketApprovedDto;
import share.money.wallet.externalservice.dto.TicketCreatedDto;
import share.money.wallet.externalservice.dto.TicketReservedDto;
import share.money.wallet.service.dto.WalletDto;

public interface WalletService {
    WalletDto findByUserId(String userId);

    WalletDto topUpAccountByUserId(String userId, Double amount);

    WalletDto createWallet(WalletDto walletDto);

    OperationStatusModel deleteWallet(String userId);

    void checkTicketCreated(TicketCreatedDto createdDto);

    TicketReservedDto reserveMoney(TicketApprovedDto ticketApprovedDto);
}
