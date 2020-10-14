package share.money.wallet.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import share.money.wallet.config.SpringRabbitConfig;
import share.money.wallet.controller.model.response.OperationStatusModel;
import share.money.wallet.externalservice.dto.TicketApprovedDto;
import share.money.wallet.externalservice.dto.TicketCreatedDto;
import share.money.wallet.externalservice.dto.TicketReservedDto;
import share.money.wallet.repository.TicketRepository;
import share.money.wallet.repository.WalletRepository;
import share.money.wallet.repository.entity.TicketEntity;
import share.money.wallet.repository.entity.WalletEntity;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;
import share.money.wallet.shared.ModelMapper;
import share.money.wallet.shared.TicketStatus;

import javax.transaction.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public WalletDto findByUserId(String userId) {
        WalletEntity walletEntity = walletRepository.findByUserId(userId);
        if (walletEntity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", userId));
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

    @Override
    public void checkTicketCreated(TicketCreatedDto createdDto) {
        WalletEntity walletEntity = walletRepository.findByUserId(createdDto.getUserId());
        if (walletEntity == null) throw new RuntimeException(String.format("Wallet account with such userId [%s] does not exist", createdDto.getUserId()));
        if (walletEntity.getTotal() < createdDto.getAmount()) throw new RuntimeException(String.format("Wallet account with such userId does not have enough money [%s]", createdDto.getAmount()));

        TicketEntity ticketEntity = ModelMapper.map(createdDto, TicketEntity.class);
        ticketEntity.setTicketStatus(TicketStatus.TICKET_APPROVED.val());
        ticketRepository.save(ticketEntity);

        TicketApprovedDto ticketApprovedDto = ModelMapper.map(ticketEntity, TicketApprovedDto.class);
        amqpTemplate.convertAndSend(SpringRabbitConfig.EXCHANGE_TICKET, SpringRabbitConfig.KEY_TICKET_APPROVE, ticketApprovedDto);

        TicketReservedDto ticketReservedDto = reserveMoney(ticketApprovedDto);
        amqpTemplate.convertAndSend(SpringRabbitConfig.EXCHANGE_TICKET, SpringRabbitConfig.KEY_TICKET_RESERVE, ticketReservedDto);
    }

    @Override
    public TicketReservedDto reserveMoney(TicketApprovedDto ticketApprovedDto) {

        WalletEntity fromBase = walletRepository.findByUserId(ticketApprovedDto.getUserId());
        fromBase.setReserved(ticketApprovedDto.getAmount());
        WalletEntity updatedEntity = walletRepository.save(fromBase);
        TicketReservedDto reservedDto = ModelMapper.map(ticketApprovedDto, TicketReservedDto.class);
        reservedDto.setTicketStatus(TicketStatus.TICKET_RESERVED.val());
        reservedDto.setAmount(updatedEntity.getTotal());
        return reservedDto;
    }
}
